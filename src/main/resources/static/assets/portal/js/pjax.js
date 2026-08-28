/* =====================================================================
 * Amour 门户局部刷新（轻量 pjax）—— 照搬 Like Girl 的 pjax 交互体验
 * 拦截站内链接：仅替换 #pjax-container 内容、更新标题并 pushState，
 * 顶部显示 NProgress 风格进度条；换页后调用 initPortalPage() 重新初始化。
 * ===================================================================== */
(function () {
  'use strict';

  /* ---------- 顶部进度条（NProgress 风格） ---------- */
  let bar = null;
  let timer = null;
  let progress = 0;

  function startProgress() {
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

  /* ---------- 链接过滤：仅拦截可 pjax 的站内页面链接 ---------- */
  function isInternalLink(a) {
    if (a.target && a.target !== '_self') return false;
    if (a.hasAttribute('download')) return false;
    const href = a.getAttribute('href') || '';
    if (!href || href.charAt(0) === '#' || href.indexOf('javascript:') === 0 || href.indexOf('mailto:') === 0) return false;
    let url;
    try {
      url = new URL(a.href, location.href);
    } catch (e) {
      return false;
    }
    if (url.origin !== location.origin) return false;
    if (/\/admin(\/|$)/.test(url.pathname)) return false; // 后台管理走完整加载
    if (/\.(png|jpe?g|gif|svg|ico|css|js|zip|pdf|mp4|webp|ttf|woff2?|php\?.*)(\?|$)/i.test(url.pathname + url.search)) return false;
    return true;
  }

  /* ---------- 执行换入内容中的内联脚本（如关于页 BotUI 对话） ---------- */
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
  function navigate(url, push) {
    // 规范化目标地址；push 模式下与当前地址相同则只重渲染、不再追加历史记录，
    // 防止任何重复触发导致"返回需要点多次"的问题
    const targetUrl = new URL(url, location.href).href;
    const sameUrl = targetUrl === location.href;
    startProgress();
    fetch(targetUrl, { headers: { 'X-PJAX': 'true' } })
      .then(function (r) { return r.text(); })
      .then(function (html) {
        const doc = new DOMParser().parseFromString(html, 'text/html');
        const frag = doc.getElementById('pjax-container');
        const target = document.getElementById('pjax-container');
        if (!frag || !target) {
          // 目标页无内容区（异常响应），退回完整加载
          location.href = targetUrl;
          return;
        }
        target.innerHTML = frag.innerHTML;
        document.title = doc.title || document.title;
        executeScripts(target);
        if (push && !sameUrl) history.pushState({ pjax: true }, '', targetUrl);
        window.scrollTo(0, 0);
        if (window.initPortalPage) window.initPortalPage();
        doneProgress();
      })
      .catch(function () {
        // 请求失败退回完整加载
        location.href = targetUrl;
      });
  }

  /* ---------- 全局点击拦截（事件委托） ---------- */
  document.addEventListener('click', function (e) {
    if (e.defaultPrevented || e.button !== 0 || e.ctrlKey || e.metaKey || e.shiftKey || e.altKey) return;
    const a = e.target.closest && e.target.closest('a');
    if (!a || !isInternalLink(a)) return;
    e.preventDefault();
    navigate(a.href, true);
  });

  /* ---------- 浏览器前进/后退 ---------- */
  window.addEventListener('popstate', function () {
    navigate(location.href, false);
  });

  /* ---------- 供侧栏等脚本调用的编程式导航 ---------- */
  window.portalNavigate = function (url) {
    navigate(url, true);
  };
})();
