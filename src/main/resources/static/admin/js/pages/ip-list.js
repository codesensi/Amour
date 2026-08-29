/**
 * IP/拉黑 页面模块（由 pjax 页面调度加载）。
 */
import { navigate as pjaxNavigate } from '/assets/common/pjax.js';

export function init() {
    // 删除/解封 IP：原站跳转 delip.php?id=x 执行删除，现阶段后端接口未实现，改为演示提示
    function del(id, State) {
        if (confirm('您确认要删除IP为 ' + State + ' 吗')) {
            // 现阶段后端接口未实现，mock 演示提示；接口实现后恢复为真实请求（delip.php）
            toastr["warning"]("演示数据：删除/解封功能暂未开放！", "Like_Girl");
        }
    }

    // 新增 IP 封禁入口：原站跳转 ipSet.php（新增页），现阶段该页面未实现，改为演示提示
    $(".fabu").on("click", function () {
        // 现阶段后端接口未实现，mock 演示提示；接口实现后恢复为真实跳转（ipSet.php）
        toastr["info"]("演示数据：新增 IP 封禁页面暂未开放！", "Like_Girl");
    });

    $("#userPost").click(function () {
        var Webanimation = $("select[name='Webanimation']").val();
        var userName = $("input[name='userName']").val();
        var userQQ = $("input[name='userQQ']").val();
        var adminName = $("input[name='adminName']").val();
        var pw = $("input[name='pw']").val();
        var SCode = $("input[name='SCode']").val();
        var cssCon = $("textarea[name='cssCon']").val();
        var headCon = $("textarea[name='headCon']").val();
        var footerCon = $("textarea[name='footerCon']").val();
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（userPost.php）
        toastr["success"]("更新登录信息成功！", "Like_Girl");
        toastr["success"]("更新全局信息成功", "Like_Girl");
        toastr["success"]("更新自定义内容成功", "Like_Girl");
    })
    $("#adminPost").click(function () {
        var title = $("input[name='title']").val();
        var logo = $("input[name='logo']").val();
        var writing = $("input[name='writing']").val();
        var WebBlur = $("select[name='WebBlur']").val();
        var WebPjax = $("select[name='WebPjax']").val();

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（adminPost.php）
        toastr["success"]("基本信息修改成功！", "Like_Girl");
        toastr["success"]("开关设置成功！", "Like_Girl");
    })
    $("#loveadminPost").click(function () {
        var boy = $("input[name='boy']").val();
        var girl = $("input[name='girl']").val();
        var boyimg = $("input[name='boyimg']").val();
        var girlimg = $("input[name='girlimg']").val();
        var startTime = $("input[name='startTime']").val();

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（loveadminPost.php）
        toastr["success"]("情侣信息修改成功！", "Like_Girl");
    })

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
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（CardadminPost.php）
        toastr["success"]("卡片信息修改成功！", "Like_Girl");
    })

    $("#leavPPost").click(function () {
        var jiequ = $("input[name='jiequ']").val();
        var lanjiezf = $("textarea[name='lanjiezf']").val();
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（leavPPost.php）
        toastr["success"]("留言设置修改成功！", "Like_Girl");
    })

    $("#littleupda").click(function () {
        var id = $("input[name='id']").val();
        var articletitle = $("input[name='articletitle']").val();
        var articletext = $("textarea[name='articletext']").val();

        // 保留原有表单校验逻辑
        if ($.trim(articletitle) === '') {
            toastr["error"]("文章标题不能为空！", "Like_Girl");
            return false;
        }

        if ($.trim(articletext) === '') {
            toastr["error"]("文章内容不能为空！", "Like_Girl");
            return false;
        }

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（littleupda.php）
        toastr["success"]("文章修改成功！", "Like_Girl");
        $('#littleupda').text('修改中...');
        $("#littleupda").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/little-set.html'); }, 1000);
    })
    $("#littleAddPost").click(function () {
        var articlename = $("select[name='articlename']").val();
        var articletitle = $("input[name='articletitle']").val();
        var articletext = $("textarea[name='articletext']").val();

        // 保留原有表单校验逻辑
        if ($.trim(articlename) === '') {
            toastr["error"]("发布人不能为空！", "Like_Girl");
            return false;
        }

        if ($.trim(articletitle) === '') {
            toastr["error"]("文章标题不能为空！", "Like_Girl");
            return false;
        }

        if ($.trim(articletext) === '') {
            toastr["error"]("文章内容不能为空！", "Like_Girl");
            return false;
        }

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（littleAddPost.php）
        toastr["success"]("新增文章成功！", "Like_Girl");
        $('#littleAddPost').text('发布中...');
        $("#littleAddPost").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/little-set.html'); }, 1000);
    })
    $("#ImgUpdaPost").click(function () {
        var imgDatd = $("input[name='imgDatd']").val();
        var imgText = $("input[name='imgText']").val();
        var id = $("input[name='id']").val();
        var imgUrl = $("input[name='imgUrl']").val();

        // 保留原有表单校验逻辑
        if ($.trim(imgDatd) === '') {
            toastr["error"]("日期不能为空！", "Like_Girl");
            return false;
        }
        if ($.trim(imgText) === '') {
            toastr["error"]("图片描述不能为空！", "Like_Girl");
            return false;
        }
        if ($.trim(imgUrl) === '') {
            toastr["error"]("图片地址不能为空！", "Like_Girl");
            return false;
        }

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（ImgUpdaPost.php）
        toastr["success"]("相册修改成功！", "Like_Girl");
        $('#ImgUpdaPost').text('修改中...');
        $("#ImgUpdaPost").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/love-img-set.html'); }, 1000);
    })
    $("#ImgAddPost").click(function () {
        var imgDatd = $("input[name='imgDatd']").val();
        var imgText = $("input[name='imgText']").val();
        var imgUrl = $("input[name='imgUrl']").val();

        // 保留原有表单校验逻辑
        if ($.trim(imgDatd) === '') {
            toastr["error"]("日期不能为空！", "Like_Girl");
            return false;
        }
        if ($.trim(imgText) === '') {
            toastr["error"]("图片描述不能为空！", "Like_Girl");
            return false;
        }
        if ($.trim(imgUrl) === '') {
            toastr["error"]("图片地址不能为空！", "Like_Girl");
            return false;
        }

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（ImgAddPost.php）
        // 注：按钮选择器沿用原站代码（原站此处操作的即是 #ImgUpdaPost）
        toastr["success"]("新增相册成功！", "Like_Girl");
        $('#ImgUpdaPost').text('新增中...');
        $("#ImgUpdaPost").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/love-img-set.html'); }, 1000);
    })
    $("#listaddPost").click(function () {
        var eventname = $("input[name='eventname']").val();
        var icon = $("input[name='icon']").val();
        var img = $("input[name='img']").val();

        // 保留原有表单校验逻辑
        if ($.trim(eventname) === '') {
            toastr["error"]("事件标题不能为空！", "Like_Girl");
            return false;
        }

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（listaddPost.php）
        toastr["success"]("新增事件成功！", "Like_Girl");
        $('#listaddPost').text('新增中...');
        $("#listaddPost").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/love-list.html'); }, 1000);
    })
    $("#ipAddPost").click(function () {
        var ipdz = $("input[name='ipdz']").val();
        var bz = $("input[name='bz']").val();

        // 保留原有表单校验逻辑：IP 地址非空校验
        if ($.trim(ipdz) === '') {
            toastr["error"]("IP地址不能为空！", "Like_Girl");
            return false;
        }

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（ipAddPost.php）
        // 注：按钮选择器沿用原站代码（原站此处操作的即是 #listupda）
        toastr["success"]("IP封禁成功！", "Like_Girl");
        $('#listupda').text('提交中...');
        $("#listupda").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/ip-list.html'); }, 1000);
    })
    $("#listupda").click(function () {
        var eventname = $("input[name='eventname']").val();
        var icon = $("input[name='icon']").val();
        var imgurl = $("input[name='imgurl']").val();
        var id = $("input[name='id']").val();

        // 保留原有表单校验逻辑
        if ($.trim(eventname) === '') {
            toastr["error"]("事件标题不能为空！", "Like_Girl");
            return false;
        }

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（listupda.php）
        toastr["success"]("修改事件成功！", "Like_Girl");
        $('#listupda').text('修改中...');
        $("#listupda").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/love-list.html'); }, 1000);
    })
    $("#aboutPost").click(function () {
        var title = $("input[name='title']").val();
        var aboutimg = $("input[name='aboutimg']").val();
        var info1 = $("input[name='info1']").val();
        var info2 = $("input[name='info2']").val();
        var info3 = $("input[name='info3']").val();
        var btn1 = $("input[name='btn1']").val();
        var btn2 = $("input[name='btn2']").val();
        var infox1 = $("input[name='infox1']").val();
        var infox2 = $("input[name='infox2']").val();
        var infox3 = $("input[name='infox3']").val();
        var infox4 = $("input[name='infox4']").val();
        var infox5 = $("input[name='infox5']").val();
        var infox6 = $("input[name='infox6']").val();
        var btnx2 = $("input[name='btnx2']").val();
        var infof1 = $("input[name='infof1']").val();
        var infof2 = $("input[name='infof2']").val();
        var infof3 = $("input[name='infof3']").val();
        var infof4 = $("input[name='infof4']").val();
        var btnf3 = $("input[name='btnf3']").val();
        var infod1 = $("input[name='infod1']").val();
        var infod2 = $("input[name='infod2']").val();
        var infod3 = $("input[name='infod3']").val();
        var infod4 = $("input[name='infod4']").val();
        var infod5 = $("input[name='infod5']").val();

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（aboutPost.php）
        toastr["success"]("修改对话配置成功！", "Like_Girl");
    })

    $(function () {
        // 原站此处会请求 wiki.kikiw.cn 拉取版本公告弹窗数据（loadModalContent），属外部站点依赖，已移除
    })
}
