/**
 * 后台管理局部刷新（pjax）模块。
 *
 * 拦截后台页面内的 .html 链接点击，通过 AJAX 拉取目标页并仅替换内容区
 * （.content-page），顶部栏、侧边菜单、loading 等公共外壳保持不动；
 * 浏览器地址栏经 history.pushState 同步更新，前进/后退同样按局部刷新处理。
 *
 * 约定：
 * - 各页面的"页面级脚本"统一放在 .content-page 容器内部（页脚之前），
 *   整页加载与局部注入两个场景都会被执行（jQuery 插入脚本时自动求值）；
 * - 外壳脚本（登录守卫、console 输出、外壳适配等）留在容器外，仅整页加载时执行一次；
 * - 目标页拉取失败或结构异常时，自动降级为整页跳转，行为与接入前一致。
 *
 * 页面脚本中如需以局部刷新方式跳转，调用 AdminPjax.load(url)。
 */
(function (global) {
    'use strict';

    // 缺少历史 API 或 jQuery 时直接退出，页面保持默认的整页跳转行为
    if (!global.history || !global.history.pushState || !global.jQuery) {
        return;
    }
    var $ = global.jQuery;

    /** 内容区选择器：pjax 替换的最小单元（含页面脚本与页脚） */
    var CONTENT_SELECTOR = '.content-page';

    /** 进行中的请求：再次切换时取消，避免旧响应晚到覆盖新内容 */
    var pendingXhr = null;

    /** 顶部加载进度条元素 */
    var $progress = null;

    /** 进度条样式：注入一次即可，随元素创建 */
    function ensureProgressStyle() {
        if (document.getElementById('admin-pjax-progress-style')) {
            return;
        }
        var style = document.createElement('style');
        style.id = 'admin-pjax-progress-style';
        style.textContent =
            '.admin-pjax-progress{position:fixed;top:0;left:0;height:3px;width:0;z-index:9999;' +
            'background:#ff5295;box-shadow:0 0 8px #ff5295;transition:width .4s ease,opacity .4s ease;' +
            'opacity:0;pointer-events:none;}' +
            '.admin-pjax-progress.is-active{width:90%;opacity:1;transition:width 4s ease,opacity .2s ease;}';
        document.head.appendChild(style);
    }

    /**
     * 判断链接是否可拦截为局部刷新：
     * 同源且位于 /admin/ 目录下的 .html 地址，且未使用修饰键/新窗口打开。
     */
    function isPjaxLink(link) {
        var href = link.getAttribute('href');
        if (!href) return false;
        // 脚本链接与页内锚点不拦截
        if (href.indexOf('javascript:') === 0 || href.charAt(0) === '#') return false;
        // 新窗口 / 强制下载不拦截
        if (link.target && link.target !== '_self') return false;
        if (link.hasAttribute('download')) return false;
        // 跨源不拦截
        if (link.protocol !== location.protocol || link.host !== location.host) return false;
        // 仅处理后台管理目录下的 .html 页面
        var path = link.pathname;
        if (path.indexOf('/admin/') !== 0) return false;
        return /\.html$/.test(path);
    }

    /** 去掉地址中的 hash，用于判断是否同页跳转 */
    function stripHash(url) {
        return url.split('#')[0];
    }

    /** 显示顶部进度条（首次调用时创建元素） */
    function startProgress() {
        ensureProgressStyle();
        if (!$progress) {
            $progress = $('<div class="admin-pjax-progress"></div>').appendTo('body');
        }
        $progress.addClass('is-active');
    }

    /** 隐藏顶部进度条 */
    function stopProgress() {
        if ($progress) {
            $progress.removeClass('is-active');
            // 动画结束后归零宽度，下次切换从头增长
            setTimeout(function () {
                $progress.css('width', '0');
            }, 400);
        }
    }

    /**
     * 销毁作用域内已初始化的 DataTable。
     * 局部刷新会丢弃旧表格节点，先销毁释放 DataTables 在节点上挂载的状态。
     */
    function destroyDataTables($scope) {
        if (!$.fn.DataTable) {
            return;
        }
        $scope.find('table').each(function () {
            if ($.fn.DataTable.isDataTable(this)) {
                $(this).DataTable().destroy();
            }
        });
    }

    /** 更新浏览器标签标题：沿用当前页"站名 - 页面名"格式中的站名前缀 */
    function updateTitle(pageTitle) {
        if (!pageTitle) {
            return;
        }
        var parts = document.title.split(' - ');
        document.title = parts.length > 1 ? parts[0] + ' - ' + pageTitle : pageTitle;
    }

    /**
     * 以局部刷新方式加载指定地址。
     *
     * @param {string}  url  目标页地址（/admin/ 下的 .html 页面）
     * @param {boolean} push 是否写入一条历史记录（前进/后退触发时为 false）
     */
    function load(url, push) {
        // 取消上一次未完成的请求，防止旧响应覆盖新内容
        if (pendingXhr) {
            pendingXhr.abort();
            pendingXhr = null;
        }
        startProgress();
        pendingXhr = $.ajax({ url: url, dataType: 'html', cache: false })
            .done(function (html) {
                var doc = new DOMParser().parseFromString(html, 'text/html');
                var nextPage = doc.querySelector(CONTENT_SELECTOR);
                var curPage = document.querySelector(CONTENT_SELECTOR);
                // 结构异常（目标不是后台页面，如登录页、错误响应）时降级整页跳转
                if (!nextPage || !curPage) {
                    global.location.href = url;
                    return;
                }
                destroyDataTables($(curPage));
                // 跨文档节点先收编进当前文档，再替换内容区
                document.adoptNode(nextPage);
                $(curPage).replaceWith(nextPage);
                if (push) {
                    global.history.pushState({ pjax: true }, '', url);
                }
                global.scrollTo(0, 0);
                updateTitle(doc.title);
                stopProgress();
                // 新内容里的表格需要重新初始化（demo.datatable-init.js 暴露的入口）
                if (typeof global.initAdminDataTables === 'function') {
                    global.initAdminDataTables();
                }
            })
            .fail(function () {
                // 请求失败（网络异常/静态资源缺失）时降级为整页跳转
                global.location.href = url;
            });
    }

    // 事件委托拦截所有链接点击（覆盖外壳菜单与内容区内的链接）
    $(document).on('click', 'a', function (e) {
        // 仅拦截普通左键点击；带修饰键（新窗口等）走浏览器默认行为
        if (e.which !== 1 || e.ctrlKey || e.metaKey || e.shiftKey || e.altKey) {
            return;
        }
        var link = e.currentTarget;
        if (!isPjaxLink(link)) {
            return;
        }
        e.preventDefault();
        // 同页链接不重复加载
        if (stripHash(link.href) === stripHash(location.href)) {
            return;
        }
        load(link.href, true);
    });

    // 前进/后退：按当前地址做局部刷新（不重复写入历史）
    global.addEventListener('popstate', function () {
        if (location.pathname.indexOf('/admin/') === 0) {
            load(location.href, false);
        }
    });

    // 暴露给页面脚本使用：mock 提交成功后的延时跳转等场景
    global.AdminPjax = {
        load: load
    };
})(window);
