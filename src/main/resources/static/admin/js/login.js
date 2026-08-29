/**
 * 后台登录页模块（ES Module）。
 *
 * 承接 login.html 的全部页面逻辑：layui 登录表单处理（验证码开关、mock 登录）、
 * 左屏品牌信息（站名/标语打字机/双头像/恋爱计时）与粒子心形动画。
 * 依赖页面上以普通脚本加载的 layui；
 * 登录态与项目配置经 common 组件（ES Module）读写。
 */
import { saveAuth, setupAjaxGuard } from '/assets/common/auth.js';
import { loadConfig, isOn, qqAvatar } from '/assets/common/config.js';

layui.use(['form', 'jquery', 'layer'], function () {
  const $ = layui.jquery, form = layui.form, layer = layui.layer;

  // 请求守卫：同源请求统一携带 accessToken，接口返回 401 时重定向登录页
  setupAjaxGuard();

  let captchaKey = '';

  // 验证码开关当前生效状态：开启时显示验证码并验证必填，关闭时不显示验证码相关内容
  let captchaEnabled = false;

  // 加载验证码：GET /captcha -> Result{success, code, msg, data:{captchaKey, captchaValue}}
  // captchaValue 为 base64 图片 data URI，直接作为 <img> 展示
  function loadCaptcha() {
    captchaKey = '';
    $('#captchaImg').hide();
    $.ajax({
      url: '/captcha',
      type: 'GET',
      dataType: 'json'
    }).done(function (res) {
      if (res.success && res.data) {
        captchaKey = res.data.captchaKey || '';
        $('#captchaImg').attr('src', res.data.captchaValue).show();
      } else {
        layer.msg(res.msg || '验证码加载失败', { icon: 2 });
      }
    }).fail(function (xhr) {
      layer.msg('验证码接口请求失败：' + xhr.status);
    });
  }

  // 点击图片刷新验证码
  $('#captchaBox').on('click', loadCaptcha);

  // 根据项目配置应用验证码开关：
  // 开启 -> 显示验证码行、输入框必填并加载验证码；关闭 -> 整行隐藏，不显示也不校验验证码
  function applyCaptchaConfig(enabled) {
    captchaEnabled = enabled;
    const $item = $('#captchaItem');
    const $input = $item.find('input[name="captcha"]');
    if (captchaEnabled) {
      $item.show();
      $input.attr('lay-verify', 'required');
      loadCaptcha();
    } else {
      $item.hide();
      $input.removeAttr('lay-verify');
    }
  }

  // MOCK_LOGIN = true：后端 /auth/login 尚未实现，表单校验通过后直接 mock 登录成功，
  // 写入本地登录态并跳转后台管理首页；后端登录接口实现后将其改为 false 即恢复走真实接口
  const MOCK_LOGIN = true;

  // 提交登录：POST /auth/login（后端实现后生效）
  form.on('submit(login-submit)', function (data) {
    if (MOCK_LOGIN) {
      // 构造与后端约定一致的登录返回体：data.accessToken + data.expiresIn（秒，-1 代表永不过期）
      const mockRes = {
        success: true,
        code: 200,
        msg: '登录成功',
        data: {
          accessToken: 'mock-' + Date.now() + '-' + Math.random().toString(36).slice(2),
          expiresIn: 7 * 24 * 60 * 60 // mock 默认 7 天；后端实现后以真实返回为准
        }
      };
      saveAuth(mockRes.data, data.field.username);
      // 首页读取该标记后展示一次欢迎提示
      sessionStorage.setItem('ADMIN_LOGIN_WELCOME', '1');
      layer.msg('登录成功，正在跳转…', { icon: 1, time: 800 }, function () {
        location.href = '/admin/index.html';
      });
      return false; // 阻止表单默认提交
    }
    // 仅在验证码开启时携带验证码参数
    const payload = {
      username: data.field.username,
      password: data.field.password
    };
    if (captchaEnabled) {
      payload.captcha = data.field.captcha;
      payload.captchaKey = captchaKey;
    }
    $.ajax({
      url: '/auth/login',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(payload),
      dataType: 'json'
    }).done(function (res) {
      if (res.success) {
        saveAuth(res.data, data.field.username);
        layer.msg('登录成功，正在跳转…', { icon: 1, time: 800 }, function () {
          location.href = '/admin/index.html';
        });
      } else {
        layer.msg(res.msg || '登录失败', { icon: 2 });
        loadCaptcha();
      }
    }).fail(function (xhr) {
      // 登录接口尚未实现（404）或校验失败时的兜底提示
      let msg = '登录接口请求失败：' + xhr.status;
      try { if (xhr.responseJSON && xhr.responseJSON.msg) msg = xhr.responseJSON.msg; } catch (e) { /* 忽略解析失败 */ }
      layer.msg(msg, { icon: 2 });
      loadCaptcha();
    });
    return false; // 阻止表单默认提交
  });

  // 应用项目配置的验证码开关：mock 阶段读取 config.js 内置配置表，
  // 后端公开配置接口实现后自动切换为真实数据（sys_config: captcha.enabled）
  isOn('captcha.enabled').then(applyCaptchaConfig);

  // 标语打字机效果：逐字显示，尾部光标闪烁；完成后光标停留 2s 再隐藏
  function typeSlogan(text) {
    const el = $('#brandSlogan').text('');
    let i = 0;
    el.addClass('typing');
    const timer = setInterval(function () {
      i += 1;
      el.text(text.slice(0, i));
      if (i >= text.length) {
        clearInterval(timer);
        setTimeout(function () { el.removeClass('typing'); }, 2000);
      }
    }, 80);
  }

  // 左屏品牌信息：站名/标语/双头像/恋爱计时，全部来自项目配置（mock 阶段读 config.js 内置表）
  loadConfig(['name', 'site.slogan', 'site.female-qq', 'site.male-qq', 'site.love-start-date']).then(function (config) {
    // 站名与标签标题均取自配置（name），页面内不硬编码站点名
    if (config['name']) {
      $('#brandName').text(config['name']);
      document.title = config['name'] + ' - 后台登录';
    }
    // 标语以打字机效果逐字呈现（偏好减弱动效时直接显示完整文案）
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
      $('#brandSlogan').text(config['site.slogan'] || '');
    } else {
      typeSlogan(config['site.slogan'] || '');
    }
    // 双头像：配置 QQ 号 -> 前端拼接 qlogo 地址（s=640 为合法尺寸）；加载失败回退占位图，避免破图
    const avatarFallback = 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(
      '<svg xmlns="http://www.w3.org/2000/svg" width="104" height="104"><rect width="104" height="104" fill="#123a2e"/><text x="52" y="66" font-size="46" fill="#ff7a9e" text-anchor="middle" font-family="serif">\u2665</text></svg>');
    $('#brandFemaleAvatar')
      .on('error', function () { $(this).off('error').attr('src', avatarFallback); })
      .attr('src', qqAvatar(config['site.female-qq'], 640));
    $('#brandMaleAvatar')
      .on('error', function () { $(this).off('error').attr('src', avatarFallback); })
      .attr('src', qqAvatar(config['site.male-qq'], 640));
    // 恋爱计时：按 site.love-start-date 计算"已相爱 N 天"
    const start = new Date(String(config['site.love-start-date']).replace(' ', 'T'));
    if (!isNaN(start.getTime())) {
      const days = Math.max(0, Math.floor((Date.now() - start.getTime()) / 86400000));
      $('#brandDays').html('<svg class="brand-days-heart" viewBox="0 0 24 24" aria-hidden="true"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/></svg> 我们已相爱 <span class="num">' + days + '</span> 天');
    }
  });
});

// 粒子心形：细小碎片从四散状态聚合为红色爱心，随节拍搏动；鼠标掠过时碎片轻轻避让
(function () {
  const canvas = document.getElementById('heartCanvas');
  if (!canvas || !canvas.getContext) return;
  const ctx = canvas.getContext('2d');
  const SIZE = 400;                                    // 画布逻辑尺寸
  const dpr = window.devicePixelRatio || 1;
  canvas.width = SIZE * dpr;
  canvas.height = SIZE * dpr;
  ctx.scale(dpr, dpr);

  const COLORS = ['#ff4d6d', '#ff6b81', '#e63946', '#ff8fa3', '#d90429'];
  const COUNT = 500;                                   // 碎片数量
  const reduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

  // 心形参数方程（数学系 y 向上，翻转并回正到画布中心）：x ∈ [-16,16]，y ∈ [-14.5,14.5]
  function heartXY(t) {
    const x = 16 * Math.pow(Math.sin(t), 3);
    const y = 13 * Math.cos(t) - 5 * Math.cos(2 * t) - 2 * Math.cos(3 * t) - Math.cos(4 * t);
    return { x: x, y: -y - 2.5 };
  }

  const cx = SIZE / 2, cy = SIZE / 2;
  const s = (SIZE * 0.74) / 32;                        // 心形宽度约占画布 74%
  const particles = [];
  for (let i = 0; i < COUNT; i++) {
    const t = Math.random() * Math.PI * 2;
    const p = heartXY(t);
    // 四成碎片贴住轮廓勾勒边缘，其余按面积均匀分布向内填充，直达心形中心不再空洞
    const shell = Math.random() < 0.4 ? 1 : Math.sqrt(Math.random());
    particles.push({
      tx: cx + p.x * s * shell,
      ty: cy + p.y * s * shell,
      x: Math.random() * SIZE,
      y: Math.random() * SIZE,
      size: 1.1 + Math.random() * 2.2,
      color: COLORS[i % COLORS.length],
      phase: Math.random() * Math.PI * 2,
      ease: 0.025 + Math.random() * 0.05
    });
  }

  const mouse = { x: 0, y: 0, on: false };
  canvas.addEventListener('mousemove', function (e) {
    const rect = canvas.getBoundingClientRect();
    mouse.x = (e.clientX - rect.left) * (SIZE / rect.width);
    mouse.y = (e.clientY - rect.top) * (SIZE / rect.height);
    mouse.on = true;
  });
  canvas.addEventListener('mouseleave', function () { mouse.on = false; });

  let pulse = 0, lastBeat = 0;

  function draw(now) {
    ctx.clearRect(0, 0, SIZE, SIZE);
    // 节拍：每 1.6s 触发一次搏动，碎片沿离心方向弹开后回聚
    if (now - lastBeat > 1600) { pulse = 1; lastBeat = now; }
    pulse += (0 - pulse) * 0.08;
    const k = 1 + pulse * 0.16;
    for (let i = 0; i < particles.length; i++) {
      const pt = particles[i];
      pt.x += (cx + (pt.tx - cx) * k - pt.x) * pt.ease;
      pt.y += (cy + (pt.ty - cy) * k - pt.y) * pt.ease;
      const jx = Math.sin(now / 900 + pt.phase) * 1.2;   // 微幅呼吸，避免画面呆板
      const jy = Math.cos(now / 850 + pt.phase) * 1.2;
      let ox = 0, oy = 0;
      if (mouse.on) {
        const dx = pt.x - mouse.x, dy = pt.y - mouse.y;
        const d2 = dx * dx + dy * dy;
        if (d2 < 3600 && d2 > 0.01) {                    // 半径 60 内的碎片被鼠标轻轻推开
          const d = Math.sqrt(d2);
          const f = ((60 - d) / 60) * 14;
          ox = (dx / d) * f;
          oy = (dy / d) * f;
        }
      }
      ctx.fillStyle = pt.color;
      ctx.fillRect(pt.x + jx + ox - pt.size / 2, pt.y + jy + oy - pt.size / 2, pt.size, pt.size);
    }
  }

  if (reduced) {
    // 降低动效偏好：静态绘制一次聚合完成的心形
    for (const pt of particles) { pt.x = pt.tx; pt.y = pt.ty; }
    draw(0);
  } else {
    requestAnimationFrame(function loop(now) { draw(now); requestAnimationFrame(loop); });
  }
})();
