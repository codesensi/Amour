/* =====================================================================
 * Amour 门户脚本 —— 照搬 Like Girl 5.2.1
 * 职责：站点配置、恋爱计时器、data-tip 悬浮提示、顶栏滚动变色、
 *       右侧悬浮栏交互、以及"未实现后端接口"的统一请求口子（mock 降级）。
 * 原站版权：Copyright (c) 2023 - 2025 by Ki（Like Girl）
 * ===================================================================== */

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
  /** 恋爱清单：GET /love/list -> [{text, done, date, img}] */
  loveList: { url: '/love/list', method: 'GET' },
  /** 点点滴滴文章列表：GET /love/littles -> [{id, title, author, date}] */
  littles: { url: '/love/littles', method: 'GET' },
  /**
   * 关于页对话剧本：GET /love/chat -> 节点数组（后台可配置，预留口子）
   * 节点两种：{type:'bot', delay, content} 消息（content 支持 "![alt](url)" 图片语法）；
   *          {type:'buttons', delay, options:[{text, value, next:[...]}]} 分支按钮，next 为点选后继续执行的子序列
   */
  aboutChat: { url: '/love/chat', method: 'GET' },
  /**
   * 站点展示配置：GET /site/config -> {logo, slogan, femaleName, maleName, femaleAvatar,
   * maleAvatar, loveStartDate, icpText, icpUrl, copyright}（后台可配置，预留口子）
   */
  siteConfig: { url: '/site/config', method: 'GET' }
};

/** 示例数据：仅在后端接口未实现时兜底展示 */
const PORTAL_MOCK = {
  photos: [
    { img: mockPhoto('第一次旅行的海边'), text: '第一次旅行的海边', date: '2023-05-20' },
    { img: mockPhoto('巷口的黄昏'), text: '巷口的黄昏', date: '2023-08-13' },
    { img: mockPhoto('冬天的第一场雪'), text: '冬天的第一场雪', date: '2023-12-16' },
    { img: mockPhoto('一起逛的夜市'), text: '一起逛的夜市', date: '2024-06-01' },
    { img: mockPhoto('你拍的晚霞'), text: '你拍的晚霞', date: '2024-09-15' },
    { img: mockPhoto('周年纪念日的晚餐'), text: '周年纪念日的晚餐', date: '2025-07-15' }
  ],
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
  /** 站点展示配置默认值（后台实现 /site/config 后自动切换为配置数据） */
  siteConfig: {
    logo: '龙猫の爱恋',
    slogan: '爱晨雾漫过青瓦，爱暮色染透篱笆，更爱与君并肩立，看遍这人间烟火里的朝暮与年华。',
    femaleName: 'Su',
    maleName: 'Li',
    femaleAvatar: 'https://q1.qlogo.cn/g?b=qq&nk=673822943&s=640',
    maleAvatar: 'https://q1.qlogo.cn/g?b=qq&nk=2623669948&s=640',
    loveStartDate: '2018-07-15T00:00:00',
    icpText: '赣ICP备2026010001号',
    icpUrl: 'https://beian.miit.gov.cn/#/Integrated/index',
    copyright: 'Copyright © 2022 - 2026 Like_Girl All Rights Reserved.'
  },
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

/** 生成占位照片（内联 SVG，保证离线可用） */
function mockPhoto(label) {
  const svg = '<svg xmlns="http://www.w3.org/2000/svg" width="600" height="400">'
    + '<defs><linearGradient id="g" x1="0" y1="0" x2="1" y2="1">'
    + '<stop offset="0" stop-color="#ffd3d3"/><stop offset="1" stop-color="#cfe8ff"/></linearGradient></defs>'
    + '<rect width="600" height="400" fill="url(#g)"/>'
    + '<text x="300" y="205" font-size="26" fill="#ffffff" text-anchor="middle" font-family="serif">' + label + '</text></svg>';
  return 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svg);
}

/**
 * 统一请求口子：优先调用后端接口；接口未实现或失败时降级为 mock。
 * @param {string} apiKey PORTAL_API 的键
 * @param {object} [payload] 请求参数（GET 拼查询串，POST 为 JSON body）
 * @returns {Promise<any>} 成功且 success=true 时返回 data，否则返回 mock 数据
 */
async function portalRequest(apiKey, payload) {
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
  return PORTAL_MOCK[apiKey];
}

/** 计算并渲染恋爱计时（首次加载与 pjax 换页后立即调用，避免空白延迟） */
function renderLoveTime(loveStartDate) {
  const birthDay = new Date(loveStartDate || siteConfig.loveStartDate);
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

/** 顶栏文字随滚动变色（照搬） */
function initHeaderScrollColor() {
  window.onscroll = function () {
    const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
    document.querySelectorAll('.wenan, .alogo').forEach(function (el) {
      el.style.color = scrollTop > 500 ? '#333333' : 'rgb(97 97 97)';
    });
  };
}

/** data-tip 自定义悬浮提示（照搬；事件委托方式绑定一次，pjax 换页后自动生效） */
function initTooltip() {
  if (initTooltip._bound) return;
  initTooltip._bound = true;
  let $tooltip = null;
  function show(el) {
    const text = el.getAttribute('data-tip') || '';
    const position = el.getAttribute('data-tip-position') || 'top';
    if (!$tooltip) {
      $tooltip = document.createElement('div');
      $tooltip.className = 'custom-tooltip';
      document.body.appendChild($tooltip);
    }
    $tooltip.textContent = text;
    $tooltip.className = 'custom-tooltip ' + position;
    $tooltip.style.visibility = 'hidden';
    $tooltip.style.display = 'block';
    const rect = el.getBoundingClientRect();
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
    const scrollLeft = window.pageXOffset || document.documentElement.scrollLeft;
    const tipWidth = $tooltip.offsetWidth;
    const tipHeight = $tooltip.offsetHeight;
    let top = 0, left = 0;
    if (position === 'bottom') { top = rect.bottom + scrollTop + 10; left = rect.left + scrollLeft + (rect.width - tipWidth) / 2; }
    else { top = rect.top + scrollTop - tipHeight - 10; left = rect.left + scrollLeft + (rect.width - tipWidth) / 2; }
    $tooltip.style.top = top + 'px';
    $tooltip.style.left = left + 'px';
    $tooltip.style.visibility = 'visible';
    $tooltip.style.opacity = '1';
  }
  function hide() { if ($tooltip) { $tooltip.style.opacity = '0'; $tooltip.style.visibility = 'hidden'; } }
  document.addEventListener('mouseover', function (e) {
    const el = e.target.closest('[data-tip]');
    if (el) show(el);
  });
  document.addEventListener('mouseout', function (e) {
    if (e.target.closest('[data-tip]')) hide();
  });
  window.addEventListener('scroll', hide);
}

/** 右侧悬浮栏动作 */
function scrollToTop(duration) {
  const d = duration || 500;
  const start = window.pageYOffset;
  const step = start / (d / 15);
  const timer = setInterval(function () {
    const cur = window.pageYOffset - step;
    if (cur <= 0) { window.scrollTo(0, 0); clearInterval(timer); }
    else { window.scrollTo(0, cur); }
  }, 15);
}

/** 打开管理后台 */
function portalGoAdmin() { window.open(PORTAL_CONFIG.adminUrl, '_blank'); }

/** 打开开源地址 */
function portalGoRepo() { window.open(PORTAL_CONFIG.repoUrl, '_blank'); }

/** Love Photo 相册：分页加载 + 逐张浮现动画（照搬原站模板，接口走 portalRequest 口子） */
let photoPage = 1;
const photoLimit = 6;

/** 照片卡片模板（照搬原站 createPhotoElement） */
function createPhotoElement(photo) {
  return '<div class="img_card col-lg-4 col-md-6 col-sm-12 col-sm-x-12 photo-item">'
    + '<div class="love_img">'
    + '<img src="' + photo.img + '" alt="' + (photo.text || '') + '" loading="lazy" data-description="' + (photo.date || '') + '">'
    + '<div class="words" data-tip="' + (photo.text || '') + '" data-tip-position="top">'
    + '<i>' + (photo.date || '') + '</i><span>' + (photo.text || '') + '</span>'
    + '</div></div></div>';
}

async function loadPhotos() {
  const gallery = document.getElementById('photoGallery');
  const loading = document.getElementById('loading');
  const loadBtn = document.getElementById('loadMoreBtn');
  if (!gallery) return;

  if (loading) loading.style.display = 'block';
  loadBtn.disabled = true;

  const res = await portalRequest('photos', { page: photoPage, limit: photoLimit });
  const photos = (res && res.data) ? res.data : (photos_list(res));
  const total = (res && res.total) ? res.total : photos.length;
  const startIndex = gallery.children.length;

  photos.forEach(function (photo) {
    const tpl = document.createElement('template');
    tpl.innerHTML = createPhotoElement(photo).trim();
    gallery.appendChild(tpl.content.firstChild);
  });

  gallery.querySelectorAll('.photo-item').forEach(function (el, idx) {
    if (idx >= startIndex) {
      setTimeout(function () { el.classList.add('show'); }, (idx - startIndex) * 300);
    }
  });

  photoPage++;
  if (loading) loading.style.display = 'none';
  if (gallery.children.length >= total) {
    loadBtn.textContent = '暂无更多数据';
    loadBtn.disabled = true;
  } else {
    loadBtn.innerHTML = '加载更多';
    loadBtn.disabled = false;
  }
}

/** 兼容 mock：photos 无外层 total/data 结构时 */
function photos_list(res) {
  return Array.isArray(res) ? res : [];
}

function resetPhotos() {
  photoPage = 1;
  const gallery = document.getElementById('photoGallery');
  if (gallery) gallery.innerHTML = '';
}

/** 留言板：渲染（照搬原站 leavform 结构）+ 提交校验（接口走 portalRequest 口子） */
function htmlEscape(str) {
  return String(str == null ? '' : str)
    .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

async function loadMessages() {
  const list = document.getElementById('leavingList');
  if (!list) return;
  const data = await portalRequest('messages', { page: 1, limit: 100 });
  const messages = (data && data.data) ? data.data : (Array.isArray(data) ? data : []);
  const countEl = document.getElementById('leavingCount');
  if (countEl) countEl.textContent = String(messages.length);
  list.innerHTML = messages.map(function (m, i) {
    return '<div class="leavform animated fadeInUp delay-03s">'
      + '<div class="textinfo">'
      + '<div class="MsgTopInfo"><i class="time" data-tip="' + htmlEscape(m.date || '') + '" data-tip-position="top">'
      + htmlEscape(m.date || '') + (m.location ? '<b class="yuan"></b>' + htmlEscape(m.location) : '')
      + '</i></div>'
      + '<div class="user_info">'
      + '<img src="' + (m.avatar || mockPhoto('游客')) + '">'
      + '<div class="head_content"><div class="level">访客 <b>#' + (i + 1) + '</b></div>'
      + '<span class="name">' + htmlEscape(m.nickname || '游客') + '</span></div>'
      + '</div>'
      + '<div class="text">' + htmlEscape(m.content || '') + '</div>'
      + '</div></div>';
  }).join('') || '<div class="portal-empty">还没有留言，来写下第一条吧~</div>';
}

/** 提交留言（校验规则照搬原站） */
async function submitMessage() {
  const qqInput = document.getElementById('QQ');
  const nameInput = document.getElementById('nickname');
  const textInput = document.getElementById('wenben');
  const btn = document.getElementById('leavingPost');
  if (!qqInput || !nameInput || !textInput) return false;

  const qq = qqInput.value.trim();
  const name = nameInput.value.trim();
  const text = textInput.value.trim();

  if (qq.length === 0) { toastr.warning('请填写QQ号码！', 'Like_Girl'); return false; }
  if (name.length === 0) { toastr.warning('请填写您的昵称！', 'Like_Girl'); return false; }
  const qqReg = /^[0-9]{6,12}$/;
  if (!qqReg.test(qq)) { toastr.warning('您的QQ号码格式错误<br/>请输入由6-12位的数字<br/>组成的QQ号码！', 'Like_Girl'); return false; }
  if (qq === '123456' || qq === '100000' || qq === '1234567') { toastr.warning('我想也许这并不是您的QQ号码...', 'Like_Girl'); return false; }
  if (text.length === 0) { toastr.warning('请填写您要留言的内容！', 'Like_Girl'); return false; }
  if (text.length <= 2) { toastr.warning('请填写两个字符以上的内容！', 'Like_Girl'); return false; }
  if (/^[0-9]+$/.test(text)) { toastr.warning('内容为纯数字 已被拦截！', 'Like_Girl'); return false; }
  if (new RegExp('[操垃圾傻逼妈]').test(text)) { toastr.warning('您输入的内容是违禁词<br/>请注意您的发言不文明的留言<br/>会被管理员拉进小黑屋喔', 'Like_Girl'); return false; }

  btn.textContent = '留言提交中...';
  btn.disabled = true;
  await portalRequest('sendMessage', { qq: qq, name: name, text: text });
  toastr.success('留言提交成功！', 'Like_Girl');
  btn.textContent = '留言成功';
  setTimeout(function () { btn.disabled = false; btn.textContent = '提交留言'; }, 5000);
  loadMessages();
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
    fetch('https://v1.apizero.cn/api/qq?qq=' + encodeURIComponent(QQ), { timeout: 8000 })
      .then(function (r) { return r.json(); })
      .then(function (result) {
        if (result && result.code === 0 && result.data && result.data.name) {
          document.getElementById('nickname').value = result.data.name;
        } else {
          toastr.warning('请手动填写昵称', 'Like_Girl');
        }
      })
      .catch(function () { toastr.warning('请手动填写昵称', 'Like_Girl'); });
  });
}

/** 点点滴滴：文章卡片列表渲染 */
async function loadLittles() {
  const box = document.getElementById('littleBox');
  if (!box) return;
  const littles = await portalRequest('littles') || [];
  box.innerHTML = littles.map(function (it) {
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
  }).join('') || '<div class="portal-empty">暂无记录…</div>';
}

/** Love List 恋爱清单渲染（照搬原站 lovelist 结构） */
async function loadLoveList() {
  const list = document.getElementById('loveListBox');
  if (!list) return;
  const items = await portalRequest('loveList') || [];
  list.innerHTML = items.map(function (it) {
    const icon = it.done
      ? '<i class="iconfont icon-chenggong2 com"></i>'
      : '<i class="iconfont icon-chenggong2 air"></i>';
    const span = it.done
      ? '<span class="success">' + htmlEscape(it.text || '') + '</span>'
      : '<span class="unfinished">' + htmlEscape(it.text || '') + '</span>';
    const img = it.img ? '<ul><li><img src="' + it.img + '" alt="' + htmlEscape(it.text || '') + '" loading="lazy"></li></ul>' : '<ul><li></li></ul>';
    return '<li class="cike">' + (it.done ? '<i class="iconfont icon-chenggong2 com"></i>' + span
      + '<svg class="icon" aria-hidden="true"><use xlink:href="#icon-tupian"></use></svg>'
      : '<i class="iconfont icon-chenggong2 air"></i>' + span) + img + '</li>';
  }).join('');
}

/** 关于页：对话机器人由页面内联脚本驱动（原 BotUI 的自研轻量复刻），无内容接口渲染逻辑 */

/** 站点展示配置（运行时状态）：初始为内置默认值，applySiteConfig 拉取后台配置后整体替换 */
let siteConfig = PORTAL_MOCK.siteConfig;

/**
 * 站点展示配置：拉取并应用到 DOM（后台可配置，预留口子）。
 * 覆盖范围：头部 logo / 右侧文字说明、首屏双方名字与 QQ 头像、
 * 恋爱计时起点、页脚 ICP 备案号与链接、页脚版权行。
 * 接口未实现或失败时 portalRequest 降级为 PORTAL_MOCK.siteConfig，
 * 应用结果与 Thymeleaf 片段内置文案一致，页面无感。
 */
async function applySiteConfig() {
  const config = await portalRequest('siteConfig');
  if (!config) return;
  siteConfig = config;
  const logoEl = document.querySelector('.alogo');
  if (logoEl && config.logo) logoEl.textContent = config.logo;
  const sloganEl = document.querySelector('.wenan');
  if (sloganEl && config.slogan) {
    sloganEl.textContent = config.slogan;
    const tipEl = sloganEl.closest('.word');
    if (tipEl) tipEl.setAttribute('data-tip', config.slogan);
  }
  const femaleImg = document.querySelector('.img-female img');
  if (femaleImg && config.femaleAvatar) femaleImg.src = config.femaleAvatar;
  const femaleName = document.querySelector('.img-female span');
  if (femaleName && config.femaleName) femaleName.textContent = config.femaleName;
  const maleImg = document.querySelector('.img-male img');
  if (maleImg && config.maleAvatar) maleImg.src = config.maleAvatar;
  const maleName = document.querySelector('.img-male span');
  if (maleName && config.maleName) maleName.textContent = config.maleName;
  const icpLink = document.getElementById('footerIcpLink');
  if (icpLink && config.icpText) icpLink.textContent = config.icpText;
  if (icpLink && config.icpUrl) icpLink.href = config.icpUrl;
  const copyEl = document.getElementById('footerCopyright');
  if (copyEl && config.copyright) copyEl.textContent = config.copyright;
  // 计时起点可能被配置改变，配置就绪后重渲染计时器
  renderLoveTime(siteConfig.loveStartDate);
}

/**
 * 页面初始化 —— 首次加载与每次 pjax 局部刷新后都会调用。
 * 所有按需初始化均以元素存在性判断，保证在任意页面重复调用安全。
 */
window.initPortalPage = function () {
  // 站点展示配置：异步拉取（后台口子 /site/config），就绪后覆盖
  // logo / 文字说明 / 头像 / 名字 / ICP / 版权，并按配置的计时起点重渲染计时器。
  // 未就绪期间页面先展示 Thymeleaf 片段内置文案，与默认配置一致，无感切换。
  applySiteConfig();
  // slogan 注入（配置到达前的兜底）
  const sloganEl = document.querySelector('.wenan');
  if (sloganEl && !sloganEl.textContent.trim()) sloganEl.textContent = siteConfig.slogan;
  // 页脚版权注入（配置到达前的兜底）
  const copyEl = document.getElementById('footerCopyright');
  if (copyEl) copyEl.textContent = siteConfig.copyright;

  initHeaderScrollColor();
  initTooltip();
  showLoveTime();

  // 首页：功能卡片整块可点（照搬原站行为）
  // 注意：这里直接走 portalNavigate 并阻断冒泡。
  // 若用 link.click() 触发合成点击，合成事件与原始事件会先后冒泡到
  // pjax 的 document 级拦截器，导致一次点击 pushState 两次——
  // 表现为"浏览器返回需要点两次才能回到上一页"。
  document.querySelectorAll('.card, .card-b').forEach(function (card) {
    if (card._portalBound) return;
    card._portalBound = true;
    card.addEventListener('click', function (e) {
      const link = card.querySelector('a');
      if (!link) return;
      e.preventDefault();
      e.stopPropagation();
      window.portalNavigate(link.href);
    });
  });

  // 首页：相册初始化（photoGallery 存在时）
  if (document.getElementById('photoGallery')) {
    resetPhotos();
    loadPhotos();
    const loadBtn = document.getElementById('loadMoreBtn');
    if (loadBtn) loadBtn.addEventListener('click', loadPhotos);
  }

  // 点点滴滴页
  if (document.getElementById('littleBox')) loadLittles();

  // 留言板页
  if (document.getElementById('leavingList')) {
    loadMessages();
    initQqAvatar();
    const postBtn = document.getElementById('leavingPost');
    if (postBtn) postBtn.addEventListener('click', submitMessage);
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
  if (document.getElementById('loveListBox')) loadLoveList();
};

document.addEventListener('DOMContentLoaded', function () {
  window.initPortalPage();
});
