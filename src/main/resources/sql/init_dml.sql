SET REFERENTIAL_INTEGRITY FALSE;

-- ----------------------------
-- 数据填充：sys_config（幂等插入）
-- ----------------------------
INSERT INTO `sys_config` (
    `id`, `config_key`, `config_value`, `value_type`, `config_group`, `remark`
)
SELECT
    t.id,
    t.config_key,
    t.config_value,
    t.value_type,
    t.config_group,
    t.remark
FROM (
         VALUES
             -- base（1000 段）
             (1001, 'name', '爱慕情侣小站', 'STRING', 'base', '项目/站点名称'),
             (1002, 'icp', '京ICP备2026010001号', 'STRING', 'base', 'ICP备案文案'),
             (1003, 'copyright-year', '2026', 'STRING', 'base', '版权年份'),
             (1004, 'qq-service', 'https://uapis.cn/api/v1/social/qq/userinfo?qq=%s', 'STRING', 'base', '用户QQ信息接口地址'),
             (1005, 'avatar-service', 'https://api.dicebear.com/7.x/bottts/svg?seed=%s', 'STRING', 'base', '用户随机头像服务地址'),
             -- site（2000 段）
             (2001, 'site.slogan', '爱晨雾漫过青瓦，爱暮色染透篱笆，更爱与君并肩立，看遍这人间烟火里的朝暮与年华。', 'STRING', 'site', '门户标语文案'),
             (2002, 'site.love-start-date', '2018-07-15 00:00:00', 'STRING', 'site', '门户恋爱计时起点'),
             -- captcha（3000 段）
             (3001, 'captcha.enabled', 'true', 'BOOLEAN', 'captcha', '验证码开关'),
             (3002, 'captcha.image-type', 'arithmetic', 'STRING', 'captcha', '图形验证码类型'),
             (3003, 'captcha.image-expire', '300', 'INTEGER', 'captcha', '图形验证码过期秒')
     ) AS t(id, config_key, config_value, value_type, config_group, remark)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_config` WHERE `sys_config`.`id` = t.id
);

-- ----------------------------
-- 数据填充：sys_user（幂等插入）
-- ----------------------------
INSERT INTO `sys_user` (
    `id`, `username`, `password`, `nickname`, `id_card`, `email`, `phone`, `qq`, `avatar`, `builtin`, `remark`
)
SELECT
    t.id,
    t.username,
    t.password,
    t.nickname,
    t.id_card,
    t.email,
    t.phone,
    t.qq,
    t.avatar,
    t.builtin,
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
                    '12345678',
                    'https://api.dicebear.com/7.x/bottts/svg?seed=admin',
                    1,
                    '超级管理员'
                )
     ) AS t(id, username, password, nickname, id_card, email, phone, qq, avatar, builtin, remark)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_user` WHERE `sys_user`.`id` = t.id
);

-- ----------------------------
-- 数据填充：sys_role（幂等插入）
-- ----------------------------
INSERT INTO `sys_role` (
    `id`, `name`, `code`, `sort`, `status`, `builtin`, `remark`
)
SELECT
    t.id,
    t.name,
    t.code,
    t.sort,
    t.status,
    t.builtin,
    t.remark
FROM (
         VALUES (
                     1,
                     '超级管理员',
                     'admin',
                     1,
                     0,
                     1,
                     '系统内置超级管理员角色'
                 )
     ) AS t(id, name, code, sort, status, builtin, remark)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_role` WHERE `sys_role`.`id` = t.id
);

-- ----------------------------
-- 数据填充：sys_user_role（幂等插入）
-- ----------------------------
INSERT INTO `sys_user_role` (
    `id`, `user_id`, `role_id`
)
SELECT
    t.id,
    t.user_id,
    t.role_id
FROM (
         VALUES (
                     1,
                     1,
                     1
                 )
     ) AS t(id, user_id, role_id)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_user_role` WHERE `sys_user_role`.`id` = t.id
);

-- ----------------------------
-- 数据填充：sys_menu（幂等插入）
-- ----------------------------
INSERT INTO `sys_menu` (
    `id`, `pid`, `title`, `type`, `path`, `component`, `sort`, `icon`, `perms`, `builtin`
)
SELECT
    t.id,
    t.pid,
    t.title,
    t.type,
    t.path,
    t.component,
    t.sort,
    t.icon,
    t.perms,
    t.builtin
FROM (
         VALUES
             (1000, 0, '系统管理', 'D', '/system', NULL, 1, 'ep:setting', NULL, 1),
             (1100, 1000, '系统配置', 'M', '/system/config', 'system/config/index', 1, 'ep:tools', NULL, 1),
             (1101, 1100, '分页查询', 'B', NULL, NULL, 1, NULL, 'system:config:page', 1),
             (1102, 1100, '详情', 'B', NULL, NULL, 2, NULL, 'system:config:detail', 1),
             (1103, 1100, '修改', 'B', NULL, NULL, 3, NULL, 'system:config:update', 1),
             (1104, 1100, '增加', 'B', NULL, NULL, 4, NULL, 'system:config:insert', 1),
             (1105, 1100, '删除', 'B', NULL, NULL, 5, NULL, 'system:config:delete', 1),
             (1200, 1000, '用户管理', 'M', '/system/user', 'system/user/index', 2, 'ep:user', NULL, 1),
             (1201, 1200, '分页查询', 'B', NULL, NULL, 1, NULL, 'system:user:page', 1),
             (1202, 1200, '详情', 'B', NULL, NULL, 2, NULL, 'system:user:detail', 1),
             (1203, 1200, '修改', 'B', NULL, NULL, 3, NULL, 'system:user:update', 1),
             (1204, 1200, '增加', 'B', NULL, NULL, 4, NULL, 'system:user:insert', 1),
             (1205, 1200, '删除', 'B', NULL, NULL, 5, NULL, 'system:user:delete', 1),
             (1300, 1000, '角色管理', 'M', '/system/role', 'system/role/index', 3, 'ep:avatar', NULL, 1),
             (1301, 1300, '分页查询', 'B', NULL, NULL, 1, NULL, 'system:role:page', 1),
             (1302, 1300, '详情', 'B', NULL, NULL, 2, NULL, 'system:role:detail', 1),
             (1303, 1300, '修改', 'B', NULL, NULL, 3, NULL, 'system:role:update', 1),
             (1304, 1300, '增加', 'B', NULL, NULL, 4, NULL, 'system:role:insert', 1),
             (1305, 1300, '删除', 'B', NULL, NULL, 5, NULL, 'system:role:delete', 1),
             (1400, 1000, '菜单管理', 'M', '/system/menu', 'system/menu/index', 4, 'ep:menu', NULL, 1),
             (1401, 1400, '分页查询', 'B', NULL, NULL, 1, NULL, 'system:menu:page', 1),
             (1402, 1400, '详情', 'B', NULL, NULL, 2, NULL, 'system:menu:detail', 1),
             (1403, 1400, '修改', 'B', NULL, NULL, 3, NULL, 'system:menu:update', 1),
             (1404, 1400, '增加', 'B', NULL, NULL, 4, NULL, 'system:menu:insert', 1),
             (1405, 1400, '删除', 'B', NULL, NULL, 5, NULL, 'system:menu:delete', 1),
             (2000, 0, '日志管理', 'D', '/log', NULL, 2, 'ep:document', NULL, 1),
             (2100, 2000, '登录日志', 'M', '/log/login', 'log/login/index', 1, 'ep:key', NULL, 1),
             (2101, 2100, '分页查询', 'B', NULL, NULL, 1, NULL, 'log:login:page', 1),
             (2102, 2100, '详情', 'B', NULL, NULL, 2, NULL, 'log:login:detail', 1),
             (2200, 2000, '操作日志', 'M', '/log/operate', 'log/operate/index', 2, 'ep:list', NULL, 1),
             (2201, 2200, '分页查询', 'B', NULL, NULL, 1, NULL, 'log:operate:page', 1),
             (2202, 2200, '详情', 'B', NULL, NULL, 2, NULL, 'log:operate:detail', 1)
     ) AS t(id, pid, title, type, path, component, sort, icon, perms, builtin)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `sys_menu`.`id` = t.id
);

SET REFERENTIAL_INTEGRITY TRUE;