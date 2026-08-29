/* =====================================================================
 * Amour 门户脚本 —— 照搬 Like Girl 5.2.1（ES Module）
 * 职责：站点配置、恋爱计时器、data-tip 悬浮提示、顶栏滚动变色、
 *       右侧悬浮栏交互、以及"未实现后端接口"的统一请求口子（mock 降级）。
 * 依赖：common/config.js（项目配置）、common/pjax.js（编程式导航）、
 *       common/toast.js（消息提示）。
 * 原站版权：Copyright (c) 2023 - 2025 by Ki（Like Girl）
 * ===================================================================== */

import { loadConfig, qqAvatar } from '/assets/common/config.js';
import { navigate as pjaxNavigate } from '/assets/common/pjax.js';
import { toast } from '/assets/common/toast.js';
import { loadLayui } from '/assets/common/layui.js';
import { initTooltip } from '/assets/common/tooltip.js';

/** 侧栏悬浮栏的 HTML onclick 属性引用的函数，模块化后统一挂回全局 */
window.scrollToTop = function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' });
};
window.portalNavigate = function portalNavigate(url) {
  pjaxNavigate(url, true);
};
window.portalGoAdmin = function portalGoAdmin() {
  window.open(PORTAL_CONFIG.adminUrl, '_blank');
};
window.portalGoRepo = function portalGoRepo() {
  window.open(PORTAL_CONFIG.repoUrl, '_blank');
};

/** 站点配置：纯前端路由类配置；站点展示类配置（logo/文案/头像/计时起点/ICP 等）走 siteConfig 口子 */
const PORTAL_CONFIG = {
  /** 管理后台入口 */
  adminUrl: '/admin/login.html',
  /** 开源地址 */
  repoUrl: 'https://github.com/codesensi/Amour',
  /** 本地化静态资源根 */
  imgBase: '/assets/portal/img'
};

/**
 * 未实现后端接口的统一口子。
 * 后端按「统一响应 Result{success, code, msg, data}」实现以下接口后，
 * 前端零改动自动切换为真实数据；失败/未实现时降级返回内置示例数据。
 */
const PORTAL_API = {
  /** 恋爱相册分页：POST /love/photos {page, limit} -> {total, data:[{img,text,date}]} */
  photos: { url: '/love/photos', method: 'POST' },
  /** 留言分页：GET /love/messages?page=&limit= -> {total, data:[{qq,nickname,avatar,content,date,location}]} */
  messages: { url: '/love/messages', method: 'GET' },
  /** 提交留言：POST /love/messages {qq, nickname, content} */
  sendMessage: { url: '/love/messages', method: 'POST' },
  /** 恋爱清单分页：GET /love/list?page=&limit= -> {total, data:[{text, done, date, img}]} */
  loveList: { url: '/love/list', method: 'GET' },
  /** 点点滴滴文章分页：GET /love/littles?page=&limit= -> {total, data:[{id, title, author, date}]} */
  littles: { url: '/love/littles', method: 'GET' },
  /**
   * 关于页对话剧本：GET /love/chat -> 节点数组（后台可配置，预留口子）
   * 节点两种：{type:'bot', delay, content} 消息（content 支持 "![alt](url)" 图片语法）；
   *          {type:'buttons', delay, options:[{text, value, next:[...]}]} 分支按钮，next 为点选后继续执行的子序列
   */
  aboutChat: { url: '/love/chat', method: 'GET' }
  /* 站点展示类配置不走独立接口：统一经 config.js 的 /config/public 口子读取 sys_config */
};

/** 占位图渐变色池：批量生成的示例照片循环取色，视觉上区分页与页 */
const PHOTO_GRADIENTS = [
  ['#ffd3d3', '#cfe8ff'],
  ['#d3ffe3', '#cfe0ff'],
  ['#fff3d3', '#ffd6e0'],
  ['#e3d3ff', '#cfeaff'],
  ['#d3e8ff', '#e8ffd3']
];

/** 批量生成示例照片的文案池 */
const PHOTO_LABELS = [
  '海边漫步', '山顶日出', '巷口的猫', '一起逛的夜市', '雨后的彩虹', '冬天的初雪',
  '游乐园的一天', '深夜的电影院', '郊外的野餐', '咖啡馆的下午', '花田里的合影', '车站的告别'
];

/**
 * 批量生成示例照片（凑足多页数据，便于查看滚动懒加载的分页效果）。
 * @param {number} count 生成条数
 */
function generateMorePhotos(count) {
  const list = [];
  for (let i = 0; i < count; i++) {
    const label = PHOTO_LABELS[i % PHOTO_LABELS.length] + ' · ' + (Math.floor(i / PHOTO_LABELS.length) + 1);
    const gradient = PHOTO_GRADIENTS[i % PHOTO_GRADIENTS.length];
    const month = String((i % 12) + 1).padStart(2, '0');
    const day = String((i % 27) + 1).padStart(2, '0');
    list.push({ img: mockPhoto(label, gradient[0], gradient[1]), text: label, date: '2025-' + month + '-' + day });
  }
  return list;
}

/** 示例数据：仅在后端接口未实现时兜底展示（关于页对话模块复用 aboutChat 剧本） */
export const PORTAL_MOCK = {
  photos: generateMorePhotos(48),
  messages: [
    { qq: '3439780232', nickname: 'Ki.', avatar: 'https://q1.qlogo.cn/g?b=qq&nk=3439780232&s=100', content: 'Like Girl 5.2.1-Stable 默认留言', date: '2025-09-02 16:24:09', location: '广东' },
    { qq: '673822943', nickname: 'Su', avatar: 'https://q1.qlogo.cn/g?b=qq&nk=673822943&s=100', content: '愿得一心人，白头不相离。', date: '2025-09-03 00:00:00', location: '' }
  ],
  loveList: [
    { text: '一起期待未来甜蜜小生活💑', done: false },
    { text: '一起为我们的小家添置东西🏠', done: false },
    { text: '一起挑选婚纱👗', done: false },
    { text: '一起去见父母👨‍👩‍👧‍👦', done: false },
    { text: '一起听一次演唱会🎤', done: true, img: mockPhoto('演唱会的回忆') },
    { text: '一起去看樱花🌸', done: false },
    { text: '一起存钱💰', done: false }
  ],
  littles: [
    { id: 1, title: 'Like_Girl 默认文章语法', author: 'Ki.', date: '2022-11-20' },
    { id: 2, title: '第一次一起去看海', author: 'Ki.', date: '2023-05-21' },
    { id: 3, title: '记录我们的第 1000 天', author: 'Su', date: '2024-05-15' }
  ],
  /** 关于页对话默认剧本（后台实现 /love/chat 后自动切换为配置数据） */
  aboutChat: [
    { type: 'bot', delay: 200, content: 'Hi, 欢迎你的来访' },
    { type: 'bot', delay: 1000, content: '愿得一人心 白首不相离' },
    { type: 'bot', delay: 1000, content: '记录日常生活 留住感动' },
    {
      type: 'buttons', delay: 1500,
      options: [
        {
          text: '听我介绍', value: 'and',
          next: [
            { type: 'bot', delay: 1500, content: '情侣小站Like Girl是 Ki 的原创项目' },
            { type: 'bot', delay: 1500, content: '在2022年暑假的假期最后几天里发布了1.0版本' },
            { type: 'bot', delay: 1500, content: '最新版本为 v5.2.0 亦是最终版本 目前已开源到码云' },
            { type: 'bot', delay: 1500, content: 'PHP确实是 “世界上最好的语言”  我非常喜欢（痛苦' },
            { type: 'bot', delay: 1500, content: '在开发过程中遇到了许多奇葩问题 也是只能自己探索解决...' },
            { type: 'bot', delay: 1500, content: '喜欢探索编程领域 热爱学习新知识 热爱开源文化' },
            {
              type: 'buttons', delay: 1500,
              options: [{ text: '为什么叫 Ki？', value: 'next' }],
              next: [
                { type: 'bot', delay: 1500, content: '不知道你有没有看过《比悲伤更悲伤的故事》' },
                { type: 'bot', delay: 1500, content: '嗨，我是k，如果有下辈子的话，' },
                { type: 'bot', delay: 1500, content: '“我想当戒指，眼镜，床和笔记本，这样的话，我就可以...”' },
                { type: 'bot', delay: 1500, content: '当然跟这个没有关系哈哈' },
                {
                  type: 'buttons', delay: 1500,
                  options: [{ text: '结束对话', value: 'end' }],
                  next: [
                    { type: 'bot', delay: 1500, content: '感谢你的来访，祝你们幸福长长久久~' }
                  ]
                }
              ]
            }
          ]
        },
        {
          text: '结束介绍', value: 'gg',
          next: [
            { type: 'bot', delay: 1500, content: ' ![告辞](/assets/portal/img/goodbye.webp) ' }
          ]
        }
      ]
    }
  ]
};

/** 生成占位照片（内联 SVG，保证离线可用；from/to 为渐变色，缺省为原站蓝粉渐变） */
function mockPhoto(label, from, to) {
  const f = from || '#ffd3d3';
  const t = to || '#cfe8ff';
  const svg = '<svg xmlns="http://www.w3.org/2000/svg" width="600" height="400">'
    + '<defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1">'
    + '<stop offset="0" stop-color="' + f + '"/><stop offset="1" stop-color="' + t + '"/></linearGradient></defs>'
    + '<rect width="600" height="400" fill="url(#g)"/>'
    + '<text x="300" y="205" font-size="26" fill="#ffffff" text-anchor="middle" font-family="serif">' + label + '</text></svg>';
  return 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svg);
}

/**
 * mock 数据响应：数组型 mock 数据按真实接口契约模拟分页
 * （{total, data: 当前页切片}，分页大小随请求 limit），使流加载逐页触发可见；
 * 其余接口原样返回内置数据。
 */
function mockResponse(apiKey, payload) {
  const data = PORTAL_MOCK[apiKey];
  if (Array.isArray(data) && payload && payload.page) {
    const page = payload.page;
    const limit = payload.limit || PAGE_SIZE;
    return {
      total: data.length,
      data: data.slice((page - 1) * limit, page * limit)
    };
  }
  return data;
}

/**
 * 统一请求口子：优先调用后端接口；接口未实现或失败时降级为 mock。
 * @param {string} apiKey PORTAL_API 的键
 * @param {object} [payload] 请求参数（GET 拼查询串，POST 为 JSON body）
 * @returns {Promise<any>} 成功且 success=true 时返回 data，否则返回 mock 数据
 */
export async function portalRequest(apiKey, payload) {
  const api = PORTAL_API[apiKey];
  try {
    let url = api.url;
    if (api.method === 'GET' && payload) {
      const qs = new URLSearchParams(payload).toString();
      if (qs) url += '?' + qs;
    }
    const res = await fetch(url, {
      method: api.method,
      headers: api.method === 'POST' ? { 'Content-Type': 'application/json' } : undefined,
      body: api.method === 'POST' ? JSON.stringify(payload || {}) : undefined
    });
    if (res.ok) {
      const json = await res.json();
      if (json && json.success) return json.data;
    }
  } catch (e) {
    /* 接口未实现或网络异常，走 mock */
  }
  console.warn('[portal] 接口 ' + apiKey + '（' + api.method + ' ' + api.url + '）未实现或请求失败，当前使用内置示例数据。后端实现后无需改前端。');
  return mockResponse(apiKey, payload);
}

/** 计算并渲染恋爱计时（首次加载与 pjax 换页后立即调用，避免空白延迟） */
function renderLoveTime(loveStartDate) {
  const birthDay = new Date(loveStartDate || siteConfig.loveStartDate);
  if (isNaN(birthDay.getTime())) return; // 配置未就绪（计时起点未拉取）时跳过本次渲染
  const today = new Date();
  const timeold = today.getTime() - birthDay.getTime();
  const msPerDay = 24 * 60 * 60 * 1000;
  const e_daysold = timeold / msPerDay;
  const daysold = Math.floor(e_daysold);
  const e_hrsold = (e_daysold - daysold) * 24;
  const hrsold = Math.floor(e_hrsold);
  const e_minsold = (e_hrsold - hrsold) * 60;
  const minsold = Math.floor((e_hrsold - hrsold) * 60);
  let seconds = Math.floor((e_minsold - minsold) * 60);
  if (seconds < 10) seconds = '0' + seconds;

  const timeLabel = document.getElementById('span_dt_dt');
  if (timeLabel !== null) {
    timeLabel.innerHTML = '这是我们一起走过的';
    document.getElementById('tian').innerHTML = daysold + '天';
    document.getElementById('shi').innerHTML = hrsold + '时';
    document.getElementById('fen').innerHTML = minsold + '分';
    document.getElementById('miao').innerHTML = seconds + '秒';
  }
}

/** 启动恋爱计时：先立即渲染一次，再进入每秒刷新循环（_running 防止 pjax 重复初始化时叠加循环） */
function showLoveTime() {
  renderLoveTime();
  if (showLoveTime._running) return;
  showLoveTime._running = true;
  window.setTimeout(function tick() {
    renderLoveTime();
    window.setTimeout(tick, 1000);
  }, 1000);
}

/** 顶栏文字随滚动变色（照搬；只绑定一次，passive 监听保证滚动流畅） */
function initHeaderScrollColor() {
  if (initHeaderScrollColor._bound) return;
  initHeaderScrollColor._bound = true;
  window.addEventListener('scroll', function () {
    const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
    document.querySelectorAll('.wenan, .alogo').forEach(function (el) {
      el.style.color = scrollTop > 500 ? '#333333' : 'rgb(97 97 97)';
    });
  }, { passive: true });
}

/** data-tip 悬浮提示已抽至 common/tooltip.js（基于 layui layer.tips，声明式接口不变） */

/** 右侧悬浮栏动作：scrollToTop 已在文件头部挂到 window（侧栏 HTML onclick 引用） */

/**
 * 照片卡片模板（照搬原站 createPhotoElement；后端数据统一经 htmlEscape 转义防注入）。
 * 相册页的懒加载与灯箱交互在页面模块 js/pages/love-photo.js 中编排。
 */
export function createPhotoElement(photo) {
  const img = htmlEscape(photo.img || '');
  const text = htmlEscape(photo.text || '');
  const date = htmlEscape(photo.date || '');
  return '<div class="img_card col-lg-4 col-md-6 col-sm-12 col-sm-x-12 photo-item">'
    + '<div class="love_img">'
    + '<img src="' + img + '" alt="' + text + '" loading="lazy" data-description="' + date + '">'
    + '<div class="words" data-tip="' + text + '" data-tip-position="top">'
    + '<i>' + date + '</i><span>' + text + '</span>'
    + '</div></div></div>';
}

/** 兼容 mock：photos 无外层 total/data 结构时 */
export function photos_list(res) {
  return Array.isArray(res) ? res : [];
}

/**
 * 留言表单：layui-form 校验规则与提交接管。
 * 校验提示文案逐字保留原站；verify/on 均为全局注册（事件委托对 pjax 动态内容
 * 同样生效），以 _bound 防止 pjax 重入导致重复绑定、重复提交。
 */
function initMessageForm(form) {
  if (initMessageForm._bound) {
    return;
  }
  initMessageForm._bound = true;

  form.verify({
    portalQQ: function (value) {
      if (value.length === 0) return '请填写QQ号码！';
      if (!/^[0-9]{6,12}$/.test(value)) return '您的QQ号码格式错误<br/>请输入由6-12位的数字<br/>组成的QQ号码！';
      if (value === '123456' || value === '100000' || value === '1234567') return '我想也许这并不是您的QQ号码...';
    },
    portalNickname: function (value) {
      if (value.length === 0) return '请填写您的昵称！';
    },
    portalContent: function (value) {
      if (value.length === 0) return '请填写您要留言的内容！';
      if (value.length <= 2) return '请填写两个字符以上的内容！';
      if (/^[0-9]+$/.test(value)) return '内容为纯数字 已被拦截！';
      if (new RegExp('[操垃圾傻逼妈]').test(value)) return '您输入的内容是违禁词<br/>请注意您的发言不文明的留言<br/>会被管理员拉进小黑屋喔';
    }
  });

  form.on('submit(message-submit)', function (data) {
    submitMessage(data.field);
    return false;
  });
}

/** 留言板：渲染（照搬原站 leavform 结构）+ 提交校验（接口走 portalRequest 口子） */
export function htmlEscape(str) {
  return String(str == null ? '' : str)
    .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

/** 门户列表每页条数：相册、留言、点点滴滴、恋爱清单统一 */
export const PAGE_SIZE = 6;

/** 流加载编排序号：每次 initLoadMore 递增，旧实例的回调据此自行终止（防 pjax 重入叠加） */
let loadMoreSeq = 0;

/**
 * 通用"加载更多"流加载编排（layui flow 按钮模式，门户列表页共用）。
 * 首屏自动加载第 1 页，之后每点击一次"加载更多"追加一页（每页 PAGE_SIZE 条）；
 * 数据加载完（含首屏不足一页）后隐藏"加载更多"按钮。
 * @param {object} o 配置项
 * @param {string} o.elem 列表容器选择器
 * @param {Function} o.fetchPage (page) => Promise<{total, data:Array}> 分页取数
 * @param {Function} o.render (item, index, page) => string 单条 HTML（调用方负责转义）
 * @param {string} [o.emptyText] 无数据提示（仅首屏为空时展示）
 * @param {Function} [o.onTotal] (total) => void 数据总数回调（留言计数等）
 * @param {Function} [o.onItems] (items, page) => void 本页数据追加后的 DOM 后处理（浮现动画等）
 */
export async function initLoadMore(o) {
  const box = document.querySelector(o.elem);
  if (!box) return;
  const token = ++loadMoreSeq;

  const [layer, flow] = await loadLayui('layer', 'flow');
  flow.load({
    elem: o.elem,
    isAuto: false,
    end: '暂无更多',
    done: function (page, next) {
      // pjax 已切走或被更新的实例取代：终止旧实例，不再发请求
      if (!box.isConnected || token !== loadMoreSeq) {
        next('', 0);
        return;
      }
      const loadIndex = layer.load(1, { shade: false });
      o.fetchPage(page).then(function (res) {
        layer.close(loadIndex);
        if (!box.isConnected || token !== loadMoreSeq) {
          next('', 0);
          return;
        }
        const items = (res && res.data) ? res.data : [];
        const total = (res && res.total) ? res.total : items.length;
        const html = items.map(function (item, i) { return o.render(item, i, page); }).join('')
          || (page === 1 ? (o.emptyText || '暂无数据') : '');
        // layui flow 的 next(html, end)：end 为 truthy 表示"还有更多"，0/false 表示到底
        next(html, page * PAGE_SIZE < total ? 1 : 0);
        if (o.onTotal) o.onTotal(total);
        if (o.onItems) o.onItems(items, page);
        // 数据加载完（含首屏不足一页）：隐藏"加载更多"按钮
        if (page * PAGE_SIZE >= total) {
          const more = box.querySelector('.layui-flow-more');
          if (more) more.style.display = 'none';
        }
      });
    }
  });
}

/** 留言卡片模板（index 与 page 合成为全局访客序号） */
function renderMessage(m, i, page) {
  const seq = (page - 1) * PAGE_SIZE + i + 1;
  return '<div class="leavform animated fadeInUp delay-03s">'
    + '<div class="textinfo">'
    + '<div class="MsgTopInfo"><i class="time" data-tip="' + htmlEscape(m.date || '') + '" data-tip-position="top">'
    + htmlEscape(m.date || '') + (m.location ? '<b class="yuan"></b>' + htmlEscape(m.location) : '')
    + '</i></div>'
    + '<div class="user_info">'
    + '<img src="' + htmlEscape(m.avatar || mockPhoto('游客')) + '">'
    + '<div class="head_content"><div class="level">访客 <b>#' + seq + '</b></div>'
    + '<span class="name">' + htmlEscape(m.nickname || '游客') + '</span></div>'
    + '</div>'
    + '<div class="text">' + htmlEscape(m.content || '') + '</div>'
    + '</div></div>';
}

/** 留言板：流加载编排（每页 6 条，提交留言后经 reloadMessages 重置重载） */
function initMessageFlow() {
  // 模板里的"留言加载中…"静态占位是覆盖式渲染时代的产物，流加载为追加式，先清掉
  const list = document.getElementById('leavingList');
  if (list) list.innerHTML = '';
  return initLoadMore({
    elem: '#leavingList',
    fetchPage: function (page) { return portalRequest('messages', { page: page, limit: PAGE_SIZE }); },
    render: renderMessage,
    emptyText: '还没有留言，来写下第一条吧~',
    onTotal: function (total) {
      const countEl = document.getElementById('leavingCount');
      if (countEl) countEl.textContent = String(total);
    }
  });
}

/** 提交留言成功后重载留言列表：清空旧内容并重建流加载 */
function reloadMessages() {
  const list = document.getElementById('leavingList');
  if (list) list.innerHTML = '';
  initMessageFlow();
}

/** 点点滴滴卡片模板 */
function renderLittle(it) {
  // 不再使用 animated fadeInUp 入场动画：pjax 局部换页时动画会让内容区
  // 先空白约 0.3 秒再淡入，表现为页面"闪烁"，与其它页面不一致
  return '<div class="card col-lg-12 col-md-12 col-sm-12 col-sm-x-12">'
    + '<div class="little_texts">'
    + '<a href="javascript:void(0)" data-id="' + (it.id || '') + '">'
    + '<div class="top-title textOneHide">' + htmlEscape(it.title || '')
    + '<svg class="little_icon" aria-hidden="true"><use xlink:href="#icon-zhankai"></use></svg>'
    + '</div></a>'
    + '<div class="info"><span><svg class="little_icon" aria-hidden="true"><use xlink:href="#icon-shoucang"></use></svg> '
    + htmlEscape(it.author || '') + ' <i>记录于</i> ' + htmlEscape(it.date || '') + '</span></div>'
    + '</div></div>';
}

/** 点点滴滴：流加载编排（每页 6 条） */
function initLittleFlow() {
  return initLoadMore({
    elem: '#littleBox',
    fetchPage: function (page) { return portalRequest('littles', { page: page, limit: PAGE_SIZE }); },
    render: renderLittle,
    emptyText: '暂无记录…'
  });
}

/** Love List 恋爱清单单条模板（照搬原站 lovelist 结构） */
function renderLoveListItem(it) {
  const icon = it.done
    ? '<i class="iconfont icon-chenggong2 com"></i>'
    : '<i class="iconfont icon-chenggong2 air"></i>';
  const span = it.done
    ? '<span class="success">' + htmlEscape(it.text || '') + '</span>'
    : '<span class="unfinished">' + htmlEscape(it.text || '') + '</span>';
  const img = it.img ? '<ul><li><img src="' + htmlEscape(it.img) + '" alt="' + htmlEscape(it.text || '') + '" loading="lazy"></li></ul>' : '<ul><li></li></ul>';
  return '<li class="cike">' + (it.done ? '<i class="iconfont icon-chenggong2 com"></i>' + span
    + '<svg class="icon" aria-hidden="true"><use xlink:href="#icon-tupian"></use></svg>'
    : '<i class="iconfont icon-chenggong2 air"></i>' + span) + img + '</li>';
}

/** 恋爱清单：流加载编排（每页 6 条） */
function initLoveListFlow() {
  return initLoadMore({
    elem: '#loveListBox',
    fetchPage: function (page) { return portalRequest('loveList', { page: page, limit: PAGE_SIZE }); },
    render: renderLoveListItem,
    emptyText: '暂无清单…'
  });
}

/**
 * 提交留言（由 layui-form 的 lay-verify 校验 + lay-submit 接管，见 message.html）。
 * 校验规则经 form.verify 注册（校验提示文案逐字保留原站），到达此处即已通过校验。
 */
async function submitMessage(field) {
  const btn = document.getElementById('leavingPost');
  if (!btn) return false;

  const qq = field.qq, name = field.name, text = field.text;

  btn.textContent = '留言提交中...';
  btn.disabled = true;
  await portalRequest('sendMessage', { qq: qq, name: name, text: text });
  toast.success('留言提交成功！', 'Like_Girl');
  btn.textContent = '留言成功';
  setTimeout(function () { btn.disabled = false; btn.textContent = '提交留言'; }, 5000);
  reloadMessages();
  return false;
}

/** QQ 号输入后自动拉取头像与昵称（照搬原站行为，第三方接口失败时提示手填） */
function initQqAvatar() {
  const qqInput = document.getElementById('QQ');
  if (!qqInput) return;
  qqInput.addEventListener('blur', function () {
    const QQ = qqInput.value.trim();
    if (QQ.length <= 0) return;
    const avatar = document.querySelector('.inputbox .avatar');
    if (avatar) avatar.src = 'https://q1.qlogo.cn/g?b=qq&nk=' + QQ + '&s=100';
    // fetch 原生不支持 timeout 选项，使用 AbortController 实现 8 秒超时
    const controller = new AbortController();
    const timer = setTimeout(function () { controller.abort(); }, 8000);
    fetch('https://v1.apizero.cn/api/qq?qq=' + encodeURIComponent(QQ), { signal: controller.signal })
      .then(function (r) { return r.json(); })
      .then(function (result) {
        clearTimeout(timer);
        if (result && result.code === 0 && result.data && result.data.name) {
          document.getElementById('nickname').value = result.data.name;
        } else {
          toast.warning('请手动填写昵称', 'Like_Girl');
        }
      })
      .catch(function () {
        clearTimeout(timer);
        toast.warning('请手动填写昵称', 'Like_Girl');
      });
  });
}

/** Love List 恋爱清单渲染（照搬原站 lovelist 结构） */

/** 关于页：对话机器人由页面内联脚本驱动（原 BotUI 的自研轻量复刻），无内容接口渲染逻辑 */

/** 站点展示配置（运行时状态）：初始为空，applySiteConfig 从站点配置（sys_config）组装后填充 */
let siteConfig = {};

/**
 * 站点展示配置：统一经 common/config.js（/config/public 口子，读取 sys_config）拉取，
 * 组装为站点视图对象后应用到 DOM。
 * 覆盖范围：头部 logo / 右侧文字说明、首屏双方名字与 QQ 头像、
 * 恋爱计时起点、页脚 ICP 备案号与链接、页脚版权行。
 * 键名契约：logo←name、slogan←site.slogan、femaleName←site.female-name、maleName←site.male-name、
 * femaleQq←site.female-qq、maleQq←site.male-qq、loveStartDate←site.love-start-date、
 * icpText←site.icp-text、copyright←copyright
 */
async function applySiteConfig() {
  const config = await loadConfig([
    'name', 'site.slogan', 'site.female-name', 'site.male-name',
    'site.female-qq', 'site.male-qq', 'site.love-start-date', 'site.icp-text', 'copyright'
  ]);
  siteConfig = {
    logo: config['name'],
    slogan: config['site.slogan'],
    femaleName: config['site.female-name'],
    maleName: config['site.male-name'],
    femaleQq: config['site.female-qq'],
    maleQq: config['site.male-qq'],
    loveStartDate: config['site.love-start-date'],
    icpText: config['site.icp-text'],
    copyright: config['copyright']
  };
  // 浏览器标签标题：站点名（配置 name）+ 页面副标题（head 片段的 title 参数）
  if (siteConfig.logo) {
    const suffix = document.title;
    if (suffix !== siteConfig.logo && suffix.indexOf(siteConfig.logo + ' — ') !== 0) {
      document.title = suffix ? siteConfig.logo + ' — ' + suffix : siteConfig.logo;
    }
  }
  const logoEl = document.querySelector('.alogo');
  if (logoEl && siteConfig.logo) logoEl.textContent = siteConfig.logo;
  const sloganEl = document.querySelector('.wenan');
  if (sloganEl && siteConfig.slogan) {
    sloganEl.textContent = siteConfig.slogan;
    const tipEl = sloganEl.closest('.word');
    if (tipEl) tipEl.setAttribute('data-tip', siteConfig.slogan);
  }
  const femaleImg = document.querySelector('.img-female img');
  if (femaleImg && siteConfig.femaleQq) femaleImg.src = qqAvatar(siteConfig.femaleQq, 640);
  const femaleName = document.querySelector('.img-female span');
  if (femaleName && siteConfig.femaleName) femaleName.textContent = siteConfig.femaleName;
  const maleImg = document.querySelector('.img-male img');
  if (maleImg && siteConfig.maleQq) maleImg.src = qqAvatar(siteConfig.maleQq, 640);
  const maleName = document.querySelector('.img-male span');
  if (maleName && siteConfig.maleName) maleName.textContent = siteConfig.maleName;
  const icpLink = document.getElementById('footerIcpLink');
  if (icpLink && siteConfig.icpText) icpLink.textContent = siteConfig.icpText;
  const copyEl = document.getElementById('footerCopyright');
  if (copyEl && siteConfig.copyright) {
    // 版权文案：sys_config 的 copyright 为年份，门户端负责拼接完整版权行
    copyEl.textContent = 'Copyright © ' + siteConfig.copyright + ' All Rights Reserved.';
  }
  // 计时起点可能被配置改变，配置就绪后重渲染计时器
  renderLoveTime(siteConfig.loveStartDate);
}

/**
 * 页面初始化 —— 首次加载与每次 pjax 局部刷新后都会调用。
 * 所有按需初始化均以元素存在性判断，保证在任意页面重复调用安全。
 */
export function initPortalPage() {
  // 站点展示配置：统一经 common/config.js（sys_config）异步拉取，就绪后覆盖
  // logo / 文字说明 / 头像 / 名字 / ICP / 版权，并按配置的计时起点重渲染计时器。
  applySiteConfig();

  initHeaderScrollColor();
  initTooltip();
  showLoveTime();

  // 首页：功能卡片整块可点（照搬原站行为）
  // 注意：这里直接走 portalNavigate 并阻断冒泡。
  // 若用 link.click() 触发合成点击，合成事件与原始事件会先后冒泡到
  // pjax 的 document 级拦截器，导致一次点击 pushState 两次——
  // 表现为"浏览器返回需要点两次才能回到上一页"。
  // 仅首页绑定：列表页的 .card 是内容卡片，内部"加载更多"等按钮
  // 若被误判为整卡跳转（卡内第一个 a 的 href 为 javascript:;），
  // 会触发 pjax.fetch('javascript:;') 使顶部进度条卡死、数据不追加。
  if (location.pathname === '/' || location.pathname === '/index.html') {
    document.querySelectorAll('.card, .card-b').forEach(function (card) {
      if (card._portalBound) return;
      card._portalBound = true;
      card.addEventListener('click', function (e) {
        const link = card.querySelector('a');
        if (!link) return;
        const href = link.getAttribute('href') || '';
        // 脚本/锚点/邮件链接不是页面跳转目标，交给各自的处理器
        if (!href || href.charAt(0) === '#' || href.indexOf('javascript:') === 0 || href.indexOf('mailto:') === 0) return;
        e.preventDefault();
        e.stopPropagation();
        window.portalNavigate(link.href);
      });
    });
  }

  // 相册页：懒加载与灯箱由页面模块 js/pages/love-photo.js 编排（经 main.js 的页面模块调度加载）

  // 点点滴滴页
  if (document.getElementById('littleBox')) initLittleFlow();

  // 留言板页
  if (document.getElementById('leavingList')) {
    initMessageFlow();
    initQqAvatar();
    // 提交接管：lay-verify 校验 + lay-submit（common 组件对 pjax 动态内容同样生效）
    loadLayui('form').then(function (m) { initMessageForm(m[0]); });
    // 浮动留言按钮：点击滚动到留言区（照搬原站 initScrollButton 行为）
    const msgBtn = document.getElementById('MessageBtn');
    const msgArea = document.getElementById('MessageArea');
    if (msgBtn && msgArea && !msgBtn._portalBound) {
      msgBtn._portalBound = true;
      msgBtn.addEventListener('click', function () {
        msgArea.scrollIntoView({ behavior: 'smooth', block: 'center' });
      });
    }
  }

  // 清单页
  if (document.getElementById('loveListBox')) initLoveListFlow();
}
