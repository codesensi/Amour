package cn.codesensi.amour.service.impl;

import cn.codesensi.amour.common.core.BasePage;
import cn.codesensi.amour.common.enums.BaseEnum;
import cn.codesensi.amour.common.enums.ConfigKeyEnum;
import cn.codesensi.amour.common.enums.MessageAuditStatusEnum;
import cn.codesensi.amour.common.enums.NoticeBizTypeEnum;
import cn.codesensi.amour.common.exception.BusinessException;
import cn.codesensi.amour.common.util.Ip2regionUtil;
import cn.codesensi.amour.common.util.IpUtil;
import cn.codesensi.amour.common.util.ServletUtil;
import cn.codesensi.amour.mapper.PortalMessageMapper;
import cn.codesensi.amour.mapper.SysNoticeMapper;
import cn.codesensi.amour.model.converter.MessageConverter;
import cn.codesensi.amour.model.dto.*;
import cn.codesensi.amour.model.entity.PortalMessage;
import cn.codesensi.amour.model.entity.SysConfig;
import cn.codesensi.amour.model.entity.SysNotice;
import cn.codesensi.amour.service.MessageService;
import cn.codesensi.amour.service.QqInfoService;
import cn.codesensi.amour.service.SysConfigService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.codesensi.amour.model.entity.table.PortalMessageTableDef.PORTAL_MESSAGE;
import static cn.codesensi.amour.model.entity.table.SysNoticeTableDef.SYS_NOTICE;

/**
 * 留言 Service 实现 —— 门户留言簿的提交与下发、管理端审核与删除共用。
 * <p>
 * 实体仅在层内流转：查询结果经转换器映射为 {@code MessageDTO} 后返回，
 * 提交在内部完成头像快照、IP 归属地解析与审核状态组装。
 *
 * @author codesensi
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final PortalMessageMapper portalMessageMapper;
    private final MessageConverter messageConverter;
    private final QqInfoService qqInfoService;
    private final SysConfigService sysConfigService;
    private final SysNoticeMapper sysNoticeMapper;

    /**
     * {@inheritDoc}
     * <p>
     * 上墙口径固定为「仅审核通过」（auditStatus 强制过滤为 1，管理端维护口径之外的安全边界）；
     * 排序为 create_time 降序 → id 降序（新留言在前）。
     *
     * @param page 分页参数
     * @return 审核通过的留言条目 DTO 分页结果
     */
    @Override
    public Page<MessageDTO> pagePortal(BasePage page) {
        return doPage(page, null, MessageAuditStatusEnum.APPROVED.getCode());
    }

    /**
     * {@inheritDoc}
     * <p>
     * 头像快照复用 {@link QqInfoService#getQqInfo}（自带缓存与降级链路），仅取头像地址，
     * 昵称以访客填写为准不覆盖；取不到头像时入库为空，由门户前端本地兜底图兜底。
     * 留言落库即待审核，审核通过后方可在门户下发；IP 取自当前请求
     * （代理头可信开关读取方式对齐 {@code RateLimitAspect} 惯例），归属地由 ip2region 离线解析。
     * 落库后同步写入一条通知中心通知（超管/主角经通知中心直接审批），与留言同一事务。
     *
     * @param submitDTO 提交留言参数 DTO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void submit(MessageSubmitDTO submitDTO) {
        QqInfoResultDTO qqInfo = qqInfoService.getQqInfo(submitDTO.getQq());
        PortalMessage entity = messageConverter.toEntity(submitDTO);
        entity.setAvatar(ObjUtil.isNull(qqInfo) ? null : StrUtil.emptyToNull(qqInfo.getAvatarUrl()));
        entity.setIp(currentIp());
        entity.setRegion(Ip2regionUtil.search(entity.getIp()));
        entity.setAuditStatus(MessageAuditStatusEnum.PENDING.getCode());
        portalMessageMapper.insert(entity);

        // 通知中心写入：biz 关联留言，审批落定后由 {@link #audit} 消化
        SysNotice notice = new SysNotice()
                .setTitle("收到新留言")
                .setContent(StrUtil.format("「{}」给你留言：{}", submitDTO.getName(), submitDTO.getText()));
        notice.setBizType(NoticeBizTypeEnum.MESSAGE_AUDIT.getCode());
        notice.setBizId(entity.getId());
        sysNoticeMapper.insert(notice);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 昵称为模糊匹配，审核状态为精确匹配，条件缺省时自动忽略；
     * 排序为 create_time 降序 → id 降序；逻辑删除（del_flag）由全局配置自动追加过滤。
     *
     * @param pageDTO 分页查询参数 DTO
     * @return 留言条目 DTO 分页结果
     */
    @Override
    public Page<MessageDTO> pageAdmin(MessagePageDTO pageDTO) {
        return doPage(pageDTO, pageDTO.getNickname(), pageDTO.getAuditStatus());
    }

    /**
     * {@inheritDoc}
     * <p>
     * 审核状态仅允许通过/驳回（待审核为提交后的初始态，不允许回设）；
     * 对齐 love-photo 等既有单列状态更新惯例：存在性校验 + 同状态幂等返回，仅覆盖审核状态字段。
     * 审核动作落定后同步消化该留言关联的审批通知（通知中心不再展示已处理通知），与审核同一事务。
     *
     * @param auditDTO 留言审核参数 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(MessageAuditDTO auditDTO) {
        MessageAuditStatusEnum status = BaseEnum.fromCode(MessageAuditStatusEnum.class, auditDTO.getAuditStatus());
        if (ObjUtil.isNull(status) || MessageAuditStatusEnum.PENDING == status) {
            throw new BusinessException("审核状态不合法");
        }
        PortalMessage message = QueryChain.of(portalMessageMapper)
                .select(PORTAL_MESSAGE.ID, PORTAL_MESSAGE.AUDIT_STATUS)
                .where(PORTAL_MESSAGE.ID.eq(auditDTO.getId()))
                .one();
        if (ObjUtil.isNull(message)) {
            throw new BusinessException("留言不存在");
        }

        // 审核状态一致时幂等返回
        if (auditDTO.getAuditStatus().equals(message.getAuditStatus())) {
            return;
        }

        PortalMessage entity = new PortalMessage();
        entity.setId(auditDTO.getId());
        entity.setAuditStatus(auditDTO.getAuditStatus());
        portalMessageMapper.update(entity);

        // 消化关联的审批通知（无关联或已消化时跳过；逻辑删除由全局配置承接）
        SysNotice notice = QueryChain.of(sysNoticeMapper)
                .select(SYS_NOTICE.ID)
                .where(SYS_NOTICE.BIZ_TYPE.eq(NoticeBizTypeEnum.MESSAGE_AUDIT.getCode()))
                .and(SYS_NOTICE.BIZ_ID.eq(auditDTO.getId()))
                .one();
        if (ObjUtil.isNotNull(notice)) {
            sysNoticeMapper.deleteById(notice.getId());
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * 对齐 role/dictType 既有 delete 惯例：任一 id 不存在时整批失败。
     *
     * @param ids 留言ID集合
     */
    @Override
    public void delete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<Long> distinctIds = ids.stream().distinct().toList();
        List<Long> existingIds = QueryChain.of(portalMessageMapper)
                .select(PORTAL_MESSAGE.ID)
                .where(PORTAL_MESSAGE.ID.in(distinctIds))
                .listAs(Long.class);
        if (existingIds.size() < distinctIds.size()) {
            throw new BusinessException("留言不存在");
        }
        portalMessageMapper.deleteBatchByIds(distinctIds);
    }

    /**
     * 分页查询内核 —— 门户与管理端分页共用的条件装配与分页执行。
     * <p>
     * 昵称模糊匹配、审核状态精确匹配，条件缺省时自动忽略；
     * 排序为 create_time 降序 → id 降序；逻辑删除（del_flag）由全局配置自动追加过滤。
     *
     * @param page        分页参数
     * @param nickname    昵称（模糊匹配，可空）
     * @param auditStatus 审核状态过滤（可空；门户固定传 approved，管理端传筛选值或 null 查全量）
     * @return 留言条目 DTO 分页结果
     */
    private Page<MessageDTO> doPage(BasePage page, String nickname, String auditStatus) {
        Page<PortalMessage> entityPage = QueryChain.of(portalMessageMapper)
                .where(PORTAL_MESSAGE.NICKNAME.like(nickname, StrUtil::isNotBlank))
                .and(PORTAL_MESSAGE.AUDIT_STATUS.eq(auditStatus, ObjUtil::isNotNull))
                .orderBy(PORTAL_MESSAGE.CREATE_TIME, false)
                .orderBy(PORTAL_MESSAGE.ID, false)
                .page(Page.of(page.getPageNumber(), page.getPageSize()));
        return messageConverter.toPageDTO(entityPage);
    }

    /**
     * 解析当前请求的客户端 IP：代理头可信开关（sys_config trust-proxy-headers）热更新，
     * 读取方式对齐 {@code RateLimitAspect} 惯例；直连部署取连接对端地址。
     *
     * @return 真实客户端 IP；无法解析时返回 {@code unknown}
     */
    private String currentIp() {
        SysConfig trustSwitch = sysConfigService.oneByKey(ConfigKeyEnum.TRUST_PROXY_HEADERS.getCode());
        boolean trustProxyHeaders = ObjUtil.isNotNull(trustSwitch) && Boolean.parseBoolean(trustSwitch.getConfigValue());
        return IpUtil.getIpAddr(ServletUtil.getRequest(), trustProxyHeaders);
    }

}
