/**
 * date:2020/02/27
 * author:Mr.Chung
 * version:2.0
 * description:layuimini 主体框架扩展
 */
layui.define(["jquery", "miniMenu", "element","miniPage", "miniTheme", "api"], function (exports) {
    var $ = layui.$,
        element = layui.element,
        layer = layui.layer,
        miniMenu = layui.miniMenu,
        miniTheme = layui.miniTheme,
        miniPage = layui.miniPage,
        api = layui.api;

    if (!/http(s*):\/\//.test(location.href)) {
        var tips = "请先将项目部署至web容器（Apache/Tomcat/Nginx/IIS/等），否则部分数据将无法显示";
        return layer.alert(tips);
    }

    var miniAdmin = {

        /**
         * 后台框架初始化（经统一请求层获取数据，mock/真实模式统一处理解包与鉴权头）
         * @param options.renderPageVersion 初始化页面是否加版本号
         * @param options.bgColorDefault 默认皮肤
         * @param options.multiModule 是否开启多模块
         * @param options.menuChildOpen 是否展开子菜单
         * @param options.loadingTime 初始化加载时间
         * @param options.pageAnim 切换菜单动画
         */
        render: function (options) {
            options.renderPageVersion = options.renderPageVersion || false;
            options.bgColorDefault = options.bgColorDefault || 0;
            options.multiModule = options.multiModule || false;
            options.menuChildOpen = options.menuChildOpen || false;
            options.loadingTime = options.loadingTime || 1;
            options.pageAnim = options.pageAnim || false;
            api.request('init').then(function (data) {
                if (data == null) {
                    miniAdmin.deleteLoader(options.loadingTime);
                    return miniAdmin.error('暂无菜单信息');
                }
                miniAdmin.renderLogo(data.logoInfo);
                miniAdmin.renderAnim(options.pageAnim);
                miniAdmin.listen({
                    homeInfo:data.homeInfo,
                    multiModule: options.multiModule,
                });
                miniMenu.render({
                    menuList: data.menuInfo,
                    multiModule: options.multiModule,
                    menuChildOpen: options.menuChildOpen
                });
                miniPage.render({
                    homeInfo:data.homeInfo,
                    menuList: data.menuInfo,
                    multiModule: options.multiModule,
                    renderPageVersion: options.renderPageVersion,
                    menuChildOpen: options.menuChildOpen,
                    listenSwichCallback: function () {
                        miniAdmin.renderDevice();
                    }
                });
                miniTheme.render({
                    bgColorDefault: options.bgColorDefault,
                    listen: true,
                });
                miniAdmin.deleteLoader(options.loadingTime);
            }, function () {
                miniAdmin.deleteLoader(options.loadingTime);
                miniAdmin.error('菜单接口有误');
            });
        },

        /**
         * 初始化logo
         * @param data
         */
        renderLogo: function (data) {
            var html = '<a href="javascript:;"><img src="' + data.image + '" alt="logo"><h1>' + data.title + '</h1></a>';
            $('.layuimini-logo').html(html);
        },

        /**
         * 切换菜单动画
         * @param anim
         */
        renderAnim: function (anim) {
            if (anim) {
                $('#layuimini-bg-color').after('<style id="layuimini-page-anim">' +
                    '.layuimini-page-anim {-webkit-animation-name:layuimini-upbit;-webkit-animation-duration:.3s;-webkit-animation-fill-mode:both;}\n' +
                    '@keyframes layuimini-upbit {0% {transform:translate3d(0,30px,0);opacity:.3;}\n' +
                    '100% {transform:translate3d(0,0,0);opacity:1;}\n' +
                    '}\n' +
                    '</style>');
            }
        },

        /**
         * 进入全屏
         */
        fullScreen: function () {
            var el = document.documentElement;
            var rfs = el.requestFullScreen || el.webkitRequestFullScreen;
            if (typeof rfs != "undefined" && rfs) {
                rfs.call(el);
            } else if (typeof window.ActiveXObject != "undefined") {
                var wscript = new ActiveXObject("WScript.Shell");
                if (wscript != null) {
                    wscript.SendKeys("{F11}");
                }
            } else if (el.msRequestFullscreen) {
                el.msRequestFullscreen();
            } else if (el.oRequestFullscreen) {
                el.oRequestFullscreen();
            } else if (el.webkitRequestFullscreen) {
                el.webkitRequestFullscreen();
            } else if (el.mozRequestFullScreen) {
                el.mozRequestFullScreen();
            } else {
                miniAdmin.error('浏览器不支持全屏调用！');
            }
        },

        /**
         * 退出全屏
         */
        exitFullScreen: function () {
            var el = document;
            var cfs = el.cancelFullScreen || el.webkitCancelFullScreen || el.exitFullScreen;
            if (typeof cfs != "undefined" && cfs) {
                cfs.call(el);
            } else if (typeof window.ActiveXObject != "undefined") {
                var wscript = new ActiveXObject("WScript.Shell");
                if (wscript != null) {
                    wscript.SendKeys("{F11}");
                }
            } else if (el.msExitFullscreen) {
                el.msExitFullscreen();
            } else if (el.oRequestFullscreen) {
                el.oCancelFullScreen();
            }else if (el.mozCancelFullScreen) {
                el.mozCancelFullScreen();
            } else if (el.webkitCancelFullScreen) {
                el.webkitCancelFullScreen();
            } else {
                miniAdmin.error('浏览器不支持全屏调用！');
            }
        },

        /**
         * 初始化设备端
         */
        renderDevice: function () {
            if (miniAdmin.checkMobile()) {
                $('.layuimini-tool i').attr('data-side-fold', 1);
                $('.layuimini-tool i').attr('class', 'layui-icon layui-icon-spread-left');
                $('.layui-layout-body').removeClass('layuimini-mini');
                $('.layui-layout-body').addClass('layuimini-all');
            }
        },


        /**
         * 初始化加载时间
         * @param loadingTime
         */
        deleteLoader: function (loadingTime) {
            setTimeout(function () {
                $('.layuimini-loader').fadeOut();
            }, loadingTime * 1000)
        },

        /**
         * 成功
         * @param title
         * @returns {*}
         */
        success: function (title) {
            return layer.msg(title, {icon: 1, shade: this.shade, scrollbar: false, time: 2000, shadeClose: true});
        },

        /**
         * 失败
         * @param title
         * @returns {*}
         */
        error: function (title) {
            return layer.msg(title, {icon: 2, shade: this.shade, scrollbar: false, time: 3000, shadeClose: true});
        },

        /**
         * 判断是否为手机
         * @returns {boolean}
         */
        checkMobile: function () {
            var ua = navigator.userAgent.toLocaleLowerCase();
            var pf = navigator.platform.toLocaleLowerCase();
            var isAndroid = (/android/i).test(ua) || ((/iPhone|iPod|iPad/i).test(ua) && (/linux/i).test(pf))
                || (/ucweb.*linux/i.test(ua));
            var isIOS = (/iPhone|iPod|iPad/i).test(ua) && !isAndroid;
            var isWinPhone = (/Windows Phone|ZuneWP7/i).test(ua);
            var clientWidth = document.documentElement.clientWidth;
            if (!isAndroid && !isIOS && !isWinPhone && clientWidth > 1024) {
                return false;
            } else {
                return true;
            }
        },

        /**
         * 监听
         * @param options
         */
        listen: function (options) {
            options.homeInfo = options.homeInfo || {};

            /**
             * 清理
             */
            $('body').on('click', '[data-clear]', function () {
                var loading = layer.load(0, {shade: false, time: 2 * 1000});
                sessionStorage.clear();

                // 经统一请求层清理服务端缓存（Result 校验与错误提示由 api 层统一处理）
                api.request('clearCache').always(function () {
                    layer.close(loading);
                    miniAdmin.success('清除缓存成功');
                });
            });

            /**
             * 刷新
             */
            $('body').on('click', '[data-refresh]', function () {
                miniPage.refresh(options);
                miniAdmin.success('刷新成功');
            });

            /**
             * 监听提示信息
             */
            $("body").on("mouseenter", ".layui-nav-tree .menu-li", function () {
                if (miniAdmin.checkMobile()) {
                    return false;
                }
                var classInfo = $(this).attr('class'),
                    tips = $(this).prop("innerHTML"),
                    isShow = $('.layuimini-tool i').attr('data-side-fold');
                if (isShow == 0 && tips) {
                    tips = "<ul class='layuimini-menu-left-zoom layui-nav layui-nav-tree layui-this'><li class='layui-nav-item layui-nav-itemed'>"+tips+"</li></ul>" ;
                    window.openTips = layer.tips(tips, $(this), {
                        tips: [2, '#2f4056'],
                        time: 300000,
                        skin:"popup-tips",
                        success:function (el) {
                            var left = $(el).position().left - 10 ;
                            $(el).css({ left:left });
                            element.render();
                        }
                    });
                }
            });

            $("body").on("mouseleave", ".popup-tips", function () {
                if (miniAdmin.checkMobile()) {
                    return false;
                }
                var isShow = $('.layuimini-tool i').attr('data-side-fold');
                if (isShow == 0) {
                    try {
                        layer.close(window.openTips);
                    } catch (e) {
                        console.log(e.message);
                    }
                }
            });

            /**
             * 全屏
             */
            $('body').on('click', '[data-check-screen]', function () {
                var check = $(this).attr('data-check-screen');
                if (check == 'full') {
                    miniAdmin.fullScreen();
                    $(this).attr('data-check-screen', 'exit');
                    $(this).html('<i class="layui-icon layui-icon-screen-restore"></i>');
                } else {
                    miniAdmin.exitFullScreen();
                    $(this).attr('data-check-screen', 'full');
                    $(this).html('<i class="layui-icon layui-icon-screen-full"></i>');
                }
            });

            /**
             * 点击遮罩层
             */
            $('body').on('click', '.layuimini-make', function () {
                miniAdmin.renderDevice();
            });

        }
    };


    exports("miniAdmin", miniAdmin);
});
