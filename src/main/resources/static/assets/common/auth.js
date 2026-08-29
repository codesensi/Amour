/**
 * 站点登录态管理通用组件（ES Module）。
 *
 * 登录态由浏览器 localStorage 承载，结构与后端登录接口约定的返回体对齐：
 *   data: { accessToken: '访问令牌', expiresIn: 有效秒数，-1 代表永不过期 }
 * mock 阶段由登录页构造该结构写入本地；后端登录接口实现后，仅需将
 * 登录页中的 MOCK_LOGIN 开关改为 false，真实返回体可直接传入本模块，
 * 读写逻辑无需改动。
 */

// 登录态在 localStorage 中的键名
const AUTH_KEY = 'ADMIN_AUTH';
// 后端约定：未登录或 token 失效的业务状态码（ResultCode.UNAUTHORIZED）
const CODE_UNAUTHORIZED = 401;
// 后端登录接口约定的"永不过期"标识
const NEVER_EXPIRES = -1;
// accessToken 请求头名称：管理后台请求后端接口时统一携带
const TOKEN_HEADER = 'accessToken';
// 防止 401 重定向重复触发
let redirecting = false;

/**
 * 登录态是否已过期。
 */
function isExpired(auth) {
  return auth.expiresAt !== NEVER_EXPIRES && auth.expiresAt <= Date.now();
}

/**
 * 读取登录态，不存在或已过期返回 null（过期与损坏数据会顺带清除）。
 */
export function getAuth() {
  const raw = localStorage.getItem(AUTH_KEY);
  if (!raw) {
    return null;
  }
  try {
    const auth = JSON.parse(raw);
    if (auth && auth.accessToken && !isExpired(auth)) {
      return auth;
    }
  } catch (e) {
    // 数据损坏时按未登录处理
  }
  localStorage.removeItem(AUTH_KEY);
  return null;
}

/**
 * 是否已登录。
 */
export function isLoggedIn() {
  return getAuth() !== null;
}

/**
 * 获取当前登录用户的 accessToken，未登录时返回空字符串。
 */
export function getAccessToken() {
  const auth = getAuth();
  return auth ? auth.accessToken : '';
}

/**
 * 保存登录态。
 *
 * @param {Object} loginData 后端登录接口返回的 data：
 *                  { accessToken: 'xxx', expiresIn: 秒数，-1 代表永不过期 }
 * @param {string} [username] 登录用户名（仅用于外壳展示，可选）
 */
export function saveAuth(loginData, username) {
  const now = Date.now();
  const expiresIn = loginData && typeof loginData.expiresIn === 'number'
    ? loginData.expiresIn
    : NEVER_EXPIRES;
  localStorage.setItem(AUTH_KEY, JSON.stringify({
    accessToken: (loginData && loginData.accessToken) || '',
    username: username || '',
    loginAt: now,
    // -1 代表永不过期，其余按秒换算为绝对过期时间戳（毫秒）
    expiresAt: expiresIn === NEVER_EXPIRES ? NEVER_EXPIRES : now + expiresIn * 1000
  }));
}

/**
 * 清除登录态（退出登录时调用）。
 */
export function clearAuth() {
  localStorage.removeItem(AUTH_KEY);
}

/**
 * 清除登录态并重定向到登录页。
 */
export function redirectToLogin() {
  clearAuth();
  if (redirecting) {
    return;
  }
  redirecting = true;
  location.replace('/admin/login.html');
}

/**
 * 接口返回"未登录或 token 失效"时的统一处理。
 * 已处于登录页时仅清登录态，避免无意义的反复刷新。
 */
export function handleUnauthorized() {
  if (location.pathname.indexOf('/admin/login.html') === 0) {
    clearAuth();
    return;
  }
  redirectToLogin();
}

/**
 * 装配后端接口请求守卫（需在页面拿到 jQuery 后调用一次）：
 * 1. 管理后台发起的同源请求，统一在请求头携带当前登录用户的 accessToken；
 * 2. 接口返回 401（未登录或 token 失效）时，清除本地登录态并重定向到登录页。
 */
export function setupAjaxGuard() {
  const $ = window.jQuery;
  if (!$ || window.__AMOUR_AJAX_GUARD__) {
    return;
  }
  window.__AMOUR_AJAX_GUARD__ = true;

  // 同源请求统一携带 accessToken（跨域请求不携带，避免令牌外泄）
  $.ajaxSetup({
    beforeSend: function (xhr, settings) {
      const crossOrigin = /^[a-z][a-z0-9+.-]*:\/\//i.test(settings.url)
        && settings.url.indexOf(location.host) === -1;
      if (!crossOrigin) {
        const token = getAccessToken();
        if (token) {
          xhr.setRequestHeader(TOKEN_HEADER, token);
        }
      }
    }
  });

  // 业务层面：统一响应体 code === 401，即未登录或 token 失效
  $(document).ajaxSuccess(function (event, xhr) {
    if (xhr.responseJSON && xhr.responseJSON.code === CODE_UNAUTHORIZED) {
      handleUnauthorized();
    }
  });

  // HTTP 层面：网关等直接返回 401 时同样重定向
  $(document).ajaxError(function (event, xhr) {
    if (xhr.status === CODE_UNAUTHORIZED) {
      handleUnauthorized();
    }
  });
}
