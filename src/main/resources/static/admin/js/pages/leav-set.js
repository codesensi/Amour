/**
 * 留言管理 页面模块（由 pjax 页面调度加载）。
 */
import { navigate as pjaxNavigate } from '/assets/common/pjax.js';
import { toast } from '/assets/common/toast.js';

export function init() {
    $(function () {
        // 登录成功欢迎提示（仅从登录页跳转过来时展示一次）
        if (sessionStorage.getItem('ADMIN_LOGIN_WELCOME') === '1') {
            sessionStorage.removeItem('ADMIN_LOGIN_WELCOME');
            toast.success('登录成功！欢迎回来~');
        }
    });

    // 删除留言按钮（事件委托）：先弹出原站确认框，确认后给出演示提示，不做真实删除
    document.addEventListener('click', function (e) {
        let el = e.target.closest('.delete-btn');
        if (!el) return;

        e.preventDefault();
        let id = el.dataset.id;
        let content = el.dataset.content;
        del(id, content);
    });

    // 原站确认后跳转 delleav.php?id=x 执行删除；现为演示数据，仅给出提示
    function del(id, text) {
        if (confirm('您确认要删除 ' + text + ' 内容吗')) {
            // 现阶段后端接口未实现，mock 演示提示；接口实现后恢复为真实删除请求
            toast.warning("演示数据：删除功能暂未接入后端", "Like_Girl");
        }
    }

    // “留言相关设置”按钮：原站跳转 /admin/leavP.php 留言设置页（暂未迁移），演示环境仅提示
    $(".fabu").on('click', function () {
        toast.info("演示数据：留言设置页面暂未迁移", "Like_Girl");
    });

    // ==================== 原站公共提交脚本（异步 Ajax 处理）mock 化 ====================
    // 原站以下各表单提交均通过 $.ajax POST 到对应 xxxPost.php；
    // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求。

    $("#userPost").click(function () {
        // 原提交：$.ajax -> userPost.php（登录信息/全局信息/自定义内容）
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("更新登录信息成功！", "Like_Girl");
        toast.success("更新全局信息成功", "Like_Girl");
        toast.success("更新自定义内容成功", "Like_Girl");
    })
    $("#adminPost").click(function () {
        // 原提交：$.ajax -> adminPost.php（基本信息/开关设置）
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("基本信息修改成功！", "Like_Girl");
        toast.success("开关设置成功！", "Like_Girl");
    })
    $("#loveadminPost").click(function () {
        // 原提交：$.ajax -> loveadminPost.php（情侣信息）
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("情侣信息修改成功！", "Like_Girl");
    })

    $("#CardadminPost").click(function () {
        // 原提交：$.ajax -> CardadminPost.php（卡片信息）
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("卡片信息修改成功！", "Like_Girl");
    })

    $("#leavPPost").click(function () {
        // 原提交：$.ajax -> leavPPost.php（留言设置：截取条数/拦截字符）
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("留言设置修改成功！", "Like_Girl");
    })

    $("#littleupda").click(function () {
        var articletitle = $("input[name='articletitle']").val();
        var articletext = $("textarea[name='articletext']").val();

        // 保留原站表单校验逻辑
        if ($.trim(articletitle) === '') {
            toast.error("文章标题不能为空！", "Like_Girl");
            return false;
        }

        if ($.trim(articletext) === '') {
            toast.error("文章内容不能为空！", "Like_Girl");
            return false;
        }

        // 原提交：$.ajax -> littleupda.php（修改文章）
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("文章修改成功！", "Like_Girl");
        $('#littleupda').text('修改中...');
        $("#littleupda").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/little-set.html'); }, 1000);
    })
    $("#littleAddPost").click(function () {
        var articlename = $("select[name='articlename']").val();
        var articletitle = $("input[name='articletitle']").val();
        var articletext = $("textarea[name='articletext']").val();

        // 保留原站表单校验逻辑
        if ($.trim(articlename) === '') {
            toast.error("发布人不能为空！", "Like_Girl");
            return false;
        }

        if ($.trim(articletitle) === '') {
            toast.error("文章标题不能为空！", "Like_Girl");
            return false;
        }

        if ($.trim(articletext) === '') {
            toast.error("文章内容不能为空！", "Like_Girl");
            return false;
        }

        // 原提交：$.ajax -> littleAddPost.php（新增文章）
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("新增文章成功！", "Like_Girl");
        $('#littleAddPost').text('发布中...');
        $("#littleAddPost").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/little-set.html'); }, 1000);
    })
    $("#ImgUpdaPost").click(function () {
        var imgDatd = $("input[name='imgDatd']").val();
        var imgText = $("input[name='imgText']").val();
        var imgUrl = $("input[name='imgUrl']").val();

        // 保留原站表单校验逻辑
        if ($.trim(imgDatd) === '') {
            toast.error("日期不能为空！", "Like_Girl");
            return false;
        }
        if ($.trim(imgText) === '') {
            toast.error("图片描述不能为空！", "Like_Girl");
            return false;
        }
        if ($.trim(imgUrl) === '') {
            toast.error("图片地址不能为空！", "Like_Girl");
            return false;
        }

        // 原提交：$.ajax -> ImgUpdaPost.php（修改相册）
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("相册修改成功！", "Like_Girl");
        $('#ImgUpdaPost').text('修改中...');
        $("#ImgUpdaPost").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/love-img-set.html'); }, 1000);
    })
    $("#ImgAddPost").click(function () {
        var imgDatd = $("input[name='imgDatd']").val();
        var imgText = $("input[name='imgText']").val();
        var imgUrl = $("input[name='imgUrl']").val();

        // 保留原站表单校验逻辑
        if ($.trim(imgDatd) === '') {
            toast.error("日期不能为空！", "Like_Girl");
            return false;
        }
        if ($.trim(imgText) === '') {
            toast.error("图片描述不能为空！", "Like_Girl");
            return false;
        }
        if ($.trim(imgUrl) === '') {
            toast.error("图片地址不能为空！", "Like_Girl");
            return false;
        }

        // 原提交：$.ajax -> ImgAddPost.php（新增相册）
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("新增相册成功！", "Like_Girl");
        $('#ImgUpdaPost').text('新增中...');
        $("#ImgUpdaPost").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/love-img-set.html'); }, 1000);
    })
    $("#listaddPost").click(function () {
        var eventname = $("input[name='eventname']").val();

        // 保留原站表单校验逻辑
        if ($.trim(eventname) === '') {
            toast.error("事件标题不能为空！", "Like_Girl");
            return false;
        }

        // 原提交：$.ajax -> listaddPost.php（新增事件）
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("新增事件成功！", "Like_Girl");
        $('#listaddPost').text('新增中...');
        $("#listaddPost").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/love-list.html'); }, 1000);
    })
    $("#ipAddPost").click(function () {
        var ipdz = $("input[name='ipdz']").val();

        // 保留原站表单校验逻辑
        if ($.trim(ipdz) === '') {
            toast.error("IP地址不能为空！", "Like_Girl");
            return false;
        }

        // 原提交：$.ajax -> ipAddPost.php（IP 封禁）
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("IP封禁成功！", "Like_Girl");
        $('#listupda').text('提交中...');
        $("#listupda").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/ip-list.html'); }, 1000);
    })
    $("#listupda").click(function () {
        var eventname = $("input[name='eventname']").val();

        // 保留原站表单校验逻辑
        if ($.trim(eventname) === '') {
            toast.error("事件标题不能为空！", "Like_Girl");
            return false;
        }

        // 原提交：$.ajax -> listupda.php（修改事件）
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("修改事件成功！", "Like_Girl");
        $('#listupda').text('修改中...');
        $("#listupda").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/love-list.html'); }, 1000);
    })
    $("#aboutPost").click(function () {
        // 原提交：$.ajax -> aboutPost.php（关于页面对话配置，共 24 个字段）
        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("修改对话配置成功！", "Like_Girl");
    })

    // 原站此处会异步加载 wiki.kikiw.cn 的版本信息弹窗（loadModalContent），
    // 属外部站点依赖，已按项目要求整体移除。
}
