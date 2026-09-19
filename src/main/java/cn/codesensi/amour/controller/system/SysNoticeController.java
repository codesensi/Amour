package cn.codesensi.amour.controller.system;

import cn.codesensi.amour.common.annotation.ApiResponseBody;
import cn.codesensi.amour.common.annotation.Log;
import cn.codesensi.amour.common.enums.LogTypeEnum;
import cn.codesensi.amour.model.converter.NoticeConverter;
import cn.codesensi.amour.model.dto.NoticeDTO;
import cn.codesensi.amour.model.dto.NoticeReadDTO;
import cn.codesensi.amour.model.request.NoticeReadRequest;
import cn.codesensi.amour.model.response.NoticeResponse;
import cn.codesensi.amour.service.SysNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知中心相关接口 前端控制器（登录态;通知由业务事件触发写入，不提供人工发布入口）。
 *
 * @author codesensi
 * @since 1.0
 */
@RestController
@ApiResponseBody
@RequiredArgsConstructor
@RequestMapping("/sys/notice")
public class SysNoticeController {

    private final SysNoticeService sysNoticeService;

    private final NoticeConverter noticeConverter;

    /**
     * 通知列表（登录态；通知中心数据源，按创建时间倒序，每条附带当前用户的已读标记）。
     *
     * @param limit 最大返回条数，缺省 20
     * @return 通知条目列表
     */
    @GetMapping("/list")
    public List<NoticeResponse> list(@RequestParam(required = false, defaultValue = "20") Integer limit) {
        List<NoticeDTO> noticeList = sysNoticeService.list(limit);
        return noticeConverter.toResponseList(noticeList);
    }

    /**
     * 标记通知已读（noticeIds 为空或缺失时标记全部未读；按唯一键幂等，重复提交不报错）。
     *
     * @param request 标记已读请求参数（noticeIds 雪花ID字符串化传输；请求体可缺省）
     */
    @Log(module = "通知中心", operation = "标记通知已读", type = LogTypeEnum.UPDATE)
    @PostMapping("/read")
    public void read(@RequestBody(required = false) NoticeReadRequest request) {
        NoticeReadDTO noticeReadDTO = noticeConverter.toReadDTO(request);
        sysNoticeService.read(noticeReadDTO);
    }

}
