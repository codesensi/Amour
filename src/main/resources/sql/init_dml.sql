SET REFERENTIAL_INTEGRITY FALSE;

-- ============================================
-- 数据填充：sys_user（幂等插入）
-- ============================================
INSERT INTO `sys_user` (
    `id`, `username`, `password`, `nickname`, `id_card`, `email`, `phone`, `avatar`, `remark`
)
SELECT
    t.id,
    t.username,
    t.password,
    t.nickname,
    t.id_card,
    t.email,
    t.phone,
    t.avatar,
    t.remark
FROM (
         VALUES (
                    1,
                    'admin',
                    '$2a$10$U.k0b43Pwg./Jg2QQl4bMOukItbYg4aYhKsciMamtHWvp3JEF2ism',
                    '超级管理员',
                    '110101200001010001',
                    'admin@amour.com',
                    '18900000000',
                    'https://api.dicebear.com/7.x/bottts/svg?seed=admin',
                    '超级管理员'
                )
     ) AS t(id, username, password, nickname, id_card, email, phone, avatar, remark)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_user` WHERE `sys_user`.id = t.id
);

-- ============================================
-- 数据填充：sys_config（幂等插入）
-- ============================================
INSERT INTO `sys_config` (
    `id`, `key`, `value`, `type`, `group`, `status`, `remark`
)
SELECT
    t.id,
    t.key,
    t.value,
    t.type,
    t.group,
    t.status,
    t.remark
FROM (
         VALUES
             (1001, 'name', '爱慕情侣小站', 'STRING', 'name', 1, '项目名称'),
             (1002, 'version', '1.0.0', 'STRING', 'name', 1, '版本号'),
             (1003, 'author', 'codesensi', 'STRING', 'name', 1, '负责人'),
             (1004, 'copyright', '2026', 'STRING', 'name', 1, '版权年份'),
             (1005, 'avatar', 'https://api.dicebear.com/7.x/bottts/svg?seed=%s', 'STRING', 'name', 1, '用户随机头像服务地址'),
             (1006, 'demo-mode', 'false', 'BOOLEAN', 'demo-mode', 1, '演示模式开关'),
             (1007, 'captcha.enabled', 'false', 'BOOLEAN', 'captcha', 1, '验证码开关'),
             (1008, 'captcha.type', 'image', 'STRING', 'captcha', 1, '验证码类型'),
             (1009, 'captcha.image-type', 'arithmetic', 'STRING', 'captcha', 1, '图形验证码类型'),
             (1010, 'captcha.image-expire', '300', 'INTEGER', 'captcha', 1, '图形验证码过期秒'),
             (1011, 'captcha.sms-expire', '900', 'INTEGER', 'captcha', 1, '短信验证码过期秒'),
             (1012, 'captcha.sms-length', '6', 'INTEGER', 'captcha', 1, '短信验证码长度')
     ) AS t(id, key, value, type, group, status, remark)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_config` WHERE `sys_config`.id = t.id
);

SET REFERENTIAL_INTEGRITY TRUE;