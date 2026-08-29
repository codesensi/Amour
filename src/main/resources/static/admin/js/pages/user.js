/**
 * 全局设置 页面模块（由 pjax 页面调度加载）。
 */
export function init() {
    // 原站遗留的账号/密码格式校验函数，按原样保留（表单未绑定 onsubmit，不会自动触发）
    function check() {
        let adminName = document.getElementsByName('adminName')[0].value.trim();
        let pw = document.getElementsByName('pw')[0].value.trim();
        // 判断adminName长度是否为0 如果为0则提示弹窗
        if (adminName.length == 0) {
            alert("请填写用户名");
            return false;
        }
        let user = /[a-zA-Z0-9]/g;
        let character = new RegExp("[`~!#$^&*()=|{}':;',\\[\\].<>/?~！#￥……&*（）——|{}【】‘；：”“'。，、？]");
        if (character.test(adminName)) {
            alert("用户名含有特殊字符 本次修改已拦截")
            return false;
        } else if (!(user.test(adminName))) {
            alert("用户名只支持数字与英文大小写字母")
            return false;
        }
        if (pw.length >= 1) {
            if (pw.length <= 6) {
                alert("密码长度需大于六位数")
                return false;
            }
            if (character.test(pw)) {
                alert("密码含有特殊字符为了过滤SQL注入已拦截\n请输入大小写字母与数字组成的密码")
                return false;
            }
        }
    }

    // 信息配置表单提交（原站通过 $.ajax 提交至 userPost.php，取值逻辑保留）
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

        // 现阶段后端接口未实现，mock 成功提示；接口实现后恢复为真实请求
        toastr["success"]("更新登录信息成功！", "Like_Girl");
        toastr["success"]("更新全局信息成功", "Like_Girl");
        toastr["success"]("更新自定义内容成功", "Like_Girl");
    })

    // 原站此处的 loadModalContent() 会请求 wiki.kikiw.cn 外部数据，属于外部站点依赖，已按规范移除
}
