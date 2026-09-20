SET REFERENTIAL_INTEGRITY FALSE;

-- ----------------------------
-- 数据填充：sys_config（幂等插入）
-- ----------------------------
INSERT INTO `sys_config` (
    `id`, `config_key`, `config_value`, `value_type`, `config_group`, `sensitive`, `remark`
)
SELECT
    t.id,
    t.config_key,
    t.config_value,
    t.value_type,
    t.config_group,
    t.sensitive,
    t.remark
FROM (
         VALUES
             -- base（1000 段）
             (1001, 'name', '爱慕情侣小站', 'STRING', 'base', 0, '项目/站点名称'),
             (1002, 'icp', '京ICP备2026010001号', 'STRING', 'base', 0, 'ICP备案文案'),
             (1003, 'copyright-year', '2026', 'STRING', 'base', 0, '版权年份'),
             (1005, 'trust-proxy-headers', 'true', 'BOOLEAN', 'base', 0, '是否信任X-Real-IP等代理头(仅部署于可信反向代理后开启)'),
             (1006, 'logo', NULL, 'STRING', 'base', 0, '项目/站点logo图片(登录页/管理端/门户端统一)'),
             (1007, 'favicon', NULL, 'STRING', 'base', 0, '项目/站点favicon图标(登录页/管理端/门户端统一)'),
             -- site（2000 段）
             (2001, 'site.love-start-date', '2018-07-15 00:00:00', 'DATETIME', 'site', 0, '门户恋爱计时起点'),
             -- captcha（3000 段）
             (3001, 'captcha.enabled', 'true', 'BOOLEAN', 'captcha', 0, '验证码开关'),
             (3002, 'captcha.image-type', 'arithmetic', 'STRING', 'captcha', 0, '图形验证码类型'),
             -- file（4000 段）
             (4001, 'file.storage', 'local', 'STRING', 'file', 0, '文件存储方式: local-本地, oss-对象存储'),
             -- rate-limit（5000 段）
             (5001, 'rate-limit.login.limit', '5', 'INTEGER', 'rate-limit', 0, '登录接口-窗口内最大请求数(0 表示拒绝全部请求)'),
             (5002, 'rate-limit.login.window', '60', 'INTEGER', 'rate-limit', 0, '登录接口-时间窗口(秒)'),
             (5003, 'rate-limit.captcha.limit', '10', 'INTEGER', 'rate-limit', 0, '验证码接口-窗口内最大请求数(0 表示拒绝全部请求)'),
             (5004, 'rate-limit.captcha.window', '60', 'INTEGER', 'rate-limit', 0, '验证码接口-时间窗口(秒)'),
             (5005, 'rate-limit.qq-info.limit', '10', 'INTEGER', 'rate-limit', 0, 'QQ信息接口-窗口内最大请求数(0 表示拒绝全部请求)'),
             (5006, 'rate-limit.qq-info.window', '60', 'INTEGER', 'rate-limit', 0, 'QQ信息接口-时间窗口(秒)'),
             (5007, 'rate-limit.amap-proxy.limit', '30', 'INTEGER', 'rate-limit', 0, '高德服务代理接口-窗口内最大请求数(0 表示拒绝全部请求)'),
             (5008, 'rate-limit.amap-proxy.window', '60', 'INTEGER', 'rate-limit', 0, '高德服务代理接口-时间窗口(秒)'),
             (5009, 'rate-limit.saying.limit', '10', 'INTEGER', 'rate-limit', 0, '一言接口-窗口内最大请求数(0 表示拒绝全部请求)'),
             (5010, 'rate-limit.saying.window', '60', 'INTEGER', 'rate-limit', 0, '一言接口-时间窗口(秒)'),
             -- security（6000 段）
             (6001, 'security.uapi-key', NULL, 'STRING', 'security', 1, 'UApiPro接口密钥(https://uapis.cn)'),
             (6002, 'security.amap-key', NULL, 'STRING', 'security', 0, '高德地图Web端JS API Key(足迹地图选点/门户足迹地图展示;配域名白名单防滥用)'),
             (6003, 'security.amap-code', NULL, 'STRING', 'security', 1, '高德地图安全密钥(与Web端JS API Key配套,空表示未启用)')
     ) AS t(id, config_key, config_value, value_type, config_group, sensitive, remark)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_config` WHERE `sys_config`.`id` = t.id
);

-- ----------------------------
-- 数据填充：sys_user（幂等插入）
-- ----------------------------
INSERT INTO `sys_user` (
    `id`, `username`, `password`, `nickname`, `email`, `gender`, `qq`, `avatar`, `builtin`, `remark`
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
    t.remark
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
              '系统内置超级管理员'),
             (2,
              'li',
              '$2a$10$U.k0b43Pwg./Jg2QQl4bMOukItbYg4aYhKsciMamtHWvp3JEF2ism',
              'Li',
              'li@amour.com',
              'M',
              '2623669948',
              NULL,
              1,
              '系统内置门户男主'),
             (3,
              'su',
              '$2a$10$U.k0b43Pwg./Jg2QQl4bMOukItbYg4aYhKsciMamtHWvp3JEF2ism',
              'Su',
              'su@amour.com',
              'F',
              '673822943',
              NULL,
              1,
              '系统内置门户女主')
     ) AS t(id, username, password, nickname, email, gender, qq, avatar, builtin, remark)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_user` WHERE `sys_user`.`id` = t.id
);

-- ----------------------------
-- 数据填充：sys_role（幂等插入）
-- ----------------------------
INSERT INTO `sys_role` (
    `id`, `name`, `code`, `sort`, `builtin`, `remark`
)
SELECT
    t.id,
    t.name,
    t.code,
    t.sort,
    t.builtin,
    t.remark
FROM (
         VALUES
             (1, '超级管理员', 'admin', 1, 1, '系统内置超级管理员角色'),
             (2, '主角', 'hero', 2, 1, '系统内置门户主角角色')
     ) AS t(id, name, code, sort, builtin, remark)
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
         VALUES
             (1, 1, 1),
             (2, 2, 2),
             (3, 3, 2)
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
             (1000, 0, '系统管理', 'D', '/admin/system', NULL, 1, 'ep:setting', NULL, 1),
             (1100, 1000, '系统配置', 'M', '/admin/system/config', 'system/config/index', 1, 'ep:tools', NULL, 1),
             (1101, 1100, '分页查询', 'B', NULL, NULL, 1, NULL, 'system:config:page', 1),
             (1102, 1100, '详情', 'B', NULL, NULL, 2, NULL, 'system:config:detail', 1),
             (1103, 1100, '修改', 'B', NULL, NULL, 3, NULL, 'system:config:update', 1),
             (1104, 1100, '增加', 'B', NULL, NULL, 4, NULL, 'system:config:insert', 1),
             (1105, 1100, '删除', 'B', NULL, NULL, 5, NULL, 'system:config:delete', 1),
             (1200, 1000, '用户管理', 'M', '/admin/system/user', 'system/user/index', 2, 'ep:user', NULL, 1),
             (1201, 1200, '分页查询', 'B', NULL, NULL, 1, NULL, 'system:user:page', 1),
             (1202, 1200, '详情', 'B', NULL, NULL, 2, NULL, 'system:user:detail', 1),
             (1203, 1200, '修改', 'B', NULL, NULL, 3, NULL, 'system:user:update', 1),
             (1204, 1200, '增加', 'B', NULL, NULL, 4, NULL, 'system:user:insert', 1),
             (1205, 1200, '删除', 'B', NULL, NULL, 5, NULL, 'system:user:delete', 1),
             (1300, 1000, '角色管理', 'M', '/admin/system/role', 'system/role/index', 3, 'ep:avatar', NULL, 1),
             (1301, 1300, '分页查询', 'B', NULL, NULL, 1, NULL, 'system:role:page', 1),
             (1302, 1300, '详情', 'B', NULL, NULL, 2, NULL, 'system:role:detail', 1),
             (1303, 1300, '修改', 'B', NULL, NULL, 3, NULL, 'system:role:update', 1),
             (1304, 1300, '增加', 'B', NULL, NULL, 4, NULL, 'system:role:insert', 1),
             (1305, 1300, '删除', 'B', NULL, NULL, 5, NULL, 'system:role:delete', 1),
             (1400, 1000, '菜单管理', 'M', '/admin/system/menu', 'system/menu/index', 4, 'ep:menu', NULL, 1),
             (1401, 1400, '分页查询', 'B', NULL, NULL, 1, NULL, 'system:menu:page', 1),
             (1402, 1400, '详情', 'B', NULL, NULL, 2, NULL, 'system:menu:detail', 1),
             (1403, 1400, '修改', 'B', NULL, NULL, 3, NULL, 'system:menu:update', 1),
             (1404, 1400, '增加', 'B', NULL, NULL, 4, NULL, 'system:menu:insert', 1),
             (1405, 1400, '删除', 'B', NULL, NULL, 5, NULL, 'system:menu:delete', 1),
             (1500, 1000, '字典管理', 'M', '/admin/system/dict', 'system/dict/index', 5, 'ep:collection', NULL, 1),
             (1501, 1500, '分页查询', 'B', NULL, NULL, 1, NULL, 'system:dict:page', 1),
             (1502, 1500, '详情', 'B', NULL, NULL, 2, NULL, 'system:dict:detail', 1),
             (1503, 1500, '修改', 'B', NULL, NULL, 3, NULL, 'system:dict:update', 1),
             (1504, 1500, '增加', 'B', NULL, NULL, 4, NULL, 'system:dict:insert', 1),
             (1505, 1500, '删除', 'B', NULL, NULL, 5, NULL, 'system:dict:delete', 1),
             (1600, 1000, '缓存监控', 'M', '/admin/system/cache', 'system/cache/index', 6, 'ep:monitor', NULL, 1),
             (1601, 1600, '查询', 'B', NULL, NULL, 1, NULL, 'system:cache:list', 1),
             (1700, 1000, '日志管理', 'M', '/admin/system/log', 'system/log/index', 7, 'ep:document', NULL, 1),
             (1701, 1700, '登录日志分页查询', 'B', NULL, NULL, 1, NULL, 'log:login:page', 1),
             (1702, 1700, '操作日志分页查询', 'B', NULL, NULL, 2, NULL, 'log:operate:page', 1),
             (1800, 1000, '文件管理', 'M', '/admin/system/file', 'system/file/index', 8, 'ep:folder-opened', NULL, 1),
             (1801, 1800, '文件分页查询', 'B', NULL, NULL, 1, NULL, 'system:file:page', 1),
             (1802, 1800, '删除文件', 'B', NULL, NULL, 2, NULL, 'system:file:delete', 1),
             (2000, 0, '恋爱画册', 'M', '/admin/love-photo', 'admin/love-photo/index', 2, 'ep:camera', NULL, 1),
             (2001, 2000, '分页查询', 'B', NULL, NULL, 1, NULL, 'admin:love-photo:page', 1),
             (2002, 2000, '增加', 'B', NULL, NULL, 2, NULL, 'admin:love-photo:insert', 1),
             (2003, 2000, '修改', 'B', NULL, NULL, 3, NULL, 'admin:love-photo:update', 1),
             (2004, 2000, '删除', 'B', NULL, NULL, 4, NULL, 'admin:love-photo:delete', 1),
             (2100, 0, '足迹管理', 'M', '/admin/footprint', 'admin/footprint/index', 3, 'ep:location', NULL, 1),
             (2101, 2100, '分页查询', 'B', NULL, NULL, 1, NULL, 'admin:footprint:page', 1),
             (2102, 2100, '增加', 'B', NULL, NULL, 2, NULL, 'admin:footprint:insert', 1),
             (2103, 2100, '修改', 'B', NULL, NULL, 3, NULL, 'admin:footprint:update', 1),
             (2104, 2100, '删除', 'B', NULL, NULL, 4, NULL, 'admin:footprint:delete', 1),
             (3000, 0, '个人中心', 'M', '/admin/profile', 'profile/index', 3, 'ep:avatar', NULL, 1)
     ) AS t(id, pid, title, type, path, component, sort, icon, perms, builtin)
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
    `id`, `dict_code`, `dict_name`, `builtin`, `remark`
)
SELECT
    t.id,
    t.dict_code,
    t.dict_name,
    t.builtin,
    t.remark
FROM (
         VALUES
             (99001, 'gender', '性别', 1, '与 GenderEnum(U/M/F) 对齐'),
             (99002, 'enable', '启用状态', 1, '与 EnableEnum(0/1) 对齐'),
             (99003, 'yes', '是否', 1, '与 YesEnum(1/0) 对齐'),
             (99004, 'del-flag', '删除标识', 1, '与 DelFlagEnum(1/0) 对齐'),
             (99005, 'menu-type', '菜单类型', 1, '与 MenuType(D/M/B) 对齐'),
             (99006, 'image-type', '图形验证码类型', 1, '与 ImageType(spec/gif/chinese/chinese-gif/arithmetic) 对齐'),
             (99007, 'success', '成功状态', 1, '与 SuccessEnum(1/0) 对齐'),
             (99008, 'config-group', '配置分组', 1, '与 sys_config.config_group(base/site/captcha/file/rate-limit/security) 对齐'),
             (99009, 'config-value-type', '配置值类型', 1, '与 sys_config.value_type(STRING/INTEGER/LONG/BOOLEAN/DATETIME) 对齐'),
             (99010, 'file-storage-type', '存储类型', 1, '与 StorageTypeEnum(local/oss) 对齐'),
             (99011, 'biz-type', '文件业务类型', 1, '与 FileBizTypeEnum(infra/avatar/photo/markdown) 对齐'),
             (99012, 'log-type', '日志类型', 1, '与 LogTypeEnum 对齐'),
             (99013, 'hidden', '显隐状态', 1, '与 HiddenEnum(0/1) 对齐')
     ) AS t(id, dict_code, dict_name, builtin, remark)
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
    `id`, `dict_code`, `dict_value`, `dict_label`, `sort`, `status`, `builtin`, `remark`
)
SELECT
    t.id,
    t.dict_code,
    t.dict_value,
    t.dict_label,
    t.sort,
    t.status,
    t.builtin,
    t.remark
FROM (
         VALUES
             -- gender（性别，对应 GenderEnum：U-未知,M-男,F-女；10000 段）
             (10001, 'gender', 'U', '未知', 1, 0, 1, '与 GenderEnum(U/M/F) 对齐'),
             (10002, 'gender', 'M', '男', 2, 0, 1, '与 GenderEnum(U/M/F) 对齐'),
             (10003, 'gender', 'F', '女', 3, 0, 1, '与 GenderEnum(U/M/F) 对齐'),
             -- enable（通用启停状态，对应 EnableEnum：0-启用,1-禁用；10100 段）
             (10101, 'enable', '0', '启用', 1, 0, 1, '与 EnableEnum(0/1) 对齐'),
             (10102, 'enable', '1', '禁用', 2, 0, 1, '与 EnableEnum(0/1) 对齐'),
             -- yes（是否，对应 YesEnum：1-是,0-否；10200 段）
             (10201, 'yes', '1', '是', 1, 0, 1, '与 YesEnum(1/0) 对齐'),
             (10202, 'yes', '0', '否', 2, 0, 1, '与 YesEnum(1/0) 对齐'),
             -- del-flag（删除标识，对应 DelFlagEnum：1-已删除,0-未删除；10300 段）
             (10301, 'del-flag', '0', '未删除', 1, 0, 1, '与 DelFlagEnum(1/0) 对齐'),
             (10302, 'del-flag', '1', '已删除', 2, 0, 1, '与 DelFlagEnum(1/0) 对齐'),
             -- menu-type（菜单类型，对应 MenuType：D-目录,M-菜单,B-按钮；10400 段）
             (10401, 'menu-type', 'D', '目录', 1, 0, 1, '与 MenuType(D/M/B) 对齐'),
             (10402, 'menu-type', 'M', '菜单', 2, 0, 1, '与 MenuType(D/M/B) 对齐'),
             (10403, 'menu-type', 'B', '按钮', 3, 0, 1, '与 MenuType(D/M/B) 对齐'),
             -- image-type（图形验证码类型，对应 ImageType；10500 段）
             (10501, 'image-type', 'spec', 'PNG字符验证码', 1, 0, 1, '与 ImageType(spec/gif/chinese/chinese-gif/arithmetic) 对齐'),
             (10502, 'image-type', 'gif', 'GIF字符验证码', 2, 0, 1, '与 ImageType(spec/gif/chinese/chinese-gif/arithmetic) 对齐'),
             (10503, 'image-type', 'chinese', '中文字符验证码', 3, 0, 1, '与 ImageType(spec/gif/chinese/chinese-gif/arithmetic) 对齐'),
             (10504, 'image-type', 'chinese-gif', '中文GIF字符验证码', 4, 0, 1, '与 ImageType(spec/gif/chinese/chinese-gif/arithmetic) 对齐'),
             (10505, 'image-type', 'arithmetic', '算术验证码', 5, 0, 1, '与 ImageType(spec/gif/chinese/chinese-gif/arithmetic) 对齐'),
             -- success（成功状态，对应 SuccessEnum：1-成功,0-失败；10600 段）
             (10601, 'success', '1', '成功', 1, 0, 1, '与 SuccessEnum(1/0) 对齐'),
             (10602, 'success', '0', '失败', 2, 0, 1, '与 SuccessEnum(1/0) 对齐'),
             -- config-group（配置分组，与 sys_config.config_group 对齐：base-基础,site-门户,captcha-验证码,file-文件,rate-limit-接口限流；10700 段）
             (10701, 'config-group', 'base', '基础配置', 1, 0, 1, '与 sys_config.config_group(base/site/captcha) 对齐'),
             (10702, 'config-group', 'site', '门户配置', 2, 0, 1, '与 sys_config.config_group(base/site/captcha) 对齐'),
             (10703, 'config-group', 'captcha', '验证码配置', 3, 0, 1, '与 sys_config.config_group(base/site/captcha) 对齐'),
             (10704, 'config-group', 'file', '文件配置', 4, 0, 1, '与 sys_config.config_group(base/site/captcha/file) 对齐'),
             (10705, 'config-group', 'rate-limit', '接口限流', 5, 0, 1, '与 sys_config.config_group(base/site/captcha/file/rate-limit) 对齐'),
             (10706, 'config-group', 'security', '安全配置', 6, 0, 1, '与 sys_config.config_group 对齐(密钥类配置:security.uapi-key/security.amap-key/security.amap-code)'),
             -- config-value-type（配置值类型，与 sys_config.value_type 对齐：STRING/INTEGER/LONG/BOOLEAN/DATETIME；10800 段）
             (10801, 'config-value-type', 'STRING', '字符串', 1, 0, 1, '与 sys_config.value_type(STRING/INTEGER/LONG/BOOLEAN) 对齐'),
             (10802, 'config-value-type', 'INTEGER', '整数', 2, 0, 1, '与 sys_config.value_type(STRING/INTEGER/LONG/BOOLEAN) 对齐'),
             (10803, 'config-value-type', 'LONG', '长整数', 3, 0, 1, '与 sys_config.value_type(STRING/INTEGER/LONG/BOOLEAN) 对齐'),
             (10804, 'config-value-type', 'BOOLEAN', '布尔', 4, 0, 1, '与 sys_config.value_type(STRING/INTEGER/LONG/BOOLEAN) 对齐'),
             (10805, 'config-value-type', 'DATETIME', '日期时间', 5, 0, 1, '与 sys_config.value_type(STRING/INTEGER/LONG/BOOLEAN/DATETIME) 对齐'),
             -- file-storage-type（存储类型，对应 StorageTypeEnum：local-本地,oss-对象存储；10900 段）
             (10901, 'file-storage-type', 'local', '本地存储', 1, 0, 1, '与 StorageTypeEnum(local/oss) 对齐'),
             (10902, 'file-storage-type', 'oss', '对象存储', 2, 0, 1, '与 StorageTypeEnum(local/oss) 对齐'),
             -- biz-type（文件业务类型，对应 FileBizTypeEnum：infra-基础设施,avatar-用户头像,photo-相册照片,markdown-点滴配图；11000 段）
             (11001, 'biz-type', 'infra', '基础设施', 1, 0, 1, '与 FileBizTypeEnum(infra/avatar/photo/markdown) 对齐'),
             (11002, 'biz-type', 'avatar', '用户头像', 2, 0, 1, '与 FileBizTypeEnum(infra/avatar/photo/markdown) 对齐'),
             (11003, 'biz-type', 'photo', '相册照片', 3, 0, 1, '与 FileBizTypeEnum(infra/avatar/photo/markdown) 对齐'),
             (11004, 'biz-type', 'markdown', '点滴配图', 4, 0, 1, '与 FileBizTypeEnum(infra/avatar/photo/markdown) 对齐'),
             -- log-type（日志类型，对应 LogTypeEnum：0-未知,1-登录,2-登出,3-查询,4-新增,5-修改,6-删除,7-授权,8-上传,9-下载；11100 段）
             (11101, 'log-type', '0', '未知', 1, 0, 1, '与 LogTypeEnum 对齐'),
             (11102, 'log-type', '1', '登录', 2, 0, 1, '与 LogTypeEnum 对齐'),
             (11103, 'log-type', '2', '登出', 3, 0, 1, '与 LogTypeEnum 对齐'),
             (11104, 'log-type', '3', '查询', 4, 0, 1, '与 LogTypeEnum 对齐'),
             (11105, 'log-type', '4', '新增', 5, 0, 1, '与 LogTypeEnum 对齐'),
             (11106, 'log-type', '5', '修改', 6, 0, 1, '与 LogTypeEnum 对齐'),
             (11107, 'log-type', '6', '删除', 7, 0, 1, '与 LogTypeEnum 对齐'),
             (11108, 'log-type', '7', '授权', 8, 0, 1, '与 LogTypeEnum 对齐'),
             (11109, 'log-type', '8', '上传', 9, 0, 1, '与 LogTypeEnum 对齐'),
             (11110, 'log-type', '9', '下载', 10, 0, 1, '与 LogTypeEnum 对齐'),
             -- hidden（照片显隐，对应 HiddenEnum：0-显示,1-隐藏；11200 段）
             (11201, 'hidden', '0', '显示', 1, 0, 1, '与 HiddenEnum(0/1) 对齐'),
             (11202, 'hidden', '1', '隐藏', 2, 0, 1, '与 HiddenEnum(0/1) 对齐')
     ) AS t(id, dict_code, dict_value, dict_label, sort, status, builtin, remark)
WHERE NOT EXISTS (
    SELECT 1 FROM `sys_dict_data` WHERE `sys_dict_data`.`id` = t.id
);

-- ----------------------------
-- 数据填充：portal_love_photo（幂等插入）
-- 恋爱画册门户展示的示例照片（hidden=0 门户可见）；id 使用独立的 30000 段顺序预留
-- ----------------------------
INSERT INTO `portal_love_photo` (
    `id`, `url`, `caption`, `date_text`, `tags`, `sort`, `hidden`
)
SELECT
    t.id,
    t.url,
    t.caption,
    t.date_text,
    t.tags,
    t.sort,
    t.hidden
FROM (
         VALUES
             (30001, 'https://t.alcy.cc/pic/fj/130.webp', '我们的第一张合照', '2018-07-15', '日常', 1, 0)
     ) AS t(id, url, caption, date_text, tags, sort, hidden)
WHERE NOT EXISTS (
    SELECT 1 FROM `portal_love_photo` WHERE `portal_love_photo`.`id` = t.id
);

-- ----------------------------
-- 数据填充：portal_footprint（幂等插入）
-- 足迹地图门户展示的示例足迹（photo_url 可空,为空时门户按无照片渲染）；id 使用独立的 31000 段顺序预留
-- ----------------------------
INSERT INTO `portal_footprint` (
    `id`, `city`, `place_name`, `longitude`, `latitude`, `arrival_date`, `photo_url`, `remark`
)
SELECT
    t.id,
    t.city,
    t.place_name,
    t.longitude,
    t.latitude,
    t.arrival_date,
    t.photo_url,
    t.remark
FROM (
         VALUES
             (31001, '成都', '宽窄巷子', 104.053600, 30.669800, '2024-10-02', NULL, '第一次一起逛巷子,看了变脸吃了三大炮')
     ) AS t(id, city, place_name, longitude, latitude, arrival_date, photo_url, remark)
WHERE NOT EXISTS (
    SELECT 1 FROM `portal_footprint` WHERE `portal_footprint`.`id` = t.id
);

SET REFERENTIAL_INTEGRITY TRUE;
