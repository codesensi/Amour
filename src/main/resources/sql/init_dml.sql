SET REFERENTIAL_INTEGRITY FALSE;

-- ----------------------------
-- 数据填充：sys_config（幂等插入）
-- ----------------------------
INSERT INTO `sys_config` (
    `id`, `config_key`, `config_value`, `value_type`, `config_group`, `sensitive`, `remark`, `creator`
)
SELECT
    t.id,
    t.config_key,
    t.config_value,
    t.value_type,
    t.config_group,
    t.sensitive,
    t.remark,
    t.creator
FROM (
         VALUES
             -- base（1000 段）
             (1001, 'name', '爱慕情侣小站', 'STRING', 'base', 0, '项目/站点名称', 1),
             (1002, 'icp', '京ICP备2026010001号', 'STRING', 'base', 0, 'ICP备案文案', 1),
             (1003, 'copyright-year', '2026', 'STRING', 'base', 0, '版权年份', 1),
             (1005, 'trust-proxy-headers', 'true', 'BOOLEAN', 'base', 0, '是否信任X-Real-IP等代理头(仅部署于可信反向代理后开启)', 1),
             (1006, 'logo', NULL, 'STRING', 'base', 0, '项目/站点logo图片(登录页/管理端/门户端统一)', 1),
             (1007, 'favicon', NULL, 'STRING', 'base', 0, '项目/站点favicon图标(登录页/管理端/门户端统一)', 1),
             -- site（2000 段）
             (2001, 'site.love-start-date', '2018-07-15 00:00:00', 'DATETIME', 'site', 0, '门户恋爱计时起点', 1),
             -- captcha（3000 段）
             (3001, 'captcha.enabled', 'true', 'BOOLEAN', 'captcha', 0, '验证码开关', 1),
             (3002, 'captcha.image-type', 'arithmetic', 'STRING', 'captcha', 0, '图形验证码类型', 1),
             -- file（4000 段）
             (4001, 'file.storage', 'local', 'STRING', 'file', 0, '文件存储方式: local-本地, oss-对象存储', 1),
             -- rate-limit（5000 段）
             (5001, 'rate-limit.login.limit', '5', 'INTEGER', 'rate-limit', 0, '登录接口-窗口内最大请求数(0 表示拒绝全部请求)', 1),
             (5002, 'rate-limit.login.window', '60', 'INTEGER', 'rate-limit', 0, '登录接口-时间窗口(秒)', 1),
             (5003, 'rate-limit.captcha.limit', '10', 'INTEGER', 'rate-limit', 0, '验证码接口-窗口内最大请求数(0 表示拒绝全部请求)', 1),
             (5004, 'rate-limit.captcha.window', '60', 'INTEGER', 'rate-limit', 0, '验证码接口-时间窗口(秒)', 1),
             (5005, 'rate-limit.qq-info.limit', '10', 'INTEGER', 'rate-limit', 0, 'QQ信息接口-窗口内最大请求数(0 表示拒绝全部请求)', 1),
             (5006, 'rate-limit.qq-info.window', '60', 'INTEGER', 'rate-limit', 0, 'QQ信息接口-时间窗口(秒)', 1),
             (5007, 'rate-limit.amap-proxy.limit', '30', 'INTEGER', 'rate-limit', 0, '高德服务代理接口-窗口内最大请求数(0 表示拒绝全部请求)', 1),
             (5008, 'rate-limit.amap-proxy.window', '60', 'INTEGER', 'rate-limit', 0, '高德服务代理接口-时间窗口(秒)', 1),
             (5009, 'rate-limit.saying.limit', '10', 'INTEGER', 'rate-limit', 0, '一言接口-窗口内最大请求数(0 表示拒绝全部请求)', 1),
             (5010, 'rate-limit.saying.window', '60', 'INTEGER', 'rate-limit', 0, '一言接口-时间窗口(秒)', 1),
             (5011, 'rate-limit.message.limit', '3', 'INTEGER', 'rate-limit', 0, '留言提交接口-窗口内最大请求数(0 表示拒绝全部请求)', 1),
             (5012, 'rate-limit.message.window', '60', 'INTEGER', 'rate-limit', 0, '留言提交接口-时间窗口(秒)', 1),
             -- security（6000 段）
             (6001, 'security.uapi-key', NULL, 'STRING', 'security', 1, 'UApiPro接口密钥(https://uapis.cn)', 1),
             (6002, 'security.amap-key', NULL, 'STRING', 'security', 0, '高德地图Web端JS API Key(足迹地图选点/门户足迹地图展示;配域名白名单防滥用)', 1),
             (6003, 'security.amap-code', NULL, 'STRING', 'security', 1, '高德地图安全密钥(与Web端JS API Key配套,空表示未启用)', 1)
     ) AS t(id, config_key, config_value, value_type, config_group, sensitive, remark, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_config` WHERE `sys_config`.`id` = t.id
);

-- ----------------------------
-- 数据填充：sys_user（幂等插入）
-- ----------------------------
INSERT INTO `sys_user` (
    `id`, `username`, `password`, `nickname`, `email`, `gender`, `qq`, `avatar`, `builtin`, `password_updated`, `remark`, `creator`
)
SELECT
    t.id,
    t.username,
    t.password,
    t.nickname,
    t.email,
    t.gender,
    t.qq,
    t.avatar,
    t.builtin,
    t.password_updated,
    t.remark,
    t.creator
FROM (
         VALUES
             (1,
              'admin',
              '$2a$10$U.k0b43Pwg./Jg2QQl4bMOukItbYg4aYhKsciMamtHWvp3JEF2ism',
              '超级管理员',
              'admin@amour.com',
              'U',
              '12345678',
              NULL,
              1,
              0,
              '系统内置超级管理员', 1),
             (2,
              'li',
              '$2a$10$U.k0b43Pwg./Jg2QQl4bMOukItbYg4aYhKsciMamtHWvp3JEF2ism',
              'Li',
              'li@amour.com',
              'M',
              '2623669948',
              NULL,
              1,
              0,
              '系统内置门户男主', 1),
             (3,
              'su',
              '$2a$10$U.k0b43Pwg./Jg2QQl4bMOukItbYg4aYhKsciMamtHWvp3JEF2ism',
              'Su',
              'su@amour.com',
              'F',
              '673822943',
              NULL,
              1,
              0,
              '系统内置门户女主', 1)
     ) AS t(id, username, password, nickname, email, gender, qq, avatar, builtin, password_updated, remark, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_user` WHERE `sys_user`.`id` = t.id
);

-- ----------------------------
-- 数据填充：sys_role（幂等插入）
-- ----------------------------
INSERT INTO `sys_role` (
    `id`, `name`, `code`, `sort`, `builtin`, `remark`, `creator`
)
SELECT
    t.id,
    t.name,
    t.code,
    t.sort,
    t.builtin,
    t.remark,
    t.creator
FROM (
         VALUES
             (1, '超级管理员', 'admin', 1, 1, '系统内置超级管理员角色', 1),
             (2, '主角', 'hero', 2, 1, '系统内置门户主角角色', 1)
     ) AS t(id, name, code, sort, builtin, remark, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_role` WHERE `sys_role`.`id` = t.id
);

-- ----------------------------
-- 数据填充：sys_user_role（幂等插入）
-- ----------------------------
INSERT INTO `sys_user_role` (
    `id`, `user_id`, `role_id`, `creator`
)
SELECT
    t.id,
    t.user_id,
    t.role_id,
    t.creator
FROM (
         VALUES
             (1, 1, 1, 1),
             (2, 2, 2, 1),
             (3, 3, 2, 1)
     ) AS t(id, user_id, role_id, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_user_role` WHERE `sys_user_role`.`id` = t.id
);

-- ----------------------------
-- 数据填充：sys_role_menu（幂等插入）
-- 主角角色绑定除系统管理（1000 子树）之外的全部菜单（业务模块与个人中心）：
-- 主角经管理端与通知中心参与留言审批等日常维护；超管角色持有 *:*:* 通配权限无需绑定；
-- id 使用独立的 40000 段顺序预留（与菜单表 10000 段、种子数据 30000 段互相独立）
-- ----------------------------
INSERT INTO `sys_role_menu` (
    `id`, `role_id`, `menu_id`, `creator`
)
SELECT
    t.id,
    t.role_id,
    t.menu_id,
    t.creator
FROM (
         VALUES
             -- 点点滴滴（2000 段）
             (40001, 2, 2000, 1),
             (40002, 2, 2001, 1),
             (40003, 2, 2002, 1),
             (40004, 2, 2003, 1),
             (40005, 2, 2004, 1),
             -- 恋爱画册（2100 段）
             (40006, 2, 2100, 1),
             (40007, 2, 2101, 1),
             (40008, 2, 2102, 1),
             (40009, 2, 2103, 1),
             (40010, 2, 2104, 1),
             -- 恋爱清单（2200 段）
             (40011, 2, 2200, 1),
             (40012, 2, 2201, 1),
             (40013, 2, 2202, 1),
             (40014, 2, 2203, 1),
             (40015, 2, 2204, 1),
             -- 留言簿（2300 段）
             (40016, 2, 2300, 1),
             (40017, 2, 2301, 1),
             (40018, 2, 2302, 1),
             (40019, 2, 2303, 1),
             (40020, 2, 2304, 1),
             -- 纪念日（2400 段）
             (40021, 2, 2400, 1),
             (40022, 2, 2401, 1),
             (40023, 2, 2402, 1),
             (40024, 2, 2403, 1),
             (40025, 2, 2404, 1),
             -- 时间胶囊（2500 段）
             (40026, 2, 2500, 1),
             (40027, 2, 2501, 1),
             (40028, 2, 2502, 1),
             (40029, 2, 2503, 1),
             (40030, 2, 2504, 1),
             -- 情侣日志（2600 段）
             (40031, 2, 2600, 1),
             (40032, 2, 2601, 1),
             (40033, 2, 2602, 1),
             (40034, 2, 2603, 1),
             (40035, 2, 2604, 1),
             -- 足迹（2700 段）
             (40036, 2, 2700, 1),
             (40037, 2, 2701, 1),
             (40038, 2, 2702, 1),
             (40039, 2, 2703, 1),
             (40040, 2, 2704, 1),
             -- 个人中心（3000 段）
             (40041, 2, 3000, 1)
     ) AS t(id, role_id, menu_id, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_role_menu` WHERE `sys_role_menu`.`id` = t.id
);

-- ----------------------------
-- 数据填充：sys_menu（幂等插入）
-- ----------------------------
INSERT INTO `sys_menu` (
    `id`, `pid`, `title`, `type`, `path`, `component`, `sort`, `icon`, `perms`, `builtin`, `creator`
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
    t.builtin,
    t.creator
FROM (
         VALUES
             (1000, 0, '系统管理', 'D', '/admin/system', NULL, 1, 'ep:setting', NULL, 1, 1),
             (1100, 1000, '系统配置', 'M', '/admin/system/config', 'system/config/index', 1, 'ep:tools', NULL, 1, 1),
             (1101, 1100, '分页查询', 'B', NULL, NULL, 1, NULL, 'system:config:page', 1, 1),
             (1102, 1100, '详情', 'B', NULL, NULL, 2, NULL, 'system:config:detail', 1, 1),
             (1103, 1100, '修改', 'B', NULL, NULL, 3, NULL, 'system:config:update', 1, 1),
             (1104, 1100, '增加', 'B', NULL, NULL, 4, NULL, 'system:config:insert', 1, 1),
             (1105, 1100, '删除', 'B', NULL, NULL, 5, NULL, 'system:config:delete', 1, 1),
             (1200, 1000, '用户管理', 'M', '/admin/system/user', 'system/user/index', 2, 'ep:user', NULL, 1, 1),
             (1201, 1200, '分页查询', 'B', NULL, NULL, 1, NULL, 'system:user:page', 1, 1),
             (1202, 1200, '详情', 'B', NULL, NULL, 2, NULL, 'system:user:detail', 1, 1),
             (1203, 1200, '修改', 'B', NULL, NULL, 3, NULL, 'system:user:update', 1, 1),
             (1204, 1200, '增加', 'B', NULL, NULL, 4, NULL, 'system:user:insert', 1, 1),
             (1205, 1200, '删除', 'B', NULL, NULL, 5, NULL, 'system:user:delete', 1, 1),
             (1300, 1000, '角色管理', 'M', '/admin/system/role', 'system/role/index', 3, 'ep:avatar', NULL, 1, 1),
             (1301, 1300, '分页查询', 'B', NULL, NULL, 1, NULL, 'system:role:page', 1, 1),
             (1302, 1300, '详情', 'B', NULL, NULL, 2, NULL, 'system:role:detail', 1, 1),
             (1303, 1300, '修改', 'B', NULL, NULL, 3, NULL, 'system:role:update', 1, 1),
             (1304, 1300, '增加', 'B', NULL, NULL, 4, NULL, 'system:role:insert', 1, 1),
             (1305, 1300, '删除', 'B', NULL, NULL, 5, NULL, 'system:role:delete', 1, 1),
             (1400, 1000, '菜单管理', 'M', '/admin/system/menu', 'system/menu/index', 4, 'ep:menu', NULL, 1, 1),
             (1401, 1400, '分页查询', 'B', NULL, NULL, 1, NULL, 'system:menu:page', 1, 1),
             (1402, 1400, '详情', 'B', NULL, NULL, 2, NULL, 'system:menu:detail', 1, 1),
             (1403, 1400, '修改', 'B', NULL, NULL, 3, NULL, 'system:menu:update', 1, 1),
             (1404, 1400, '增加', 'B', NULL, NULL, 4, NULL, 'system:menu:insert', 1, 1),
             (1405, 1400, '删除', 'B', NULL, NULL, 5, NULL, 'system:menu:delete', 1, 1),
             (1500, 1000, '字典管理', 'M', '/admin/system/dict', 'system/dict/index', 5, 'ep:collection', NULL, 1, 1),
             (1501, 1500, '分页查询', 'B', NULL, NULL, 1, NULL, 'system:dict:page', 1, 1),
             (1502, 1500, '详情', 'B', NULL, NULL, 2, NULL, 'system:dict:detail', 1, 1),
             (1503, 1500, '修改', 'B', NULL, NULL, 3, NULL, 'system:dict:update', 1, 1),
             (1504, 1500, '增加', 'B', NULL, NULL, 4, NULL, 'system:dict:insert', 1, 1),
             (1505, 1500, '删除', 'B', NULL, NULL, 5, NULL, 'system:dict:delete', 1, 1),
             (1600, 1000, '缓存监控', 'M', '/admin/system/cache', 'system/cache/index', 6, 'ep:monitor', NULL, 1, 1),
             (1601, 1600, '查询', 'B', NULL, NULL, 1, NULL, 'system:cache:list', 1, 1),
             (1700, 1000, '日志管理', 'M', '/admin/system/log', 'system/log/index', 7, 'ep:document', NULL, 1, 1),
             (1701, 1700, '登录日志分页查询', 'B', NULL, NULL, 1, NULL, 'system:log-login:page', 1, 1),
             (1702, 1700, '操作日志分页查询', 'B', NULL, NULL, 2, NULL, 'system:log-operate:page', 1, 1),
             (1800, 1000, '文件管理', 'M', '/admin/system/file', 'system/file/index', 8, 'ep:folder-opened', NULL, 1, 1),
             (1801, 1800, '文件分页查询', 'B', NULL, NULL, 1, NULL, 'system:file:page', 1, 1),
             (1802, 1800, '删除文件', 'B', NULL, NULL, 2, NULL, 'system:file:delete', 1, 1),
             (2000, 0, '点点滴滴', 'M', '/admin/moments', 'admin/moments/index', 2, 'ep:sunny', NULL, 1, 1),
             (2001, 2000, '分页查询', 'B', NULL, NULL, 1, NULL, 'admin:moments:page', 1, 1),
             (2002, 2000, '增加', 'B', NULL, NULL, 2, NULL, 'admin:moments:insert', 1, 1),
             (2003, 2000, '修改', 'B', NULL, NULL, 3, NULL, 'admin:moments:update', 1, 1),
             (2004, 2000, '删除', 'B', NULL, NULL, 4, NULL, 'admin:moments:delete', 1, 1),
             (2100, 0, '恋爱画册', 'M', '/admin/love-photo', 'admin/love-photo/index', 3, 'ep:camera', NULL, 1, 1),
             (2101, 2100, '分页查询', 'B', NULL, NULL, 1, NULL, 'admin:love-photo:page', 1, 1),
             (2102, 2100, '增加', 'B', NULL, NULL, 2, NULL, 'admin:love-photo:insert', 1, 1),
             (2103, 2100, '修改', 'B', NULL, NULL, 3, NULL, 'admin:love-photo:update', 1, 1),
             (2104, 2100, '删除', 'B', NULL, NULL, 4, NULL, 'admin:love-photo:delete', 1, 1),
             (2200, 0, '恋爱清单', 'M', '/admin/love-list', 'admin/love-list/index', 4, 'ep:list', NULL, 1, 1),
             (2201, 2200, '分页查询', 'B', NULL, NULL, 1, NULL, 'admin:love-list:page', 1, 1),
             (2202, 2200, '增加', 'B', NULL, NULL, 2, NULL, 'admin:love-list:insert', 1, 1),
             (2203, 2200, '修改', 'B', NULL, NULL, 3, NULL, 'admin:love-list:update', 1, 1),
             (2204, 2200, '删除', 'B', NULL, NULL, 4, NULL, 'admin:love-list:delete', 1, 1),
             (2300, 0, '留言簿', 'M', '/admin/message', 'admin/message/index', 5, 'ep:chat-dot-round', NULL, 1, 1),
             (2301, 2300, '分页查询', 'B', NULL, NULL, 1, NULL, 'admin:message:page', 1, 1),
             (2302, 2300, '增加', 'B', NULL, NULL, 2, NULL, 'admin:message:insert', 1, 1),
             (2303, 2300, '修改', 'B', NULL, NULL, 3, NULL, 'admin:message:update', 1, 1),
             (2304, 2300, '删除', 'B', NULL, NULL, 4, NULL, 'admin:message:delete', 1, 1),
             (2400, 0, '纪念日', 'M', '/admin/anniversary', 'admin/anniversary/index', 6, 'ep:calendar', NULL, 1, 1),
             (2401, 2400, '分页查询', 'B', NULL, NULL, 1, NULL, 'admin:anniversary:page', 1, 1),
             (2402, 2400, '增加', 'B', NULL, NULL, 2, NULL, 'admin:anniversary:insert', 1, 1),
             (2403, 2400, '修改', 'B', NULL, NULL, 3, NULL, 'admin:anniversary:update', 1, 1),
             (2404, 2400, '删除', 'B', NULL, NULL, 4, NULL, 'admin:anniversary:delete', 1, 1),
             (2500, 0, '时间胶囊', 'M', '/admin/time-capsule', 'admin/time-capsule/index', 7, 'ep:box', NULL, 1, 1),
             (2501, 2500, '分页查询', 'B', NULL, NULL, 1, NULL, 'admin:time-capsule:page', 1, 1),
             (2502, 2500, '增加', 'B', NULL, NULL, 2, NULL, 'admin:time-capsule:insert', 1, 1),
             (2503, 2500, '修改', 'B', NULL, NULL, 3, NULL, 'admin:time-capsule:update', 1, 1),
             (2504, 2500, '删除', 'B', NULL, NULL, 4, NULL, 'admin:time-capsule:delete', 1, 1),
             (2600, 0, '情侣日志', 'M', '/admin/diary', 'admin/diary/index', 8, 'ep:notebook', NULL, 1, 1),
             (2601, 2600, '分页查询', 'B', NULL, NULL, 1, NULL, 'admin:diary:page', 1, 1),
             (2602, 2600, '增加', 'B', NULL, NULL, 2, NULL, 'admin:diary:insert', 1, 1),
             (2603, 2600, '修改', 'B', NULL, NULL, 3, NULL, 'admin:diary:update', 1, 1),
             (2604, 2600, '删除', 'B', NULL, NULL, 4, NULL, 'admin:diary:delete', 1, 1),
             (2700, 0, '足迹', 'M', '/admin/footprint', 'admin/footprint/index', 9, 'ep:location', NULL, 1, 1),
             (2701, 2700, '分页查询', 'B', NULL, NULL, 1, NULL, 'admin:footprint:page', 1, 1),
             (2702, 2700, '增加', 'B', NULL, NULL, 2, NULL, 'admin:footprint:insert', 1, 1),
             (2703, 2700, '修改', 'B', NULL, NULL, 3, NULL, 'admin:footprint:update', 1, 1),
             (2704, 2700, '删除', 'B', NULL, NULL, 4, NULL, 'admin:footprint:delete', 1, 1),
             (3000, 0, '个人中心', 'M', '/admin/profile', 'profile/index', 10, 'ep:avatar', NULL, 1, 1)
     ) AS t(id, pid, title, type, path, component, sort, icon, perms, builtin, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_menu` WHERE `sys_menu`.`id` = t.id
);

-- ----------------------------
-- 数据填充：sys_dict_type（幂等插入）
-- 内置类型（builtin=1）编码与 common/enums 下现有枚举类对齐（取枚举类名去 Enum 后缀的小写
-- kebab 形式，如 GenderEnum → gender、EnableEnum → enable、MenuType → menu-type）；
-- id 使用独立的 99000 段顺序预留（与条目表 10000 段互相独立）
-- ----------------------------
INSERT INTO `sys_dict_type` (
    `id`, `dict_code`, `dict_name`, `builtin`, `remark`, `creator`
)
SELECT
    t.id,
    t.dict_code,
    t.dict_name,
    t.builtin,
    t.remark,
    t.creator
FROM (
         VALUES
             (99001, 'gender', '性别', 1, '与 GenderEnum(U/M/F) 对齐', 1),
             (99002, 'enable', '启用状态', 1, '与 EnableEnum(0/1) 对齐', 1),
             (99003, 'yes', '是否', 1, '与 YesEnum(1/0) 对齐', 1),
             (99004, 'del-flag', '删除标识', 1, '与 DelFlagEnum(1/0) 对齐', 1),
             (99005, 'menu-type', '菜单类型', 1, '与 MenuType(D/M/B) 对齐', 1),
             (99006, 'image-type', '图形验证码类型', 1, '与 ImageType(spec/gif/chinese/chinese-gif/arithmetic) 对齐', 1),
             (99007, 'success', '成功状态', 1, '与 SuccessEnum(1/0) 对齐', 1),
             (99008, 'config-group', '配置分组', 1, '与 sys_config.config_group(base/site/captcha/file/rate-limit/security) 对齐', 1),
             (99009, 'config-value-type', '配置值类型', 1, '与 sys_config.value_type(STRING/INTEGER/LONG/BOOLEAN/DATETIME) 对齐', 1),
             (99010, 'file-storage-type', '存储类型', 1, '与 StorageTypeEnum(local/oss) 对齐', 1),
             (99011, 'biz-type', '文件业务类型', 1, '与 FileBizTypeEnum(infra/avatar/photo/markdown) 对齐', 1),
             (99012, 'log-type', '日志类型', 1, '与 LogTypeEnum 对齐', 1),
             (99013, 'hidden', '显隐状态', 1, '与 HiddenEnum(0/1) 对齐', 1),
             (99014, 'anniversary-type', '纪念日类型', 1, '与 AnniversaryTypeEnum(birthday/anniversary/festival) 对齐', 1),
             (99015, 'message-audit-status', '留言审核状态', 1, '与 MessageAuditStatusEnum(pending/approved/rejected) 对齐', 1),
             (99016, 'done', '完成状态', 1, '与 DoneEnum(0/1) 对齐', 1),
             (99017, 'diary-mood', '日记心情', 1, '与 DiaryMoodEnum(sunny/cloudy/rainy/windy/snowy/starry/bloom/moon) 对齐', 1)
     ) AS t(id, dict_code, dict_name, builtin, remark, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_dict_type` WHERE `sys_dict_type`.`id` = t.id
);

-- ----------------------------
-- 数据填充：sys_dict_data（幂等插入）
-- dict_code 与 sys_dict_type 内置类型对齐，内置条目（builtin=1）仅承载展示层
-- （label/排序/启停），业务校验仍由对应枚举类负责；
-- 组内条目顺序与枚举声明顺序一致，id 按编码分段预留（10000 段起，每编码预留 100）
-- ----------------------------
INSERT INTO `sys_dict_data` (
    `id`, `dict_code`, `dict_value`, `dict_label`, `sort`, `status`, `builtin`, `remark`, `creator`
)
SELECT
    t.id,
    t.dict_code,
    t.dict_value,
    t.dict_label,
    t.sort,
    t.status,
    t.builtin,
    t.remark,
    t.creator
FROM (
         VALUES
             -- gender（性别，对应 GenderEnum：U-未知,M-男,F-女；10000 段）
             (10001, 'gender', 'U', '未知', 1, 0, 1, '与 GenderEnum(U/M/F) 对齐', 1),
             (10002, 'gender', 'M', '男', 2, 0, 1, '与 GenderEnum(U/M/F) 对齐', 1),
             (10003, 'gender', 'F', '女', 3, 0, 1, '与 GenderEnum(U/M/F) 对齐', 1),
             -- enable（通用启停状态，对应 EnableEnum：0-启用,1-禁用；10100 段）
             (10101, 'enable', '0', '启用', 1, 0, 1, '与 EnableEnum(0/1) 对齐', 1),
             (10102, 'enable', '1', '禁用', 2, 0, 1, '与 EnableEnum(0/1) 对齐', 1),
             -- yes（是否，对应 YesEnum：1-是,0-否；10200 段）
             (10201, 'yes', '1', '是', 1, 0, 1, '与 YesEnum(1/0) 对齐', 1),
             (10202, 'yes', '0', '否', 2, 0, 1, '与 YesEnum(1/0) 对齐', 1),
             -- del-flag（删除标识，对应 DelFlagEnum：1-已删除,0-未删除；10300 段）
             (10301, 'del-flag', '0', '未删除', 1, 0, 1, '与 DelFlagEnum(1/0) 对齐', 1),
             (10302, 'del-flag', '1', '已删除', 2, 0, 1, '与 DelFlagEnum(1/0) 对齐', 1),
             -- menu-type（菜单类型，对应 MenuType：D-目录,M-菜单,B-按钮；10400 段）
             (10401, 'menu-type', 'D', '目录', 1, 0, 1, '与 MenuType(D/M/B) 对齐', 1),
             (10402, 'menu-type', 'M', '菜单', 2, 0, 1, '与 MenuType(D/M/B) 对齐', 1),
             (10403, 'menu-type', 'B', '按钮', 3, 0, 1, '与 MenuType(D/M/B) 对齐', 1),
             -- image-type（图形验证码类型，对应 ImageType；10500 段）
             (10501, 'image-type', 'spec', 'PNG字符验证码', 1, 0, 1, '与 ImageType(spec/gif/chinese/chinese-gif/arithmetic) 对齐', 1),
             (10502, 'image-type', 'gif', 'GIF字符验证码', 2, 0, 1, '与 ImageType(spec/gif/chinese/chinese-gif/arithmetic) 对齐', 1),
             (10503, 'image-type', 'chinese', '中文字符验证码', 3, 0, 1, '与 ImageType(spec/gif/chinese/chinese-gif/arithmetic) 对齐', 1),
             (10504, 'image-type', 'chinese-gif', '中文GIF字符验证码', 4, 0, 1, '与 ImageType(spec/gif/chinese/chinese-gif/arithmetic) 对齐', 1),
             (10505, 'image-type', 'arithmetic', '算术验证码', 5, 0, 1, '与 ImageType(spec/gif/chinese/chinese-gif/arithmetic) 对齐', 1),
             -- success（成功状态，对应 SuccessEnum：1-成功,0-失败；10600 段）
             (10601, 'success', '1', '成功', 1, 0, 1, '与 SuccessEnum(1/0) 对齐', 1),
             (10602, 'success', '0', '失败', 2, 0, 1, '与 SuccessEnum(1/0) 对齐', 1),
             -- config-group（配置分组，与 sys_config.config_group 对齐：base-基础,site-门户,captcha-验证码,file-文件,rate-limit-接口限流；10700 段）
             (10701, 'config-group', 'base', '基础配置', 1, 0, 1, '与 sys_config.config_group(base/site/captcha) 对齐', 1),
             (10702, 'config-group', 'site', '门户配置', 2, 0, 1, '与 sys_config.config_group(base/site/captcha) 对齐', 1),
             (10703, 'config-group', 'captcha', '验证码配置', 3, 0, 1, '与 sys_config.config_group(base/site/captcha) 对齐', 1),
             (10704, 'config-group', 'file', '文件配置', 4, 0, 1, '与 sys_config.config_group(base/site/captcha/file) 对齐', 1),
             (10705, 'config-group', 'rate-limit', '接口限流', 5, 0, 1, '与 sys_config.config_group(base/site/captcha/file/rate-limit) 对齐', 1),
             (10706, 'config-group', 'security', '安全配置', 6, 0, 1, '与 sys_config.config_group 对齐(密钥类配置:security.uapi-key/security.amap-key/security.amap-code)', 1),
             -- config-value-type（配置值类型，与 sys_config.value_type 对齐：STRING/INTEGER/LONG/BOOLEAN/DATETIME；10800 段）
             (10801, 'config-value-type', 'STRING', '字符串', 1, 0, 1, '与 sys_config.value_type(STRING/INTEGER/LONG/BOOLEAN) 对齐', 1),
             (10802, 'config-value-type', 'INTEGER', '整数', 2, 0, 1, '与 sys_config.value_type(STRING/INTEGER/LONG/BOOLEAN) 对齐', 1),
             (10803, 'config-value-type', 'LONG', '长整数', 3, 0, 1, '与 sys_config.value_type(STRING/INTEGER/LONG/BOOLEAN) 对齐', 1),
             (10804, 'config-value-type', 'BOOLEAN', '布尔', 4, 0, 1, '与 sys_config.value_type(STRING/INTEGER/LONG/BOOLEAN) 对齐', 1),
             (10805, 'config-value-type', 'DATETIME', '日期时间', 5, 0, 1, '与 sys_config.value_type(STRING/INTEGER/LONG/BOOLEAN/DATETIME) 对齐', 1),
             -- file-storage-type（存储类型，对应 StorageTypeEnum：local-本地,oss-对象存储；10900 段）
             (10901, 'file-storage-type', 'local', '本地存储', 1, 0, 1, '与 StorageTypeEnum(local/oss) 对齐', 1),
             (10902, 'file-storage-type', 'oss', '对象存储', 2, 1, 1, '与 StorageTypeEnum(local/oss) 对齐', 1),
             -- biz-type（文件业务类型，对应 FileBizTypeEnum：infra-基础设施,avatar-用户头像,photo-相册照片,markdown-点滴配图；11000 段）
             (11001, 'biz-type', 'infra', '基础设施', 1, 0, 1, '与 FileBizTypeEnum(infra/avatar/photo/markdown) 对齐', 1),
             (11002, 'biz-type', 'avatar', '用户头像', 2, 0, 1, '与 FileBizTypeEnum(infra/avatar/photo/markdown) 对齐', 1),
             (11003, 'biz-type', 'photo', '相册照片', 3, 0, 1, '与 FileBizTypeEnum(infra/avatar/photo/markdown) 对齐', 1),
             (11004, 'biz-type', 'markdown', '点滴配图', 4, 0, 1, '与 FileBizTypeEnum(infra/avatar/photo/markdown) 对齐', 1),
             -- log-type（日志类型，对应 LogTypeEnum：0-未知,1-登录,2-登出,3-查询,4-新增,5-修改,6-删除,7-授权,8-上传,9-下载；11100 段）
             (11101, 'log-type', '0', '未知', 1, 0, 1, '与 LogTypeEnum 对齐', 1),
             (11102, 'log-type', '1', '登录', 2, 0, 1, '与 LogTypeEnum 对齐', 1),
             (11103, 'log-type', '2', '登出', 3, 0, 1, '与 LogTypeEnum 对齐', 1),
             (11104, 'log-type', '3', '查询', 4, 0, 1, '与 LogTypeEnum 对齐', 1),
             (11105, 'log-type', '4', '新增', 5, 0, 1, '与 LogTypeEnum 对齐', 1),
             (11106, 'log-type', '5', '修改', 6, 0, 1, '与 LogTypeEnum 对齐', 1),
             (11107, 'log-type', '6', '删除', 7, 0, 1, '与 LogTypeEnum 对齐', 1),
             (11108, 'log-type', '7', '授权', 8, 0, 1, '与 LogTypeEnum 对齐', 1),
             (11109, 'log-type', '8', '上传', 9, 0, 1, '与 LogTypeEnum 对齐', 1),
             (11110, 'log-type', '9', '下载', 10, 0, 1, '与 LogTypeEnum 对齐', 1),
             -- hidden（照片显隐，对应 HiddenEnum：0-显示,1-隐藏；11200 段）
             (11201, 'hidden', '0', '显示', 1, 0, 1, '与 HiddenEnum(0/1) 对齐', 1),
             (11202, 'hidden', '1', '隐藏', 2, 0, 1, '与 HiddenEnum(0/1) 对齐', 1),
             -- anniversary-type（纪念日类型，对应 AnniversaryTypeEnum：birthday-生日,anniversary-纪念日,festival-节日；11300 段）
             (11301, 'anniversary-type', 'birthday', '生日', 1, 0, 1, '与 AnniversaryTypeEnum 对齐', 1),
             (11302, 'anniversary-type', 'anniversary', '纪念日', 2, 0, 1, '与 AnniversaryTypeEnum 对齐', 1),
             (11303, 'anniversary-type', 'festival', '节日', 3, 0, 1, '与 AnniversaryTypeEnum 对齐', 1),
             -- message-audit-status（留言审核状态，对应 MessageAuditStatusEnum：pending-待审核,approved-通过,rejected-驳回；11400 段）
             (11401, 'message-audit-status', 'pending', '待审核', 1, 0, 1, '与 MessageAuditStatusEnum 对齐', 1),
             (11402, 'message-audit-status', 'approved', '通过', 2, 0, 1, '与 MessageAuditStatusEnum 对齐', 1),
             (11403, 'message-audit-status', 'rejected', '驳回', 3, 0, 1, '与 MessageAuditStatusEnum 对齐', 1),
             -- done（通用完成状态，对应 DoneEnum：0-未完成,1-已完成；11500 段）
             (11501, 'done', '0', '未完成', 1, 0, 1, '与 DoneEnum(0/1) 对齐', 1),
             (11502, 'done', '1', '已完成', 2, 0, 1, '与 DoneEnum(0/1) 对齐', 1),
             -- diary-mood（日记心情，对应 DiaryMoodEnum：unknown-不标记,sunny-晴天,cloudy-多云,overcast-阴天,rainy-雨天,drizzle-细雨,thunderstorm-雷阵雨,windy-起风,snowy-落雪,sleet-雨夹雪,hail-冰雹,starry-星夜,bloom-花开,moon-月色,rainbow-彩虹,fog-薄雾,leaf-落叶,sunset-日落,meteor-流星,aurora-极光；11600 段）
             (11601, 'diary-mood', 'unknown', '不标记', 1, 0, 1, '与 DiaryMoodEnum 对齐(默认值)', 1),
             (11602, 'diary-mood', 'sunny', '晴天', 2, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11603, 'diary-mood', 'cloudy', '多云', 3, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11604, 'diary-mood', 'overcast', '阴天', 4, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11605, 'diary-mood', 'rainy', '雨天', 5, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11606, 'diary-mood', 'drizzle', '细雨', 6, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11607, 'diary-mood', 'thunderstorm', '雷阵雨', 7, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11608, 'diary-mood', 'windy', '起风', 8, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11609, 'diary-mood', 'snowy', '落雪', 9, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11610, 'diary-mood', 'sleet', '雨夹雪', 10, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11611, 'diary-mood', 'hail', '冰雹', 11, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11612, 'diary-mood', 'starry', '星夜', 12, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11613, 'diary-mood', 'bloom', '花开', 13, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11614, 'diary-mood', 'moon', '月色', 14, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11615, 'diary-mood', 'rainbow', '彩虹', 15, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11616, 'diary-mood', 'fog', '薄雾', 16, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11617, 'diary-mood', 'leaf', '落叶', 17, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11618, 'diary-mood', 'sunset', '日落', 18, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11619, 'diary-mood', 'meteor', '流星', 19, 0, 1, '与 DiaryMoodEnum 对齐', 1),
             (11620, 'diary-mood', 'aurora', '极光', 20, 0, 1, '与 DiaryMoodEnum 对齐', 1)
     ) AS t(id, dict_code, dict_value, dict_label, sort, status, builtin, remark, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_dict_data` WHERE `sys_dict_data`.`id` = t.id
);

-- ----------------------------
-- 数据填充：portal_love_photo（幂等插入）
-- 恋爱画册门户展示的示例照片（hidden=0 门户可见）；id 使用独立的 30000 段顺序预留
-- ----------------------------
INSERT INTO `portal_love_photo` (
    `id`, `url`, `caption`, `date_text`, `tags`, `sort`, `creator`
)
SELECT
    t.id,
    t.url,
    t.caption,
    t.date_text,
    t.tags,
    t.sort,
    t.creator
FROM (
         VALUES
             (30001, 'https://t.alcy.cc/pic/fj/130.webp', '我们的第一张合照', '2018-07-15', '日常', 1, 1)
     ) AS t(id, url, caption, date_text, tags, sort, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `portal_love_photo` WHERE `portal_love_photo`.`id` = t.id
);

-- ----------------------------
-- 数据填充：portal_footprint（幂等插入）
-- 足迹地图门户展示的示例足迹（photo_url 可空,为空时门户按无照片渲染）；id 使用独立的 31000 段顺序预留
-- ----------------------------
INSERT INTO `portal_footprint` (
    `id`, `city`, `place_name`, `longitude`, `latitude`, `arrival_date`, `photo_url`, `remark`, `creator`
)
SELECT
    t.id,
    t.city,
    t.place_name,
    t.longitude,
    t.latitude,
    t.arrival_date,
    t.photo_url,
    t.remark,
    t.creator
FROM (
         VALUES
             (31001, '成都', '宽窄巷子', 104.053307, 30.663869, '2024-10-02', NULL, '第一次一起逛巷子,看了变脸吃了三大炮', 1),
             (31002, '北京', '故宫博物院', 116.397228, 39.916534, '2023-05-20', NULL, '红墙黄瓦里逛了一天紫禁城,砖缝都是故事', 1),
             (31003, '杭州', '西湖', 120.144386, 30.242874, '2024-04-06', NULL, '断桥遇上苏堤春晓,一湖烟雨看了一天', 1)
     ) AS t(id, city, place_name, longitude, latitude, arrival_date, photo_url, remark, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `portal_footprint` WHERE `portal_footprint`.`id` = t.id
);

-- ----------------------------
-- 数据填充：portal_anniversary（幂等插入）
-- 门户纪念日展示的示例数据（type 对应字典 anniversary-type）；id 使用独立的 32000 段顺序预留
-- ----------------------------
INSERT INTO `portal_anniversary` (
    `id`, `name`, `type`, `anniversary_date`, `repeat_yearly`, `creator`
)
SELECT
    t.id,
    t.name,
    t.type,
    t.anniversary_date,
    t.repeat_yearly,
    t.creator
FROM (
         VALUES
             (32001, '在一起纪念日', 'anniversary', '2022-05-21', 1, 1)
     ) AS t(id, name, type, anniversary_date, repeat_yearly, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `portal_anniversary` WHERE `portal_anniversary`.`id` = t.id
);

-- ----------------------------
-- 数据填充：portal_message（幂等插入）
-- 留言簿门户展示的示例留言（audit_status='approved' 审核通过后门户可见）；id 使用独立的 33000 段顺序预留
-- ----------------------------
INSERT INTO `portal_message` (
    `id`, `nickname`, `avatar`, `content`, `ip`, `region`, `audit_status`
)
SELECT
    t.id,
    t.nickname,
    t.avatar,
    t.content,
    t.ip,
    t.region,
    t.audit_status
FROM (
         VALUES
             (33001, '汏臉貓', 'https://q1.qlogo.cn/g?b=qq&nk=1324497787&s=100', '祝你们永远像热恋期一样甜！', '113.64.12.7', '广东', 'approved')
     ) AS t(id, nickname, avatar, content, ip, region, audit_status)
WHERE NOT EXISTS (
    SELECT 1 FROM `portal_message` WHERE `portal_message`.`id` = t.id
);

-- ----------------------------
-- 数据填充：portal_love_list（幂等插入）
-- 恋爱清单门户展示的示例愿望（覆盖旅行/日常/仪式/美食/成长/家庭等场景,已完成项带纪念照;hidden=0 门户可见）；
-- id 使用独立的 34000 段顺序预留
-- ----------------------------
INSERT INTO `portal_love_list` (
    `id`, `content`, `done`, `photo`, `sort`, `creator`
)
SELECT
    t.id,
    t.content,
    t.done,
    t.photo,
    t.sort,
    t.creator
FROM (
         VALUES
             (34001, '一起期待未来甜蜜小生活💑', 0, NULL, 1, 1),
             (34002, '一起为我们的小家添置东西🏠', 0, NULL, 2, 1),
             (34003, '一起挑选婚纱👗', 0, NULL, 3, 1),
             (34004, '一起去见双方父母🏡', 0, NULL, 4, 1),
             (34005, '一起听一次演唱会🎤', 1, 'https://t.alcy.cc/pic/fj/135.webp', 5, 1),
             (34006, '一起去看樱花🌸', 0, NULL, 6, 1),
             (34007, '一起存钱💰', 0, NULL, 7, 1),
             (34008, '一起去看一次日出🌅', 1, 'https://t.alcy.cc/pic/fj/132.webp', 8, 1),
             (34009, '一起去看一次大海🌊', 1, 'https://t.alcy.cc/pic/fj/133.webp', 9, 1),
             (34010, '一起做一顿烛光晚餐🕯️', 0, NULL, 10, 1),
             (34011, '一起养一只小猫咪🐱', 0, NULL, 11, 1),
             (34012, '一起去看一次极光🌌', 0, NULL, 12, 1),
             (34013, '一起露营看星星✨', 0, NULL, 13, 1),
             (34014, '一起坐一次热气球🎈', 0, NULL, 14, 1),
             (34015, '一起装饰一棵圣诞树🎄', 0, NULL, 15, 1),
             (34016, '一起跨年倒数🕛', 1, 'https://t.alcy.cc/pic/fj/134.webp', 16, 1),
             (34017, '一起逛遍这座城市的夜市🍢', 0, NULL, 17, 1),
             (34018, '一起学一门乐器🎹', 0, NULL, 18, 1),
             (34019, '一起拍一套情侣写真📷', 0, NULL, 19, 1),
             (34020, '一起攒够小家的第一桶金🏆', 0, NULL, 20, 1)
     ) AS t(id, content, done, photo, sort, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `portal_love_list` WHERE `portal_love_list`.`id` = t.id
);

-- ----------------------------
-- 数据填充：portal_time_capsule（幂等插入）
-- 时间胶囊门户展示的示例信件（open_time 到点后门户可见全文,未到点仅展示封存卡）；id 使用独立的 35000 段顺序预留
-- ----------------------------
INSERT INTO `portal_time_capsule` (
    `id`, `title`, `content`, `open_time`, `hidden`, `creator`
)
SELECT
    t.id,
    t.title,
    t.content,
    t.open_time,
    t.hidden,
    t.creator
FROM (
         VALUES
             (35001, '写给恋爱三周年的信💌', '三年前的今天你答应了和我在一起，这三年的每一天都比昨天更幸福。希望未来的每一年，我们都像今天一样热爱生活、热爱彼此。', '2025-05-21 00:00:00', 0, 1),
             (35002, '写给下一个五年的我们💌', '见字如面。五年后的我们，应该在为小家忙碌着吧？希望那时的我们依然会为一顿烛光晚餐而开心，依然愿意为对方学一道新菜。', '2026-12-31 00:00:00', 0, 1),
             (35003, '三十年后打开🔮', '当你打开这封信的时候，我们已经一起走过了大半生。谢谢你没有放开我的手。余生很长，我们慢慢走。', '2048-05-21 00:00:00', 0, 1)
     ) AS t(id, title, content, open_time, hidden, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `portal_time_capsule` WHERE `portal_time_capsule`.`id` = t.id
);

-- ----------------------------
-- 数据填充：portal_diary（幂等插入）
-- 情侣日记门户展示的示例日记（两人交替记录,门户按记录人分栏展示）;id 使用独立的 36000 段顺序预留
-- ----------------------------
INSERT INTO `portal_diary` (
    `id`, `user_id`, `diary_date`, `mood`, `content`, `creator`
)
SELECT
    t.id,
    t.user_id,
    t.diary_date,
    t.mood,
    t.content,
    t.creator
FROM (
         VALUES
             (36001, 2, '2025-01-12', 'sunny', '今天一起去了菜市场,她挑西红柿的样子像在选宝石。晚饭做了番茄牛腩,汤底很浓,是家的味道。', 2),
             (36002, 3, '2025-01-12', 'unknown', '加班到很晚,回家路上看见他在楼下等我,手里的奶茶还是热的。原来被人惦记是这种感觉。', 2),
             (36003, 2, '2025-02-09', 'sunny', '周末把阳台收拾了出来,绿萝又发了新芽。我们约定每周日一起给它浇水。', 2),
             (36004, 3, '2025-03-14', 'drizzle', '下雨了,我们窝在沙发里重看了一遍老电影。他说台词比记忆里的更甜。', 2),
             (36005, 2, '2025-04-02', 'sunset', '一起规划了明年的旅行,第一站是海边。期待是一件让日子发光的事。', 2),
             (36006, 3, '2025-04-02', 'overcast', '今天吵了一点点架,又和好了。原来磨合不是妥协,是学会把「我」变成「我们」。', 2),
             (36007, 2, '2025-05-20', 'bloom', '他学会了做舒芙蕾,虽然塌了,但是很好吃。认真生活的样子真好看。', 2),
             (36008, 3, '2025-06-01', 'starry', '晚饭后沿着河边走了很久,路灯把两个人的影子拉得很长。所谓幸福,大概就是这样不说话也不尴尬的散步。', 2),
             (36009, 2, '2025-07-19', 'windy', '一起给绿萝换了盆,泥土的味道让人踏实。她说秋天要买个书架,把我们的书都摆在一起。', 2),
             (36010, 3, '2025-08-23', 'thunderstorm', '台风天宅在家,煮了一锅姜茶,把毯子铺在地板上打牌。输了的人负责下一顿晚饭。', 2),
             (36011, 2, '2025-09-30', 'moon', '中秋节回她家,阿姨做的菜好吃到犯规。临走时阿姨塞给我们一整盒月饼,说路上慢慢吃。', 2),
             (36012, 3, '2025-10-25', 'starry', '今晚的月亮很圆。我们坐在阳台的懒人沙发上,一人一副耳机,把喜欢的歌单交换着听完。', 2),
             (36013, 2, '2025-11-11', 'fog', '双十一没买东西,一起去了趟宜家。在样板间里畅想未来的客厅,越聊越起劲。', 2),
             (36014, 3, '2025-12-14', 'snowy', '初雪比预报来得早。他站在楼下,围巾裹到鼻子,手里还是一杯热奶茶。', 2),
             (36015, 2, '2025-12-31', 'starry', '跨年夜,我们对着烟花许了同一个愿望。新的一年,也请多多指教。', 2),
             (36016, 3, '2026-01-03', 'sunny', '元旦一起大扫除,翻出去年一起买的第一件家具。原来我们已经攒了这么多共同记忆。', 2),
             (36017, 2, '2026-01-05', 'sleet', '她开始学吉他了,每天晚上练二十分钟。跑调也没关系,反正好听的都是心意。', 2),
             (36018, 3, '2026-01-07', 'unknown', '把这一年的照片洗了出来,做成本子。原来我们已经一起走过了这么多地方。', 2)
     ) AS t(id, user_id, diary_date, mood, content, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `portal_diary` WHERE `portal_diary`.`id` = t.id
);

-- ----------------------------
-- 数据填充：portal_moments(幂等;仅保留一条示范长文,充分展示详情页排版)
-- ----------------------------
DELETE FROM `portal_moments` WHERE `id` IN (37001, 37002, 37003);

INSERT INTO `portal_moments` (
    `id`, `title`, `user_id`, `content`, `record_date`, `sort`, `category`, `tags`, `status`, `creator`
)
SELECT
    t.id,
    t.title,
    t.user_id,
    t.content,
    t.record_date,
    t.sort,
    t.category,
    t.tags,
    t.status,
    t.creator
FROM (
         VALUES
             (37001, '把 1000 天装进行李箱:一场说走就走的纪念旅行', 2,
              '<p>凌晨四点半,闹钟还没响我就醒了。窗外的天是深蓝色的,像把整个夜空泡进了墨水瓶里。我蹑手蹑脚地爬起来收拾行李,回头看见你蜷在被子里,只露出一个毛茸茸的头顶。那一刻我忽然想,所谓<strong>纪念日</strong>,其实只是给平凡日子找的一个借口——真正想做的,不过是借一个由头,和你再多看一眼世界。</p><p>一千天,说长不长,说短不短。翻了一遍相册,我们一共一起吃过两百多顿认真的晚饭,攒下三千多张照片,走过了十二座城市。如果把每一天都写成一页信纸,大概能摞到天花板那么高吧。</p><p>所以这次,我们把行李箱装成了时间胶囊,出发去看海。</p><h2>出发前夜:把日子折进行李箱</h2><p>收拾行李是一门玄学。你的原则是"万一用得上呢",我的原则是"到时候再说"。于是我们的行李箱里出现了三瓶防晒霜、两副墨镜、一本谁都不会真的打开的旅行攻略,以及一个始终想不起来用途的插线板。</p><p>出发前一晚,我们对着清单做了最后一次核对,像两个不称职的特工在核对任务清单:</p><ul><li>证件、充电器、常用药品,一样不能少;</li><li>零食塞满随行袋的每一个角落,包括那包你最爱吃的海苔;</li><li>给楼下的猫留好三天的猫粮,拜托邻居帮忙添水;</li><li>把去年在夜市买的情侣手环带上,和<strong>1000 天</strong>这个数字配一套。</li></ul><p>行李箱合上的瞬间,你郑重其事地宣布:"本次旅行的一切分歧,以<em>肚子最饿的人</em>的意见为准。"后来事实证明,这条规则的危险程度,远超我们两个人当时的想象。</p><h2>在路上:意料之外的美好</h2><p>旅行的意义大概就是:计划永远赶不上变化,而变化往往比计划可爱得多。</p><h3>误车的三十分钟</h3><p>高铁开车前三十分钟,我们还在站台的另一头研究哪家便利店的饭团好吃。狂奔、过闸、赶在车门关闭前一秒冲进车厢——这是我们俩第一次在旅途中上演生死时速。坐定之后你瘫在座位上喘气,冒出一句:<code>1000-DAYS-MISSION</code>完成度百分之五十。</p><blockquote>所谓旅行,就是花一半的时间迷路,再用另一半的时间爱上迷路后的意外收获。</blockquote><p>误车的三十分钟让我们错过了座位旁的风景,却收获了整段旅程里笑得最大声的回忆。你把这段经历记进了手机备忘录,标题就叫<code>误车大事记</code>——你说以后要写进我们的家庭年鉴里。</p><h3>一碗面的救赎</h3><p>晚上九点,我们又累又饿,随便钻进一家亮着暖黄灯的小面馆。老板娘端上来两碗热气腾腾的葱油拌面,面上卧着两颗溏心蛋。你尝了一口,眼睛"唰"地亮了。</p><p>那碗面到底有多好吃?这么说吧,第二天中午,我们放弃了人均两百的餐厅,又绕回了那家面馆。<em>有些幸福就是这么不讲道理</em>,一碗面就足够了。</p><hr><h2>第 1000 天:把感谢说出口</h2><p>海边的清晨比想象中冷。我们把外套披在同一件大衣里,看太阳一点点从海平线下面爬上来。你忽然说:"其实纪念日不重要,重要的是我们还记得为什么在一起。"</p><p>我想了想,认真列了一份清单,作为第 1000 天的总结:</p><ol><li>一起吃过两百多顿晚饭,其中一半是你做的,另一半是我负责点外卖;</li><li>一起走过十二座城市,吵过七次架,和好的方式全是吃饭;</li><li>拍下三千多张照片,你在其中的九成里都在摆同一个手势;</li><li>存钱罐换过三个,每一枚硬币都在为下一次出发攒路费;</li><li>说过无数次"下次再来",而这一次,我们真的来了。</li></ol><figure><img src="https://t.alcy.cc/pic/fj/135.webp" alt="旅途纪念插画"><figcaption>Fig. 01 — 把我们的第 1000 天,画成一张去往海边的车票。</figcaption></figure><p>日落的时候,我们坐在防波堤上分一副耳机。你说,以后的每一千天都要这样庆祝。我说是,顺便把<em>两百顿晚饭</em>的目标往上调一调。</p><p>回程的路上你靠在我肩上睡着了。车厢摇晃,海风从窗缝里挤进来,带着一点咸味。我掏出手机,在备忘录里敲下这句话,作为这次旅行的结尾:</p><blockquote>数字会一直涨下去,而记录,是我们送给彼此最温柔的时间胶囊。</blockquote><p>下一个一千天,我们已经约好了一个更远的地方。<s>本来还计划顺路去敦煌</s>,最后决定把这份惊喜留到下一站——毕竟<u>期待本身</u>,就是纪念的一部分。<strong>慢慢来,反正余生很长,我们慢慢走。</strong></p>',
              '2025-03-15', 2, '纪念', '纪念日,旅行,手账', 0, 2)
     ) AS t(id, title, user_id, content, record_date, sort, category, tags, status, creator)
WHERE NOT EXISTS (
    SELECT 1 FROM `portal_moments` WHERE `portal_moments`.`id` = t.id
);

SET REFERENTIAL_INTEGRITY TRUE;
