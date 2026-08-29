/**
 * 全站 mock 提交处理器 —— 原站公共提交脚本（异步 Ajax 到 xxxPost.php）的 mock 化归口。
 *
 * 原站把同一份提交脚本内嵌到每个管理页；layui 化后由本模块统一挂载，
 * 各页面模块在 init() 中调用 bindMockSubmissions() 即可（document 委托 +
 * 防重入标记，pjax 换页不会叠加绑定）。
 *
 * 后端接口实现后：将各处理器的 mock 提示替换为真实请求即可。
 */
import { toast } from '/assets/common/toast.js';
import { navigate as pjaxNavigate } from '/assets/common/pjax.js';

let bound = false;

export function bindMockSubmissions() {
  if (bound) return;
  bound = true;

  $("#userPost").on('click', function () {
    // 原提交：$.ajax -> userPost.php（登录信息/全局信息/自定义内容）
    toast.success("更新登录信息成功！", "Like_Girl");
    toast.success("更新全局信息成功", "Like_Girl");
    toast.success("更新自定义内容成功", "Like_Girl");
  });

  $("#adminPost").on('click', function () {
    // 原提交：$.ajax -> adminPost.php（基本信息/开关设置）
    toast.success("基本信息修改成功！", "Like_Girl");
    toast.success("开关设置成功！", "Like_Girl");
  });

  $("#loveadminPost").on('click', function () {
    // 原提交：$.ajax -> loveadminPost.php（情侣信息）
    toast.success("情侣信息修改成功！", "Like_Girl");
  });

  $("#CardadminPost").on('click', function () {
    // 原提交：$.ajax -> CardadminPost.php（卡片信息）
    toast.success("卡片信息修改成功！", "Like_Girl");
  });

  $("#leavPPost").on('click', function () {
    // 原提交：$.ajax -> leavPPost.php（留言设置：截取条数/拦截字符）
    toast.success("留言设置修改成功！", "Like_Girl");
  });

  $("#aboutPost").on('click', function () {
    // 原提交：$.ajax -> aboutPost.php（关于页面对话配置，共 24 个字段）
    toast.success("修改对话配置成功！", "Like_Girl");
  });

  $("#littleupda").on('click', function () {
    const articletitle = $("input[name='articletitle']").val();
    const articletext = $("textarea[name='articletext']").val();

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
    toast.success("文章修改成功！", "Like_Girl");
    disableAndRedirect('#littleupda', '修改中...', '/admin/views/little-set.html');
  });

  $("#littleAddPost").on('click', function () {
    const articlename = $("select[name='articlename']").val();
    const articletitle = $("input[name='articletitle']").val();
    const articletext = $("textarea[name='articletext']").val();

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
    toast.success("新增文章成功！", "Like_Girl");
    disableAndRedirect('#littleAddPost', '发布中...', '/admin/views/little-set.html');
  });

  $("#ImgUpdaPost").on('click', function () {
    const imgDatd = $("input[name='imgDatd']").val();
    const imgText = $("input[name='imgText']").val();
    const imgUrl = $("input[name='imgUrl']").val();

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
    toast.success("相册修改成功！", "Like_Girl");
    disableAndRedirect('#ImgUpdaPost', '修改中...', '/admin/views/love-img-set.html');
  });

  $("#ImgAddPost").on('click', function () {
    const imgDatd = $("input[name='imgDatd']").val();
    const imgText = $("input[name='imgText']").val();
    const imgUrl = $("input[name='imgUrl']").val();

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
    toast.success("新增相册成功！", "Like_Girl");
    disableAndRedirect('#ImgUpdaPost', '新增中...', '/admin/views/love-img-set.html');
  });

  $("#listaddPost").on('click', function () {
    const eventname = $("input[name='eventname']").val();

    // 保留原站表单校验逻辑
    if ($.trim(eventname) === '') {
      toast.error("事件标题不能为空！", "Like_Girl");
      return false;
    }

    // 原提交：$.ajax -> listaddPost.php（新增事件）
    toast.success("新增事件成功！", "Like_Girl");
    disableAndRedirect('#listaddPost', '新增中...', '/admin/views/love-list.html');
  });

  $("#listupda").on('click', function () {
    const eventname = $("input[name='eventname']").val();

    // 保留原站表单校验逻辑
    if ($.trim(eventname) === '') {
      toast.error("事件标题不能为空！", "Like_Girl");
      return false;
    }

    // 原提交：$.ajax -> listupda.php（修改事件）
    toast.success("修改事件成功！", "Like_Girl");
    disableAndRedirect('#listupda', '修改中...', '/admin/views/love-list.html');
  });

  $("#ipAddPost").on('click', function () {
    const ipdz = $("input[name='ipdz']").val();

    // 保留原站表单校验逻辑
    if ($.trim(ipdz) === '') {
      toast.error("IP地址不能为空！", "Like_Girl");
      return false;
    }

    // 原提交：$.ajax -> ipAddPost.php（IP 封禁）
    toast.success("IP封禁成功！", "Like_Girl");
    disableAndRedirect('#listupda', '提交中...', '/admin/views/ip-list.html');
  });
}

/** 提交反馈：按钮文案切换 + 禁用，随后经 pjax 回到目标列表页 */
function disableAndRedirect(selector, text, target) {
  $(selector).text(text);
  $(selector).attr('disabled', 'disabled');
  // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
  setTimeout(function () { pjaxNavigate(target); }, 1000);
}
