/**
 * 恋爱清单 页面模块（由 pjax 页面调度加载）。
 */
import { escapeHtml as esc } from '../escape.js';
import { bindMockSubmissions } from '../mock-submit.js';
import { openCrudDialog, confirmDelete, mockRequest, nextId } from '../crud-dialog.js';
import { toast } from '/assets/common/toast.js';
import { openPhotoViewer } from '/assets/common/photoviewer.js';
import { renderAdminTable } from '../datatable-init.js';

/** 恋爱清单列表演示数据（原站 83 条静态行迁移；后端接口实现后由 table.url 取数） */
const LOVE_EVENTS = [
    { id: 83, title: "一起期待未来甜蜜小生活💑", done: false, img: '' },
    { id: 82, title: "一起为我们的小家添置东西🏠", done: false, img: '' },
    { id: 81, title: "一起挑选婚纱👗", done: false, img: '' },
    { id: 80, title: "一起挑选戒指💍", done: false, img: '' },
    { id: 79, title: "一起去见父母👨‍👩‍👧‍👦", done: false, img: '' },
    { id: 78, title: "一起入住一次五星级酒店，看夜景🏨", done: false, img: '' },
    { id: 77, title: "一起去许愿池许个愿🙏", done: false, img: '' },
    { id: 76, title: "一起玩一次真心话大冒险🎭", done: false, img: '' },
    { id: 75, title: "一起去听一次相声😂", done: false, img: '' },
    { id: 74, title: "一起去一次酒吧🍹", done: false, img: '' },
    { id: 73, title: "一起听一次演唱会🎤", done: true, img: '' },
    { id: 72, title: "一起拍照洗照片贴房间🖼", done: false, img: '' },
    { id: 71, title: "一起骑行车🚴", done: false, img: '' },
    { id: 70, title: "一起去一次动物园🐼", done: false, img: '' },
    { id: 69, title: "一起敷面膜🧖", done: false, img: '' },
    { id: 68, title: "一起去看樱花🌸", done: false, img: '' },
    { id: 67, title: "一起存钱💰", done: false, img: '' },
    { id: 66, title: "为对方做便当🍱", done: false, img: '' },
    { id: 65, title: "当陌生人一天，不许交流🤫", done: false, img: '' },
    { id: 64, title: "接对方下班一次🚗", done: false, img: '' },
    { id: 63, title: "一起吐槽一次对方的缺点😆", done: false, img: '' },
    { id: 62, title: "一起放一次风筝🪁", done: false, img: '' },
    { id: 61, title: "一起为对方按摩一次💆", done: false, img: '' },
    { id: 60, title: "一起去跳一次广场舞🕺", done: false, img: '' },
    { id: 59, title: "一起去挑选一束花💐", done: false, img: '' },
    { id: 58, title: "一起在午夜看一次恐怖片🎃", done: false, img: '' },
    { id: 57, title: "一起为布置小家出主意🏠", done: false, img: '' },
    { id: 56, title: "一起坐一次巴士，在没去过的地方下车🚌", done: false, img: '' },
    { id: 55, title: "偷偷为对方买喜欢又舍不得的东西🎁", done: false, img: '' },
    { id: 54, title: "一起睡个懒觉，赖个床🛏", done: false, img: '' },
    { id: 53, title: "一起在沙发上躺着🛋", done: false, img: '' },
    { id: 52, title: "一起去我们上过的小学，中学，大学🏫", done: false, img: '' },
    { id: 51, title: "给对方准备礼物🎁", done: false, img: '' },
    { id: 50, title: "一起DIY手工🎨", done: false, img: '' },
    { id: 49, title: "一起露营，住一次帐篷🏕", done: false, img: '' },
    { id: 48, title: "一起坐一次船⛵", done: false, img: '' },
    { id: 47, title: "一起听音乐，听同一首歌🎵", done: false, img: '' },
    { id: 46, title: "一起跳舞💃", done: false, img: '' },
    { id: 45, title: "一起和朋友们去吃饭🍽", done: false, img: '' },
    { id: 44, title: "一起看雪，堆雪人⛄", done: false, img: '' },
    { id: 43, title: "一起庆祝恋爱纪念日💖", done: false, img: '' },
    { id: 42, title: "一起吃火锅🍲", done: false, img: '' },
    { id: 41, title: "一起看烟花🎆", done: false, img: '' },
    { id: 40, title: "一起修理电器🔧", done: false, img: '' },
    { id: 39, title: "一起打扑克牌🃏", done: false, img: '' },
    { id: 38, title: "一起喝醉一次🍻", done: false, img: '' },
    { id: 37, title: "一起去一次海底世界🐠", done: false, img: '' },
    { id: 36, title: "一起用情侣手机壳📱", done: false, img: '' },
    { id: 35, title: "一起种花草🌱", done: false, img: '' },
    { id: 34, title: "一起坐一次飞机✈️", done: false, img: '' },
    { id: 33, title: "一起上下班，坐地铁🚇", done: false, img: '' },
    { id: 32, title: "一起看日出看日落🌅", done: false, img: '' },
    { id: 31, title: "一起做一次蛋糕甜点🍰", done: false, img: '' },
    { id: 30, title: "一起在下雨天追剧📺", done: false, img: '' },
    { id: 29, title: "一起看书，分享自己喜欢的书籍📖", done: false, img: '' },
    { id: 28, title: "一起坐一次热气球🎈", done: false, img: '' },
    { id: 27, title: "一起逛超市买好吃的🛒", done: false, img: '' },
    { id: 26, title: "互穿对方的衣服，拍照留念📸", done: false, img: '' },
    { id: 25, title: "一起去看一次海，去沙滩🏖", done: false, img: '' },
    { id: 24, title: "一起为对方刷牙，然后亲亲😘", done: false, img: '' },
    { id: 23, title: "一起拍视频记录生活📹", done: false, img: '' },
    { id: 22, title: "一起坐一次摩天轮🎡", done: false, img: '' },
    { id: 21, title: "一起去爬山⛰", done: false, img: '' },
    { id: 20, title: "一起去旅游✈️", done: false, img: '' },
    { id: 19, title: "一起熬夜通宵跨年🎇", done: false, img: '' },
    { id: 18, title: "一起去吃一次全家桶🍗", done: false, img: '' },
    { id: 17, title: "一起去做次陶艺🏺", done: false, img: '' },
    { id: 16, title: "一起为对方抹指甲油💅", done: false, img: '' },
    { id: 15, title: "一起给对方化妆💅", done: false, img: '' },
    { id: 14, title: "一起研究口红色号💄", done: false, img: '' },
    { id: 13, title: "一起养一只宠物🐶", done: false, img: '' },
    { id: 12, title: "一起去蹦极🪂", done: false, img: '' },
    { id: 11, title: "一起去一次鬼屋👻", done: false, img: '' },
    { id: 10, title: "一起给对方写信，然后读给对方听💌", done: false, img: '' },
    { id: 9, title: "一起打扫卫生🧹", done: false, img: '' },
    { id: 8, title: "一起过生日🎂", done: false, img: '' },
    { id: 7, title: "一起过次烛光晚餐🍷", done: false, img: '' },
    { id: 6, title: "一起在厨房做次饭🍳", done: false, img: '' },
    { id: 5, title: "一起唱次歌并且录下来🎤", done: false, img: '' },
    { id: 4, title: "一起去游泳🏊", done: false, img: '' },
    { id: 3, title: "一起去一趟迪士尼游乐园🎡", done: false, img: '' },
    { id: 2, title: "一起穿情侣装逛街🧡", done: false, img: '' },
    { id: 1, title: "一起去电影院看一场电影🎬", done: false, img: '' }
];

/** 内容区内参与灯箱预览的图片选择器（照搬原站的扩展名匹配） */
const IMG_SELECTOR = 'img[src$=jpg],img[src$=gif],img[src$=JPG],img[src$=png],img[src$=jpeg]';

/**
 * 恋爱清单 CRUD API（mock 实现）。
 * TODO(后端): 接口实现后替换为真实请求，例如：
 *   create: (data) => fetch('/admin/api/love-events', { method: 'POST', body: JSON.stringify(data) })
 */
const api = {
    create(data) {
        return mockRequest('新增事件成功！', function () {
            LOVE_EVENTS.unshift({ id: nextId(LOVE_EVENTS), title: data.title, done: data.done === '1', img: data.img || '' });
        });
    },
    update(id, data) {
        return mockRequest('修改事件成功！', function () {
            const row = LOVE_EVENTS.find(function (r) { return String(r.id) === String(id); });
            if (row) Object.assign(row, { title: data.title, done: data.done === '1', img: data.img || '' });
        });
    },
    remove(id) {
        return mockRequest('删除事件成功！', function () {
            const i = LOVE_EVENTS.findIndex(function (r) { return String(r.id) === String(id); });
            if (i > -1) LOVE_EVENTS.splice(i, 1);
        });
    }
};

/** 新增/修改事件表单字段配置 */
const EVENT_FIELDS = [
    { name: 'title', label: '事件标题', required: true, placeholder: '请输入事件标题' },
    { name: 'done', label: '完成状态', type: 'select', options: [{ value: '0', label: '未完成' }, { value: '1', label: '已完成' }] },
    { name: 'img', label: '图片地址', placeholder: '选填，填写图片 URL' }
];

export function init() {
    // 恋爱清单列表：layui table 常规页码分页（83 条演示数据）
    function renderTable() {
        return renderAdminTable({
            elem: '#love-event-table',
        cols: [[
            { type: 'numbers', title: '序号', width: 70 },
            { field: 'title', title: '事件标题', minWidth: 220 },
            { field: 'done', title: '完成状态', width: 110, templet: function (d) {
                return d.done
                    ? '<span class="layui-badge layui-bg-green">已完成</span>'
                    : '<span class="layui-badge layui-bg-red">未完成</span>';
            } },
            { field: 'img', title: '图片预览', width: 160, templet: function (d) {
                return d.img ? '<img src="' + d.img + '" style="height:40px;">' : '暂无图片';
            } },
            { title: '操作', width: 200, templet: function (d) {
                // 删除按钮用 data 属性传参（避免 title 中引号/emoji 破坏 href 字符串），点击行为由下方委托接管
                return '<a href="javascript:void(0);" class="layui-btn layui-btn-xs js-mock-edit" data-id="' + d.id + '">'
                    + '<i class=" layui-icon layui-icon-edit"></i>修改</a> '
                    + '<a href="javascript:void(0);" class="layui-btn layui-btn-xs layui-btn-danger delete-btn" data-id="' + d.id + '" data-title="' + esc(d.title) + '">'
                    + '<i class=" layui-icon layui-icon-delete"></i>删除</a>';
            } }
        ]],
        data: LOVE_EVENTS
    });
    }

    renderTable();

    // 为页面中的图片挂载灯箱预览（layui layer.photos，替换原 hs.expand/spotlight 方案）：
    // 委托绑定到内容区外壳（pjax 不替换外壳，绑定一次即可），layui table 渲染的图片同样生效
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

    // 新增事件：弹出表单弹框，确认后经 API 层提交（现阶段 mock，后端接入后仅换 API 层）
    $('.js-add-event').on('click', function () {
        openCrudDialog({
            title: '新增事件',
            fields: EVENT_FIELDS,
            onSubmit: function (values) {
                return api.create(values).then(renderTable);
            }
        });
    });

    // 表格行内按钮（事件委托）：layui table 动态渲染的行必须用委托才能命中。
    // 绑定在内容区外壳（pjax 不替换外壳，_rowBtnBound 防重入叠加）
    if (page && !page._rowBtnBound) {
        page._rowBtnBound = true;
        page.addEventListener('click', function (e) {
            // 修改事件：弹出表单弹框并预填当前行数据
            const editBtn = e.target.closest('.js-mock-edit');
            if (editBtn) {
                e.preventDefault();
                const row = LOVE_EVENTS.find(function (r) { return String(r.id) === String(editBtn.dataset.id); });
                if (!row) return;
                openCrudDialog({
                    title: '修改事件',
                    fields: EVENT_FIELDS,
                    initial: { title: row.title, done: row.done ? '1' : '0', img: row.img },
                    onSubmit: function (values) {
                        return api.update(row.id, values).then(renderTable);
                    }
                });
                return;
            }
            // 删除事件：读取 data-id / data-title 后走确认提示
            const delBtn = e.target.closest('.delete-btn');
            if (delBtn) {
                e.preventDefault();
                removeRow(delBtn.dataset.id, delBtn.dataset.title);
            }
        });
    }

    // 删除事件：原站 confirm 确认后跳转 dellist.php?id=x 真实删除；
    // 现经确认提示 + API 层处理（mock），数据变更后重渲染表格
    function removeRow(id, title) {
        confirmDelete({
            message: '您确认要删除内容为 ' + title + ' 的事件吗',
            onDelete: function () {
                return api.remove(id).then(renderTable);
            }
        });
    }

    // 全站 mock 提交处理器统一由 mock-submit.js 挂载（document 委托 + 防重入）
    bindMockSubmissions();
}
