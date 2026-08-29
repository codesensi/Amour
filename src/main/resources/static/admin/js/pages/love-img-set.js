/**
 * 恋爱相册 页面模块（由 pjax 页面调度加载）。
 */
import { navigate as pjaxNavigate } from '/assets/common/pjax.js';
import { toast } from '/assets/common/toast.js';
import { loadLayui } from '/assets/common/layui.js';
import { renderAdminTable } from '../datatable-init.js';

/** 恋爱相册列表演示数据（原站静态行迁移；后端接口实现后由 table.url 取数） */
const LOVE_IMG_ROWS = [
    { id: 2, desc: '我们结婚啦', date: '2022-08-15' }
];

export function init() {
    // 恋爱相册列表：layui table 常规页码分页
    renderAdminTable({
        cols: [[
            { type: 'numbers', title: '序号', width: 70 },
            { field: 'desc', title: '图片描述', minWidth: 200 },
            { field: 'date', title: '日期', width: 180 },
            { title: '操作', width: 220, templet: function (d) {
                return '<a href="javascript:void(0);" class="js-mock-edit">'
                    + '<button type="button" class="btn btn-secondary btn-rounded"><i class=" mdi mdi-clipboard-text-play-outline mr-1"></i>修改</button></a> '
                    + '<a href="javascript:void(0);" class="delete-btn" data-id="' + d.id + '" data-desc="' + d.desc + '">'
                    + '<button type="button" class="btn btn-danger btn-rounded"><i class=" mdi mdi-delete-empty mr-1"></i>删除</button></a>';
            } }
        ]],
        data: LOVE_IMG_ROWS
    });

    // 表格行内按钮（事件委托）：layui table 动态渲染的行必须用委托才能命中。
    // 绑定在内容区外壳（pjax 不替换外壳，_rowBtnBound 防重入叠加）
    const page = document.querySelector('.content-page');
    if (page && !page._rowBtnBound) {
        page._rowBtnBound = true;
        page.addEventListener('click', function (e) {
            // 修改相册：原站跳转编辑页（modImg.php?id=x），对应页面暂未迁移，走演示提示
            const editBtn = e.target.closest('.js-mock-edit');
            if (editBtn) {
                e.preventDefault();
                demoTip();
                return;
            }
            // 删除相册：读取 data-id / data-desc 后走 layer.confirm 确认
            const delBtn = e.target.closest('.delete-btn');
            if (delBtn) {
                e.preventDefault();
                del(delBtn.dataset.id, delBtn.dataset.desc);
            }
        });
    }

    // ==================== 页面交互脚本（照搬原站公共提交脚本，mock 化改造） ====================

    // 演示提示：原站"新增/修改"按钮为页面跳转（loveImgAdd.php / modImg.php），对应页面暂未迁移
    function demoTip() {
        toast.info("演示数据：新增/编辑相册页面暂未开放", "Like_Girl");
    }
    window.demoTip = demoTip; // HTML 头部"新增"按钮 onclick 属性引用，需暴露到全局

    // 删除相册：原站为跳转 delImg.php?id=... 真实删除；现阶段后端接口未实现，
    // mock 为 layer.confirm 确认 + 演示提示，接口实现后恢复为真实删除请求
    function del(id, imgText) {
        loadLayui('layer').then(function (m) {
            m[0].confirm('您确认要删除描述为 ' + imgText + ' 的相册图片吗', { title: '删除确认' }, function (index) {
                m[0].close(index);
                toast.info("演示数据：删除操作暂未开放", "Like_Girl");
            });
        });
    }

    // 原站此脚本末尾的 loadModalContent()（请求 wiki.kikiw.cn/modalData.php 填充版本公告模态框）
    // 属于外部站点依赖且本页无对应模态框，已按改造要求移除。

    // 以下提交处理器照搬原站公共脚本：表单取值逻辑保留，$.ajax 提交统一替换为成功分支行为
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

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（原请求：POST userPost.php）
        toast.success("更新登录信息成功！", "Like_Girl");
        toast.success("更新全局信息成功", "Like_Girl");
        toast.success("更新自定义内容成功", "Like_Girl");
    })
    $("#adminPost").click(function () {
        var title = $("input[name='title']").val();
        var logo = $("input[name='logo']").val();
        var writing = $("input[name='writing']").val();
        var WebBlur = $("select[name='WebBlur']").val();
        var WebPjax = $("select[name='WebPjax']").val();

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（原请求：POST adminPost.php）
        toast.success("基本信息修改成功！", "Like_Girl");
        toast.success("开关设置成功！", "Like_Girl");
    })
    $("#loveadminPost").click(function () {
        var boy = $("input[name='boy']").val();
        var girl = $("input[name='girl']").val();
        var boyimg = $("input[name='boyimg']").val();
        var girlimg = $("input[name='girlimg']").val();
        var startTime = $("input[name='startTime']").val();

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（原请求：POST loveadminPost.php）
        toast.success("情侣信息修改成功！", "Like_Girl");
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

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（原请求：POST CardadminPost.php）
        toast.success("卡片信息修改成功！", "Like_Girl");
    })

    $("#leavPPost").click(function () {
        var jiequ = $("input[name='jiequ']").val();
        var lanjiezf = $("textarea[name='lanjiezf']").val();

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（原请求：POST leavPPost.php）
        toast.success("留言设置修改成功！", "Like_Girl");
    })

    $("#littleupda").click(function () {
        var id = $("input[name='id']").val();
        var articletitle = $("input[name='articletitle']").val();
        var articletext = $("textarea[name='articletext']").val();

        if ($.trim(articletitle) === '') {
            toast.error("文章标题不能为空！", "Like_Girl");
            return false;
        }

        if ($.trim(articletext) === '') {
            toast.error("文章内容不能为空！", "Like_Girl");
            return false;
        }

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（原请求：POST littleupda.php）
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

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（原请求：POST littleAddPost.php）
        toast.success("新增文章成功！", "Like_Girl");
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

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（原请求：POST ImgUpdaPost.php）
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

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（原请求：POST ImgAddPost.php）
        // 注：原站成功分支操作的是 #ImgUpdaPost 按钮，此处保持原逻辑不变
        toast.success("新增相册成功！", "Like_Girl");
        $('#ImgUpdaPost').text('新增中...');
        $("#ImgUpdaPost").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/love-img-set.html'); }, 1000);
    })
    $("#listaddPost").click(function () {
        var eventname = $("input[name='eventname']").val();
        var icon = $("input[name='icon']").val();
        var img = $("input[name='img']").val();

        if ($.trim(eventname) === '') {
            toast.error("事件标题不能为空！", "Like_Girl");
            return false;
        }

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（原请求：POST listaddPost.php）
        toast.success("新增事件成功！", "Like_Girl");
        $('#listaddPost').text('新增中...');
        $("#listaddPost").attr("disabled", "disabled");
        // 原站用 setInterval 存在重复触发问题，改为 setTimeout 并走 pjax 局部刷新
        setTimeout(function () { pjaxNavigate('/admin/views/love-list.html'); }, 1000);
    })
    $("#ipAddPost").click(function () {
        var ipdz = $("input[name='ipdz']").val();
        var bz = $("input[name='bz']").val();

        if ($.trim(ipdz) === '') {
            toast.error("IP地址不能为空！", "Like_Girl");
            return false;
        }

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（原请求：POST ipAddPost.php）
        // 注：原站成功分支操作的是 #listupda 按钮，此处保持原逻辑不变
        toast.success("IP封禁成功！", "Like_Girl");
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

        if ($.trim(eventname) === '') {
            toast.error("事件标题不能为空！", "Like_Girl");
            return false;
        }

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（原请求：POST listupda.php）
        toast.success("修改事件成功！", "Like_Girl");
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

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求（原请求：POST aboutPost.php）
        toast.success("修改对话配置成功！", "Like_Girl");
    })
}
