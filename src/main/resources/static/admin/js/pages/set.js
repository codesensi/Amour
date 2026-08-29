/**
 * 基本设置 页面模块（由 pjax 页面调度加载）。
 */
export function init() {
    // 基本设置表单提交（原站通过 $.ajax 提交至 adminPost.php，取值逻辑保留）
    $("#adminPost").click(function () {
        var title = $("input[name='title']").val();
        var logo = $("input[name='logo']").val();
        var writing = $("input[name='writing']").val();
        var WebBlur = $("select[name='WebBlur']").val();
        var WebPjax = $("select[name='WebPjax']").val();

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toastr["success"]("基本信息修改成功！", "Like_Girl");
        toastr["success"]("开关设置成功！", "Like_Girl");
    })

    // 情侣配置表单提交（原站通过 $.ajax 提交至 loveadminPost.php，取值逻辑保留）
    $("#loveadminPost").click(function () {
        var boy = $("input[name='boy']").val();
        var girl = $("input[name='girl']").val();
        var boyimg = $("input[name='boyimg']").val();
        var girlimg = $("input[name='girlimg']").val();
        var startTime = $("input[name='startTime']").val();

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toastr["success"]("情侣信息修改成功！", "Like_Girl");
    })

    // 卡片配置&版权配置表单提交（原站通过 $.ajax 提交至 CardadminPost.php，取值逻辑保留）
    $("#CardadminPost").click(function () {
        var bgimg = $("input[name='bgimg']").val();
        var card1 = $("input[name='card1']").val();
        var deci1 = $("input[name='deci1']").val();
        var card2 = $("input[name='card2']").val();
        var deci2 = $("input[name='deci2']").val();
        var card3 = $("input[name='card3']").val();
        var deci3 = $("input[name='deci3']").val();
        var icp = $("input[name='icp']").val();
        var Copyright = $("input[name='Copyright']").val();

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toastr["success"]("卡片信息修改成功！", "Like_Girl");
    })

    // 原站此处的 loadModalContent() 会请求 wiki.kikiw.cn 外部数据，属于外部站点依赖，已按规范移除
}
