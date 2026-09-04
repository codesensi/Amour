package cn.codesensi.amour.controller;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.model.converter.QqInfoConverter;
import cn.codesensi.amour.model.dto.QqInfoResultDTO;
import cn.codesensi.amour.model.request.QqInfoRequest;
import cn.codesensi.amour.model.response.QqInfoResponse;
import cn.codesensi.amour.service.QqInfoService;
import cn.dev33.satoken.annotation.SaIgnore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * QQ 信息查询相关接口 前端控制器
 * <p>
 * 面向全站免登录场景（门户留言、资料展示等），供各端按 QQ 号查询头像与昵称。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
public class QqInfoController {

    private final QqInfoService qqInfoService;

    private final QqInfoConverter qqInfoConverter;

    /**
     * 查询 QQ 信息（免登录）
     * <p>
     * 服务端转发调用头像服务
     *
     * @param request 查询入参（Jakarta Validation 校验，失败走全局 BindException 处理器返回 400）
     * @return 头像地址与昵称；字段可能为空，由前端判空兜底
     */
    @SaIgnore
    @GetMapping("/qq-info")
    public QqInfoResponse qqInfo(@Valid QqInfoRequest request) {
        QqInfoResultDTO qqInfoResultDTO = qqInfoService.getQqInfo(request.getQq());
        return qqInfoConverter.toResponse(qqInfoResultDTO);
    }
}
