/**
 * Amour 站点局部刷新（pjax）通用组件 —— 门户与管理后台共用的底层能力（ES Module）。
 *
 * 拦截站内链接点击，通过 fetch 拉取目标页并仅替换内容区容器，
 * 浏览器地址栏经 history.pushState 同步更新，前进/后退同样按局部刷新处理；
 * 目标页拉取失败或结构异常时，自动降级为整页跳转。
 *
 * 本组件为原生 JS 实现，不依赖任何第三方库；各端通过 initPjax(options)
 * 注入差异配置（内容区、拦截范围、标题组装、生命周期回调）：
 *
 *   initPjax({
 *     container: '#pjax-container',   // 内容区选择器（必填）
 *     match: function (url) {...},    // 链接拦截范围（必填，url 为 URL 对象）
 *     buildTitle: function (t) {...}, // 标题组装（可选，默认直接使用目标页标题）
 *     onBeforeSwap: function (t) {...},// 内容交换前回调（清理旧页面状态，可选）
 *     onPageReady: function () {...}, // 内容注入后回调（页面级初始化，可选）
 *     progressColor: 'linear-gradient(...)' // 进度条颜色（可选，默认门户渐变）
 *   });
 *
 * 编程式导航：navigate(url)，供表单提交后跳转等场景复用。
 */

/** 运行时配置（initPjax 时写入） */
let options = null;
/** 进行中的请求控制器：再次切换时取消，避免旧响应晚到覆盖新内容 */
let pendingAbort = null;
/** 防止重复初始化导致的重复事件绑定 */
let initialized = false;

/* ---------- 顶部进度条（NProgress 风格） ---------- */
let bar = null;
let timer = null;
let progress = 0;
/** 进度条样式：类名固定 pjax-progress，颜色随配置注入（归口组件，页面无需自带样式） */
let progressStyleInjected = false;

function startProgress() {
  if (!document.body) {
    return;
  }
  if (!progressStyleInjected) {
    const style = document.createElement('style');
    style.textContent = '.pjax-progress{position:fixed;top:0;left:0;z-index:99999;width:0;height:3px;' +
      'background:' + options.progressColor + ';transition:width .2s ease,opacity .3s ease;pointer-events:none;}';
    document.head.appendChild(style);
    progressStyleInjected = true;
  }
  if (!bar) {
    bar = document.createElement('div');
    bar.className = 'pjax-progress';
    document.body.appendChild(bar);
  }
  progress = 0;
  bar.style.opacity = '1';
  bar.style.width = '0';
  timer = setInterval(function () {
    progress = Math.min(progress + Math.random() * 10, 90);
    bar.style.width = progress + '%';
  }, 200);
}

function doneProgress() {
  clearInterval(timer);
  if (!bar) return;
  bar.style.width = '100%';
  setTimeout(function () {
    bar.style.opacity = '0';
    bar.style.width = '0';
  }, 300);
}

/* ---------- 链接通用排除规则 ---------- */

/**
 * 链接是否可拦截为局部刷新（通用规则）：
 * 同源、普通左键、无修饰键、非新窗口/下载、非脚本/锚点/邮件链接。
 * 端级范围（哪些路径属于本端）由 options.match 补充判断。
 */
function isPjaxLink(link) {
  if (link.target && link.target !== '_self') return false;
  if (link.hasAttribute('download')) return false;
  const href = link.getAttribute('href') || '';
  if (!href || href.charAt(0) === '#' || href.indexOf('javascript:') === 0 || href.indexOf('mailto:') === 0) return false;
  let url;
  try {
    url = new URL(link.href, location.href);
  } catch (e) {
    return false;
  }
  if (url.origin !== location.origin) return false;
  if (options.match && !options.match(url)) return false;
  return true;
}

/* ---------- 执行换入内容中的内联脚本（随容器交换后重新执行） ---------- */
function executeScripts(container) {
  container.querySelectorAll('script').forEach(function (old) {
    const s = document.createElement('script');
    if (old.src) {
      s.src = old.src;
    } else {
      s.textContent = old.textContent;
    }
    old.parentNode.removeChild(old);
    document.body.appendChild(s);
  });
}

/* ---------- 核心：拉取目标页并替换内容区 ---------- */
export function navigate(url, push) {
  if (!options) {
    // 未初始化时按完整加载处理
    window.location.href = url;
    return;
  }
  // 规范化目标地址；push 模式下与当前地址相同则只重渲染、不再追加历史记录，
  // 防止任何重复触发导致"返回需要点多次"的问题
  const targetUrl = new URL(url, location.href).href;
  const sameUrl = targetUrl === location.href;
  // 取消上一次未完成的请求，防止旧响应晚到覆盖新内容
  if (pendingAbort) {
    pendingAbort.abort();
    pendingAbort = null;
  }
  startProgress();
  const aborter = new AbortController();
  pendingAbort = aborter;
  fetch(targetUrl, { headers: { 'X-PJAX': 'true' }, cache: 'no-store', signal: aborter.signal })
    .then(function (r) { return r.text(); })
    .then(function (html) {
      const doc = new DOMParser().parseFromString(html, 'text/html');
      const frag = doc.querySelector(options.container);
      const target = document.querySelector(options.container);
      if (!frag || !target) {
        // 目标页无内容区（异常响应），退回完整加载
        window.location.href = targetUrl;
        return;
      }
      if (options.onBeforeSwap) {
        options.onBeforeSwap(target);
      }
      // 必须先滚回顶部再交换内容：若先交换，新页面高度可能骤降，
      // 合成器会立即钳制滚动位置并提交一帧"新内容停在中部"的画面，表现为页面闪烁；
      // 旧内容仍在时回顶高度充足无钳制，回顶与交换合并为同一次绘制
      window.scrollTo(0, 0);
      target.innerHTML = frag.innerHTML;
      document.title = options.buildTitle ? options.buildTitle(doc.title) : (doc.title || document.title);
      executeScripts(target);
      if (push && !sameUrl) history.pushState({ pjax: true }, '', targetUrl);
      if (options.onPageReady) options.onPageReady();
      doneProgress();
    })
    .catch(function (e) {
      // 主动取消的请求不降级跳转（新请求已在途）
      if (e && e.name === 'AbortError') return;
      // 请求失败退回完整加载
      window.location.href = targetUrl;
    })
    .finally(function () {
      if (pendingAbort === aborter) {
        pendingAbort = null;
      }
    });
}

/* ---------- 初始化：注册事件委托与浏览器历史处理 ---------- */
export function initPjax(opts) {
  if (initialized) {
    return;
  }
  options = opts;
  initialized = true;

  // 全局点击拦截（事件委托，覆盖外壳与内容区里的所有链接）
  document.addEventListener('click', function (e) {
    if (e.defaultPrevented || e.button !== 0 || e.ctrlKey || e.metaKey || e.shiftKey || e.altKey) return;
    const link = e.target.closest && e.target.closest('a');
    if (!link || !isPjaxLink(link)) return;
    e.preventDefault();
    navigate(link.href, true);
  });

  // 浏览器前进/后退
  window.addEventListener('popstate', function () {
    navigate(window.location.href, false);
  });
}
