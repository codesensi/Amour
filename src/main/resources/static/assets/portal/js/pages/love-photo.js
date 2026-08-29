/**
 * 相册页页面模块（由 pjax 页面调度加载）：
 * "加载更多"按钮式分页（layui flow）+ 逐张浮现动画 + 点击灯箱（layer.photos）。
 * 首屏自动加载第 1 页（6 张），之后每点击一次按钮追加一页；
 * 数据加载完（含首屏不足一页）后按钮隐藏。
 * 分页编排复用 portal.js 的 initLoadMore（portal 各列表页共用的通用能力）。
 */
import { portalRequest, createPhotoElement, initLoadMore } from '../portal.js';
import { openPhotoViewer } from '/assets/common/photoviewer.js';

/**
 * 相册"加载更多"分页 + 灯箱绑定（页面模块入口，由 main.js 的页面调度调用）。
 */
export function init() {
  const gallery = document.getElementById('photoGallery');
  if (!gallery) return;

  initLoadMore({
    elem: '#photoGallery',
    fetchPage: function (page) { return portalRequest('photos', { page: page, limit: 6 }); },
    render: function (photo) { return createPhotoElement(photo).trim(); },
    emptyText: '暂无照片…',
    onItems: function () {
      // 逐张浮现动画（仅处理本次追加、尚未浮现的卡片）
      gallery.querySelectorAll('.photo-item:not(.show)').forEach(function (el, idx) {
        setTimeout(function () { el.classList.add('show'); }, idx * 300);
      });
    }
  });

  bindLightbox(gallery);
}

/** 图片点击灯箱（委托绑定在 gallery 上，随 pjax 注入的元素生效） */
function bindLightbox(gallery) {
  gallery.addEventListener('click', function (e) {
    const img = e.target.closest('img');
    if (!img) return;
    const imgs = Array.prototype.slice.call(gallery.querySelectorAll('img'));
    openPhotoViewer(imgs.map(function (im) {
      return { src: im.src, alt: im.getAttribute('data-description') || '' };
    }), imgs.indexOf(img));
  });
}
