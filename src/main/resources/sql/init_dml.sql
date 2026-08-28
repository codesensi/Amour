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
    SELECT 1 FROM `sys_user` WHERE `sys_user`.`id` = t.id
);

-- ============================================
-- 数据填充：sys_config（幂等插入）
-- ============================================
INSERT INTO `sys_config` (
    `id`, `c_key`, `c_value`, `v_type`, `c_group`, `remark`
)
SELECT
    t.id,
    t.c_key,
    t.c_value,
    t.v_type,
    t.c_group,
    t.remark
FROM (
         VALUES
             (1001, 'name', '爱慕情侣小站', 'STRING', 'app', '项目名称'),
             (1002, 'version', '1.0.0', 'STRING', 'app', '版本号'),
             (1003, 'author', 'codesensi', 'STRING', 'app', '负责人'),
             (1004, 'copyright', '2026', 'STRING', 'app', '版权年份'),
             (1005, 'avatar', 'https://api.dicebear.com/7.x/bottts/svg?seed=%s', 'STRING', 'app', '用户随机头像服务地址'),
             (1006, 'demo-mode', 'false', 'BOOLEAN', 'app', '演示模式开关'),
             (1007, 'captcha.enabled', 'false', 'BOOLEAN', 'captcha', '验证码开关'),
             (1009, 'captcha.image-type', 'arithmetic', 'STRING', 'captcha', '图形验证码类型'),
             (1010, 'captcha.image-expire', '300', 'INTEGER', 'captcha', '图形验证码过期秒')
     ) AS t(id, c_key, c_value, v_type, c_group, remark)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_config` WHERE `sys_config`.`id` = t.id
);

SET REFERENTIAL_INTEGRITY TRUE;