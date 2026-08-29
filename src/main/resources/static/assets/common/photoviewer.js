/**
 * 图片灯箱（ES Module，两端通用）—— 基于 layui layer 的 layer.photos。
 *
 * 用法：传入图片数组（可选起始索引），弹出可左右切换的大图浏览层。
 *   import { openPhotoViewer } from '/assets/common/photoviewer.js';
 *   openPhotoViewer([{ src: '/a.jpg', alt: '描述' }, ...], 2);
 *
 * 页面需已加载 layui.js（本组件经 common/layui.js 惰性等待 layer 就绪）。
 */
import { loadLayui } from './layui.js';

/**
 * 打开图片灯箱。
 * @param {Array<{src: string, alt?: string}>} images 图片列表
 * @param {number} [startIndex=0] 起始显示的图片下标
 */
export function openPhotoViewer(images, startIndex) {
  return loadLayui('layer').then(function (m) {
    m[0].photos({
      photos: {
        title: '',
        id: Date.now(),
        start: startIndex || 0,
        data: images
      },
      anim: 5
    });
  });
}
