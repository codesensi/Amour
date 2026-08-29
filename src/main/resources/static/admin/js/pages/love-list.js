/**
 * 恋爱清单 页面模块（由 pjax 页面调度加载）。
 */
import { navigate as pjaxNavigate } from '/assets/common/pjax.js';
import { toast } from '/assets/common/toast.js';
import { openPhotoViewer } from '/assets/common/photoviewer.js';

/** 内容区内参与灯箱预览的图片选择器（照搬原站的扩展名匹配） */
const IMG_SELECTOR = 'img[src$=jpg],img[src$=gif],img[src$=JPG],img[src$=png],img[src$=jpeg]';

export function init() {
    // 为页面中的图片挂载灯箱预览（layui layer.photos，替换原 hs.expand/spotlight 方案）：
    // 委托绑定到内容区外壳（pjax 不替换外壳，绑定一次即可），DataTables 渲染的图片同样生效
    const page = document.querySelector('.content-page');
    if (page && !page._lightboxBound) {
        page._lightboxBound = true;
        page.addEventListener('click', function (e) {
            const img = e.target.closest(IMG_SELECTOR);
            if (!img) return;
            const imgs = Array.prototype.slice.call(page.querySelectorAll(IMG_SELECTOR));
            openPhotoViewer(imgs.map(function (el) { return { src: el.src, alt: el.alt || '' }; }), imgs.indexOf(img));
        });
    }

    // 新增事件按钮：原站跳转 lovelistAdd.php 新增页；该页面尚未迁移，mock 为演示提示
    $('.js-add-event').on('click', function () {
        toast.error("演示数据：新增事件功能暂未开放！", "Like_Girl");
    });

    // 修改事件按钮：原站跳转 modlist.php?id=x 编辑页；该页面尚未迁移，mock 为演示提示
    $('.js-mock-edit').on('click', function () {
        toast.error("演示数据：修改事件功能暂未开放！", "Like_Girl");
    });

    // 删除事件：原站 confirm 确认后跳转 dellist.php?id=x 真实删除；
    // 现阶段后端接口未实现，mock 处理：确认后仅弹出演示提示，不真正删除数据
    function del(id, eventname) {
        if (confirm('您确认要删除内容为 ' + eventname + ' 的事件吗')) {
            // 原站此处为 location.href = 'dellist.php?id=' + id（真实删除），现按演示数据处理
            toast.error("演示数据：事件《" + eventname + "》未被删除（删除功能暂未开放）！", "Like_Girl");
        }
    }

    // 页面 HTML 中存在 href="javascript:del(...)" 引用，需挂到 window 供全局调用
    window.del = del;

    // 说明：原站该位置为多个后台页面复用的公共提交脚本，
    // 此处仅保留恋爱清单页相关的两个提交处理（新增事件 / 修改事件），
    // 其余页面（基本设置/相册/文章等）的提交逻辑为复用代码，已按需精简。
    // 新增事件提交：原站为 $.ajax({url: "listaddPost.php", ...})
    $("#listaddPost").click(function () {
        var eventname = $("input[name='eventname']").val();
        var icon = $("input[name='icon']").val();
        var img = $("input[name='img']").val();

        // 表单校验（保留原站逻辑）：事件标题非空校验
        if ($.trim(eventname) === '') {
            toast.error("事件标题不能为空！", "Like_Girl");
            return false;
        }

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("新增事件成功！", "Like_Girl");
        $('#listaddPost').text('新增中...');
        $("#listaddPost").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/love-list.html'); }, 1000);
    });

    // 修改事件提交：原站为 $.ajax({url: "listupda.php", ...})
    $("#listupda").click(function () {
        var eventname = $("input[name='eventname']").val();
        var icon = $("input[name='icon']").val();
        var imgurl = $("input[name='imgurl']").val();
        var id = $("input[name='id']").val();

        // 表单校验（保留原站逻辑）：事件标题非空校验
        if ($.trim(eventname) === '') {
            toast.error("事件标题不能为空！", "Like_Girl");
            return false;
        }

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toast.success("修改事件成功！", "Like_Girl");
        $('#listupda').text('修改中...');
        $("#listupda").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/love-list.html'); }, 1000);
    });
}
