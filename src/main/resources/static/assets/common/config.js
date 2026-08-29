/**
 * 项目配置通用读取模块 —— 前端统一的项目配置（sys_config 表）查询口子。
 *
 * 适用场景：管理后台登录页（验证码开关）、门户首页（logo/页脚/版权等）等多处
 * 需要读取项目配置的地方，统一经由此模块请求，避免各页面自行封装。
 *
 * 约定与特性：
 * - 配置值统一以字符串返回（与后端 ConfigService 约定一致），布尔判断用 isOn()；
 * - load() 按查询组缓存 Promise：同一页面相同查询组多处调用只发一次请求；
 * - mock 阶段直接读取内置 MOCK_CONFIG 表；后端公开配置接口实现后，
 *   仅需将 MOCK_CONFIG_API 改为 false，所有调用方零改动自动切换为真实数据。
 *
 * 后端接口契约（未实现，预留口子）：
 *   GET /config/public?keys=key1,key2
 *   -> Result{success, code, msg, data: { key1: 'value1', key2: 'value2' }}
 *   未启用或不存在的配置键不出现在 data 中。
 */
(function (global) {
  'use strict';

  /** mock 开关：后端公开配置接口实现后改为 false */
  var MOCK_CONFIG_API = true;

  /** 后端公开配置接口地址（实现后生效） */
  var API_URL = '/config/public';

  /**
   * mock 配置表：对齐 sql/init_dml.sql 中 sys_config 初始数据。
   * 其中 captcha.enabled mock 为 'true' 以便预览登录页验证码行为（初始数据为 'false'）；
   * site 分组为门户站点配置，门户 logo 复用 name、版权行由 copyright 年份拼接，故未单独建键。
   * 后端接口实现后以真实配置为准。
   */
  var MOCK_CONFIG = {
    'name': '爱慕情侣小站',
    'version': '1.0.0',
    'author': 'codesensi',
    'copyright': '2026',
    'avatar': 'https://api.dicebear.com/7.x/bottts/svg?seed=%s',
    'demo-mode': 'false',
    'captcha.enabled': 'true',
    'captcha.image-type': 'arithmetic',
    'captcha.image-expire': '300',
    'site.slogan': '爱晨雾漫过青瓦，爱暮色染透篱笆，更爱与君并肩立，看遍这人间烟火里的朝暮与年华。',
    'site.female-name': 'Su',
    'site.male-name': 'Li',
    'site.female-qq': '673822943',
    'site.male-qq': '2623669948',
    'site.love-start-date': '2018-07-15T00:00:00',
    'site.icp-text': '赣ICP备2026010001号'
  };

  /** 缓存：查询组（keys 逗号串）-> Promise<配置 map> */
  var cache = {};

  /** mock 数据源：从内置配置表过滤出命中的键 */
  function mockLoad(keys) {
    var map = {};
    keys.forEach(function (key) {
      if (Object.prototype.hasOwnProperty.call(MOCK_CONFIG, key)) {
        map[key] = MOCK_CONFIG[key];
      }
    });
    return Promise.resolve(map);
  }

  /** 真实数据源：按后端接口契约批量查询 */
  function remoteLoad(keys) {
    var query = keys.map(function (key) { return encodeURIComponent(key); }).join(',');
    return fetch(API_URL + '?keys=' + query)
      .then(function (response) { return response.json(); })
      .then(function (res) {
        return res && res.success && res.data ? res.data : {};
      });
  }

  /**
   * 批量查询项目配置。
   * @param {string|string[]} keys 配置键（如 'captcha.enabled'），建议一次批量查询
   * @returns {Promise<Object>} key -> 字符串值 的映射
   */
  function load(keys) {
    var list = Array.isArray(keys) ? keys : [keys];
    var group = list.join(',');
    if (!cache[group]) {
      cache[group] = MOCK_CONFIG_API ? mockLoad(list) : remoteLoad(list);
    }
    return cache[group];
  }

  /**
   * 查询单个配置项，返回 Promise<字符串值>（配置不存在时为 undefined）。
   */
  function get(key) {
    return load([key]).then(function (map) { return map[key]; });
  }

  /**
   * 布尔型配置便捷判断：值为 'true'（忽略大小写）即视为开启。
   */
  function isOn(key) {
    return get(key).then(function (value) { return String(value).toLowerCase() === 'true'; });
  }

  /**
   * 拼接 QQ 头像地址：nk 为 QQ 号，s 为尺寸（门户首页 640、留言列表 100），size 缺省 640。
   */
  function qqAvatar(qq, size) {
    return 'https://q1.qlogo.cn/g?b=qq&nk=' + encodeURIComponent(qq) + '&s=' + (size || 640);
  }

  global.ProjectConfig = {
    load: load,
    get: get,
    isOn: isOn,
    qqAvatar: qqAvatar
  };
})(window);
