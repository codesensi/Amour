/**
 * layui 自定义扩展注册
 *
 * - app：应用层（统一请求层 / 登录态），业务代码从这里引入
 * - lay-module：layuimini 框架扩展与第三方组件扩展
 */
window.rootPath = (function (src) {
    src = document.scripts[document.scripts.length - 1].src;
    return src.substring(0, src.lastIndexOf("/") + 1);
})();

layui.config({
    base: rootPath,
    version: true
}).extend({
    // 应用层（位于 js 根目录）
    api: 'api',                // 统一请求层（mock/真实后端双模式）
    auth: 'auth',              // 登录态管理
    // layuimini 框架扩展
    miniAdmin: "lay-module/layuimini/miniAdmin", // 后台框架主体
    miniMenu: "lay-module/layuimini/miniMenu",   // 菜单扩展
    miniPage: "lay-module/layuimini/miniPage",   // 单页路由扩展
    miniTheme: "lay-module/layuimini/miniTheme", // 主题扩展
    // 第三方组件扩展
    treetable: 'lay-module/treetable-lay/treetable',     // table 树形扩展
    tableSelect: 'lay-module/tableSelect/tableSelect',   // table 选择扩展
    iconPickerFa: 'lay-module/iconPicker/iconPickerFa',  // layui-icon 图标选择扩展（数据源：layui.css）
    echarts: 'lay-module/echarts/echarts',               // echarts 图表
    echartsTheme: 'lay-module/echarts/echartsTheme',     // echarts 图表主题
    wangEditor: 'lay-module/wangEditor/wangEditor'       // wangEditor 富文本（layui 封装，内部加载压缩版）
});
