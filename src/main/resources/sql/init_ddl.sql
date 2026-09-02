-- ----------------------------
-- 表结构：sys_config（系统配置表）
-- 幂等建表：仅当表不存在时创建
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_config` (
    `id`           BIGINT        NOT NULL                COMMENT '主键ID',
    `config_key`   VARCHAR(128)  NULL DEFAULT NULL       COMMENT '配置键',
    `config_value` VARCHAR(4000) NULL DEFAULT NULL       COMMENT '配置值',
    `value_type`   VARCHAR(16)   NULL DEFAULT NULL       COMMENT '值类型: STRING, INTEGER, LONG, BOOLEAN',
    `config_group` VARCHAR(64)   NULL DEFAULT NULL       COMMENT '分组',
    `status`       TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '配置状态: 0-启用, 1-禁用',
    `remark`       VARCHAR(512)  NULL DEFAULT NULL       COMMENT '备注',
    `creator`      BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
    `create_time`  DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
    `updater`      BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
    `update_time`  DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
    `del_flag`     TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_c_config_key` (`config_key` ASC)
    ) ENGINE = InnoDB
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_general_ci
    ROW_FORMAT = DYNAMIC
    COMMENT = '系统配置表';

-- ----------------------------
-- 表结构：sys_user（用户信息表）
-- 幂等建表：仅当表不存在时创建
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`          BIGINT        NOT NULL                COMMENT '用户ID',
    `username`    VARCHAR(128)  NULL DEFAULT NULL       COMMENT '用户名称',
    `password`    VARCHAR(512)  NULL DEFAULT NULL       COMMENT '用户密码',
    `nickname`    VARCHAR(64)   NULL DEFAULT NULL       COMMENT '用户昵称',
    `id_card`     VARCHAR(64)   NULL DEFAULT NULL       COMMENT '用户身份证号码',
    `email`       VARCHAR(64)   NULL DEFAULT NULL       COMMENT '用户邮箱',
    `phone`       VARCHAR(11)   NULL DEFAULT NULL       COMMENT '用户手机号码',
    `gender`      VARCHAR(1)    NOT NULL DEFAULT 'U'    COMMENT '用户性别: U-未知, M-男, F-女',
    `qq`          VARCHAR(12)   NULL DEFAULT NULL       COMMENT '用户QQ号码',
    `avatar`      VARCHAR(512)  NULL DEFAULT NULL       COMMENT '用户头像地址',
    `status`      TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '用户状态: 0-启用, 1-禁用',
    `builtin`     TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '内置标识: 0-非内置, 1-内置',
    `remark`      VARCHAR(512)  NULL DEFAULT NULL       COMMENT '备注',
    `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
    `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
    `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
    `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
    `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_u_username` (`username` ASC),
    INDEX `idx_u_status`   (`status` ASC)
    ) ENGINE = InnoDB
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_general_ci
    ROW_FORMAT = DYNAMIC
    COMMENT = '用户信息表';

-- ----------------------------
-- 表结构：sys_role（角色信息表）
-- 幂等建表：仅当表不存在时创建
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id`          BIGINT        NOT NULL                COMMENT '角色ID',
    `name`        VARCHAR(64)   NULL DEFAULT NULL       COMMENT '角色名称',
    `code`        VARCHAR(64)   NULL DEFAULT NULL       COMMENT '角色编码',
    `sort`        INT           NOT NULL DEFAULT 0      COMMENT '排序（数字越小越靠前）',
    `status`      TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '角色状态: 0-启用, 1-禁用',
    `builtin`     TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '内置标识: 0-非内置, 1-内置',
    `remark`      VARCHAR(512)  NULL DEFAULT NULL       COMMENT '备注',
    `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
    `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
    `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
    `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
    `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_r_code`   (`code` ASC),
    INDEX `idx_r_status` (`status` ASC)
    ) ENGINE = InnoDB
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_general_ci
    ROW_FORMAT = DYNAMIC
    COMMENT = '角色信息表';

-- ----------------------------
-- 表结构：sys_user_role（用户角色关联表）
-- 幂等建表：仅当表不存在时创建
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id`          BIGINT        NOT NULL                COMMENT '主键ID',
    `user_id`     BIGINT        NULL DEFAULT NULL       COMMENT '用户ID',
    `role_id`     BIGINT        NULL DEFAULT NULL       COMMENT '角色ID',
    `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
    `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
    `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
    `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
    `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_ur_user_id` (`user_id` ASC),
    INDEX `idx_ur_role_id` (`role_id` ASC)
    ) ENGINE = InnoDB
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_general_ci
    ROW_FORMAT = DYNAMIC
    COMMENT = '用户角色关联表';

-- ----------------------------
-- 表结构：sys_menu（菜单表）
-- 幂等建表：仅当表不存在时创建
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id`          BIGINT        NOT NULL                COMMENT '菜单ID',
    `pid`         BIGINT        NOT NULL DEFAULT 0      COMMENT '父级菜单ID',
    `title`       VARCHAR(256)  NULL DEFAULT NULL       COMMENT '菜单名称',
    `type`        VARCHAR(1)    NULL DEFAULT NULL       COMMENT '菜单类型: D-目录, M-菜单, B-按钮',
    `path`        VARCHAR(512)  NULL DEFAULT NULL       COMMENT '路由路径',
    `component`   VARCHAR(256)  NULL DEFAULT NULL       COMMENT '组件路径',
    `sort`        INT           NOT NULL DEFAULT 0      COMMENT '排序（数字越小越靠前）',
    `icon`        VARCHAR(256)  NULL DEFAULT NULL       COMMENT '菜单图标',
    `perms`       VARCHAR(64)   NULL DEFAULT NULL       COMMENT '权限编码',
    `status`      TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '菜单状态: 0-启用, 1-禁用',
    `hidden`      TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '显隐标识: 0-显示, 1-隐藏',
    `builtin`     TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '内置标识: 0-非内置, 1-内置',
    `remark`      VARCHAR(512)  NULL DEFAULT NULL       COMMENT '备注',
    `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
    `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
    `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
    `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
    `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_m_status` (`status` ASC)
    ) ENGINE = InnoDB
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_general_ci
    ROW_FORMAT = DYNAMIC
    COMMENT = '菜单表';

-- ----------------------------
-- 表结构：sys_role_menu（角色菜单关联表）
-- 幂等建表：仅当表不存在时创建
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id`          BIGINT        NOT NULL                COMMENT '主键ID',
    `role_id`     BIGINT        NULL DEFAULT NULL       COMMENT '角色ID',
    `menu_id`     BIGINT        NULL DEFAULT NULL       COMMENT '菜单ID',
    `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
    `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
    `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
    `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
    `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_rm_role_id` (`role_id` ASC),
    INDEX `idx_rm_menu_id` (`menu_id` ASC)
    ) ENGINE = InnoDB
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_general_ci
    ROW_FORMAT = DYNAMIC
    COMMENT = '角色菜单关联表';

-- ----------------------------
-- 表结构：sys_log（系统日志表）
-- 幂等建表：仅当表不存在时创建
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_log` (
    `id`          BIGINT        NOT NULL                COMMENT '主键ID',
    `trace_id`    VARCHAR(64)   NULL DEFAULT NULL       COMMENT '链路追踪ID',
    `log_type`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '日志类型: 0-未知, 1-登录, 2-登出, 3-查询, 4-新增, 5-修改, 6-删除, 7-授权, 8-上传, 9-下载',
    `user_id`     BIGINT        NULL DEFAULT NULL       COMMENT '用户ID',
    `username`    VARCHAR(128)  NULL DEFAULT NULL       COMMENT '用户名称',
    `module`      VARCHAR(64)   NULL DEFAULT NULL       COMMENT '操作模块',
    `operation`   VARCHAR(128)  NULL DEFAULT NULL       COMMENT '操作描述',
    `method`      VARCHAR(256)  NULL DEFAULT NULL       COMMENT '请求类方法',
    `url`         VARCHAR(512)  NULL DEFAULT NULL       COMMENT '请求接口地址',
    `ip`          VARCHAR(64)   NULL DEFAULT NULL       COMMENT '操作IP',
    `region`      VARCHAR(64)   NULL DEFAULT NULL       COMMENT 'IP归属地',
    `elapsed`     BIGINT        NULL DEFAULT NULL       COMMENT '耗时（毫秒）',
    `status`      TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '操作状态: 0-成功, 1-失败',
    `msg`         VARCHAR(512)  NULL DEFAULT NULL       COMMENT '描述/失败原因',
    `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
    `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
    `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
    `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
    `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_l_trace_id` (`trace_id` ASC),
    INDEX `idx_l_log_type` (`log_type` ASC),
    INDEX `idx_l_username`  (`username` ASC)
    ) ENGINE = InnoDB
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_general_ci
    ROW_FORMAT = DYNAMIC
    COMMENT = '系统日志表';

-- ----------------------------
-- 表结构：sys_file（文件记录表）
-- 幂等建表：仅当表不存在时创建
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_file` (
    `id`            BIGINT        NOT NULL                COMMENT '主键ID',
    `original_name` VARCHAR(256)  NULL DEFAULT NULL       COMMENT '原始文件名',
    `path`          VARCHAR(512)  NULL DEFAULT NULL       COMMENT '存储路径',
    `size`          BIGINT        NULL DEFAULT NULL       COMMENT '文件大小（字节）',
    `md5`           VARCHAR(64)   NULL DEFAULT NULL       COMMENT '文件MD5值',
    `storage_type`  VARCHAR(16)   NULL DEFAULT NULL       COMMENT '存储类型: local-本地, oss-对象存储',
    `extension`     VARCHAR(16)   NULL DEFAULT NULL       COMMENT '文件扩展名',
    `content_type`  VARCHAR(128)  NULL DEFAULT NULL       COMMENT '文件类型',
    `biz_type`      VARCHAR(64)   NULL DEFAULT NULL       COMMENT '业务来源: avatar-用户头像, markdown-点滴配图, photo-相册照片',
    `biz_id`        BIGINT        NULL DEFAULT NULL       COMMENT '业务关联ID',
    `creator`       BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
    `create_time`   DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
    `updater`       BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
    `update_time`   DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
    `del_flag`      TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_f_biz`         (`biz_type` ASC, `biz_id` ASC),
    INDEX `idx_f_md5`         (`md5` ASC),
    INDEX `idx_f_create_time` (`create_time` ASC)
    ) ENGINE = InnoDB
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_general_ci
    ROW_FORMAT = DYNAMIC
    COMMENT = '文件记录表';

-- ----------------------------
-- 表结构：sys_dict（数据字典表）
-- 幂等建表：仅当表不存在时创建
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_dict` (
    `id`          BIGINT        NOT NULL                COMMENT '主键ID',
    `dict_code`   VARCHAR(64)   NULL DEFAULT NULL       COMMENT '字典编码',
    `dict_name`   VARCHAR(64)   NULL DEFAULT NULL       COMMENT '字典名称',
    `dict_value`  VARCHAR(128)  NULL DEFAULT NULL       COMMENT '字典值',
    `dict_label`  VARCHAR(128)  NULL DEFAULT NULL       COMMENT '字典标签',
    `sort`        INT           NOT NULL DEFAULT 0      COMMENT '排序（数字越小越靠前）',
    `status`      TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '状态: 0-启用, 1-禁用',
    `builtin`     TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '内置标识: 0-非内置, 1-内置',
    `remark`      VARCHAR(512)  NULL DEFAULT NULL       COMMENT '备注',
    `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
    `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
    `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
    `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
    `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_d_dict_code` (`dict_code` ASC),
    INDEX `idx_d_status` (`status` ASC)
    ) ENGINE = InnoDB
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_general_ci
    ROW_FORMAT = DYNAMIC
    COMMENT = '数据字典表';


-- =====================================================================================================================
-- -- ----------------------------
-- -- 表结构：sys_notice（通知公告表）
-- -- 幂等建表：仅当表不存在时创建
-- -- ----------------------------
-- CREATE TABLE IF NOT EXISTS `sys_notice` (
--     `id`          BIGINT        NOT NULL                COMMENT '主键ID',
--     `title`       VARCHAR(128)  NULL DEFAULT NULL       COMMENT '公告标题',
--     `content`     TEXT          NULL DEFAULT NULL       COMMENT '公告内容',
--     `type`        TINYINT(1)    NULL DEFAULT NULL       COMMENT '公告类型: 1-通知, 2-公告',
--     `status`      TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '状态: 0-启用, 1-禁用',
--     `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
--     `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
--     `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
--     `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
--     `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
--     PRIMARY KEY (`id`),
--     INDEX `idx_n_status` (`status` ASC)
--     ) ENGINE = InnoDB
--     CHARACTER SET = utf8mb4
--     COLLATE = utf8mb4_general_ci
--     ROW_FORMAT = DYNAMIC
--     COMMENT = '通知公告表';
--
-- -- ----------------------------
-- -- 表结构：portal_little（点点滴滴文章表）
-- -- 幂等建表：仅当表不存在时创建
-- -- ----------------------------
-- CREATE TABLE IF NOT EXISTS `portal_little` (
--     `id`          BIGINT        NOT NULL                COMMENT '主键ID',
--     `title`       VARCHAR(256)  NOT NULL                COMMENT '文章标题',
--     `author`      VARCHAR(64)   NULL DEFAULT NULL       COMMENT '作者',
--     `content`     TEXT          NULL DEFAULT NULL       COMMENT '文章内容',
--     `record_date` DATE          NULL DEFAULT NULL       COMMENT '记录日期',
--     `sort`        INT           NOT NULL DEFAULT 0      COMMENT '排序（数字越小越靠前）',
--     `status`      TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '状态: 0-显示, 1-隐藏',
--     `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
--     `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
--     `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
--     `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
--     `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
--     PRIMARY KEY (`id`),
--     INDEX `idx_plt_record_date` (`record_date` ASC)
--     ) ENGINE = InnoDB
--     CHARACTER SET = utf8mb4
--     COLLATE = utf8mb4_general_ci
--     ROW_FORMAT = DYNAMIC
--     COMMENT = '点点滴滴文章表';
--
-- -- ----------------------------
-- -- 表结构：portal_leaving（留言表）
-- -- 幂等建表：仅当表不存在时创建
-- -- ----------------------------
-- CREATE TABLE IF NOT EXISTS `portal_leaving` (
--     `id`           BIGINT        NOT NULL                COMMENT '主键ID',
--     `nickname`     VARCHAR(64)   NOT NULL                COMMENT '访客昵称',
--     `avatar`       VARCHAR(512)  NULL DEFAULT NULL       COMMENT '留言头像（随机头像生成即落库，终身固定）',
--     `content`      VARCHAR(1024) NOT NULL                COMMENT '留言内容',
--     `ip`           VARCHAR(64)   NULL DEFAULT NULL       COMMENT '留言IP',
--     `region`       VARCHAR(64)   NULL DEFAULT NULL       COMMENT 'IP归属地',
--     `audit_status` TINYINT(1)    NOT NULL DEFAULT 1      COMMENT '审核状态: 0-待审核, 1-通过, 2-驳回',
--     `creator`      BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
--     `create_time`  DATETIME      NULL DEFAULT NULL       COMMENT '留言时间',
--     `updater`      BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
--     `update_time`  DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
--     `del_flag`     TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
--     PRIMARY KEY (`id`),
--     INDEX `idx_plv_create_time` (`create_time` ASC)
--     ) ENGINE = InnoDB
--     CHARACTER SET = utf8mb4
--     COLLATE = utf8mb4_general_ci
--     ROW_FORMAT = DYNAMIC
--     COMMENT = '留言表';
--
-- -- ----------------------------
-- -- 表结构：portal_photo（恋爱相册照片表）
-- -- 幂等建表：仅当表不存在时创建
-- -- ----------------------------
-- CREATE TABLE IF NOT EXISTS `portal_photo` (
--     `id`          BIGINT        NOT NULL                COMMENT '主键ID',
--     `url`         VARCHAR(512)  NOT NULL                COMMENT '照片地址',
--     `description` VARCHAR(256)  NULL DEFAULT NULL       COMMENT '悬浮文案（日期）',
--     `tag`         VARCHAR(64)   NULL DEFAULT NULL       COMMENT '照片标签（旅行/日常/节日等）',
--     `sort`        INT           NOT NULL DEFAULT 0      COMMENT '排序（数字越小越靠前）',
--     `status`      TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '状态: 0-显示, 1-隐藏',
--     `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
--     `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
--     `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
--     `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
--     `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
--     PRIMARY KEY (`id`),
--     INDEX `idx_pp_sort` (`sort` ASC)
--     ) ENGINE = InnoDB
--     CHARACTER SET = utf8mb4
--     COLLATE = utf8mb4_general_ci
--     ROW_FORMAT = DYNAMIC
--     COMMENT = '恋爱相册照片表';
--
-- -- ----------------------------
-- -- 表结构：portal_event（恋爱清单表）
-- -- 幂等建表：仅当表不存在时创建
-- -- ----------------------------
-- CREATE TABLE IF NOT EXISTS `portal_event` (
--     `id`          BIGINT        NOT NULL                COMMENT '主键ID',
--     `content`     VARCHAR(256)  NOT NULL                COMMENT '清单内容',
--     `done`        TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '完成状态: 0-未完成, 1-已完成',
--     `img`         VARCHAR(512)  NULL DEFAULT NULL       COMMENT '纪念照地址（完成项可选）',
--     `sort`        INT           NOT NULL DEFAULT 0      COMMENT '排序（数字越小越靠前）',
--     `status`      TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '状态: 0-显示, 1-隐藏',
--     `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
--     `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
--     `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
--     `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
--     `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
--     PRIMARY KEY (`id`),
--     INDEX `idx_pe_sort` (`sort` ASC)
--     ) ENGINE = InnoDB
--     CHARACTER SET = utf8mb4
--     COLLATE = utf8mb4_general_ci
--     ROW_FORMAT = DYNAMIC
--     COMMENT = '恋爱清单表';
--
-- -- ----------------------------
-- -- 表结构：portal_chat（对话剧本表）
-- -- 幂等建表：仅当表不存在时创建
-- -- ----------------------------
-- CREATE TABLE IF NOT EXISTS `portal_chat` (
--     `id`          BIGINT        NOT NULL                COMMENT '主键ID',
--     `title`       VARCHAR(128)  NULL DEFAULT NULL       COMMENT '剧本名称',
--     `content`     TEXT          NULL                    COMMENT '剧本JSON（节点/选项结构，见 GET /love/chat 契约）',
--     `status`      TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '状态: 0-启用, 1-停用',
--     `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
--     `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
--     `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
--     `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
--     `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
--     PRIMARY KEY (`id`),
--     INDEX `idx_pch_status` (`status` ASC)
--     ) ENGINE = InnoDB
--     CHARACTER SET = utf8mb4
--     COLLATE = utf8mb4_general_ci
--     ROW_FORMAT = DYNAMIC
--     COMMENT = '对话剧本表';
--
-- -- ----------------------------
-- -- 表结构：portal_anniversary（纪念日表）
-- -- 幂等建表：仅当表不存在时创建
-- -- ----------------------------
-- CREATE TABLE IF NOT EXISTS `portal_anniversary` (
--     `id`                BIGINT        NOT NULL                COMMENT '主键ID',
--     `name`              VARCHAR(128)  NOT NULL                COMMENT '纪念日名称',
--     `type`              TINYINT(1)    NULL DEFAULT NULL       COMMENT '纪念日类型: 1-生日, 2-纪念日, 3-节日',
--     `anniversary_date`  DATE          NOT NULL                COMMENT '纪念日日期（每年重复时仅取月/日）',
--     `repeat_yearly`     TINYINT(1)    NOT NULL DEFAULT 1      COMMENT '是否每年重复: 0-否, 1-是',
--     `sort`              INT           NOT NULL DEFAULT 0      COMMENT '排序（数字越小越靠前）',
--     `status`            TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '状态: 0-显示, 1-隐藏',
--     `creator`           BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
--     `create_time`       DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
--     `updater`           BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
--     `update_time`       DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
--     `del_flag`          TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
--     PRIMARY KEY (`id`),
--     INDEX `idx_pan_date` (`anniversary_date` ASC)
--     ) ENGINE = InnoDB
--     CHARACTER SET = utf8mb4
--     COLLATE = utf8mb4_general_ci
--     ROW_FORMAT = DYNAMIC
--     COMMENT = '纪念日表';
--
-- -- ----------------------------
-- -- 表结构：portal_quote（情话/每日一句表）
-- -- 幂等建表：仅当表不存在时创建
-- -- ----------------------------
-- CREATE TABLE IF NOT EXISTS `portal_quote` (
--     `id`          BIGINT        NOT NULL                COMMENT '主键ID',
--     `content`     VARCHAR(512)  NOT NULL                COMMENT '情话内容',
--     `author`      VARCHAR(64)   NULL DEFAULT NULL       COMMENT '来源/作者',
--     `show_date`   DATE          NULL DEFAULT NULL       COMMENT '指定展示日期（空为随机轮播）',
--     `status`      TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '状态: 0-显示, 1-隐藏',
--     `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
--     `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
--     `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
--     `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
--     `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
--     PRIMARY KEY (`id`),
--     INDEX `idx_pq_show_date` (`show_date` ASC)
--     ) ENGINE = InnoDB
--     CHARACTER SET = utf8mb4
--     COLLATE = utf8mb4_general_ci
--     ROW_FORMAT = DYNAMIC
--     COMMENT = '情话/每日一句表';
--
-- -- ----------------------------
-- -- 表结构：portal_letter（时间胶囊表）
-- -- 幂等建表：仅当表不存在时创建
-- -- ----------------------------
-- CREATE TABLE IF NOT EXISTS `portal_letter` (
--     `id`          BIGINT        NOT NULL                COMMENT '主键ID',
--     `title`       VARCHAR(128)  NULL DEFAULT NULL       COMMENT '标题',
--     `content`     TEXT          NULL                    COMMENT '信件内容',
--     `open_time`   DATETIME      NULL DEFAULT NULL       COMMENT '解锁时间',
--     `status`      TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '状态: 0-显示, 1-隐藏',
--     `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
--     `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
--     `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
--     `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
--     `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
--     PRIMARY KEY (`id`),
--     INDEX `idx_ple_open_time` (`open_time` ASC)
--     ) ENGINE = InnoDB
--     CHARACTER SET = utf8mb4
--     COLLATE = utf8mb4_general_ci
--     ROW_FORMAT = DYNAMIC
--     COMMENT = '时间胶囊表';
--
-- -- ----------------------------
-- -- 表结构：portal_diary（情侣日记表）
-- -- 幂等建表：仅当表不存在时创建
-- -- ----------------------------
-- CREATE TABLE IF NOT EXISTS `portal_diary` (
--     `id`          BIGINT        NOT NULL                COMMENT '主键ID',
--     `user_id`     BIGINT        NOT NULL                COMMENT '记录人ID',
--     `diary_date`  DATE          NOT NULL                COMMENT '记录日期',
--     `mood`        VARCHAR(16)   NULL DEFAULT NULL       COMMENT '心情标识',
--     `content`     TEXT          NULL                    COMMENT '日记内容',
--     `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
--     `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
--     `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
--     `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
--     `del_flag`    TINYINT(1)    NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
--     PRIMARY KEY (`id`),
--     INDEX `idx_pd_user_date` (`user_id` ASC, `diary_date` ASC)
--     ) ENGINE = InnoDB
--     CHARACTER SET = utf8mb4
--     COLLATE = utf8mb4_general_ci
--     ROW_FORMAT = DYNAMIC
--     COMMENT = '情侣日记表';
--
-- -- ----------------------------
-- -- 表结构：portal_footprint（足迹地图表）
-- -- 幂等建表：仅当表不存在时创建
-- -- ----------------------------
-- CREATE TABLE IF NOT EXISTS `portal_footprint` (
--     `id`           BIGINT         NOT NULL                COMMENT '主键ID',
--     `city`         VARCHAR(128)   NOT NULL                COMMENT '城市/地点名称',
--     `longitude`    DECIMAL(10,6)  NULL DEFAULT NULL       COMMENT '经度',
--     `latitude`     DECIMAL(10,6)  NULL DEFAULT NULL       COMMENT '纬度',
--     `arrival_date` DATE           NULL DEFAULT NULL       COMMENT '到访日期',
--     `photo_id`     BIGINT         NULL DEFAULT NULL       COMMENT '关联照片ID（portal_photo.id）',
--     `remark`       VARCHAR(512)   NULL DEFAULT NULL       COMMENT '备注',
--     `creator`      BIGINT         NULL DEFAULT NULL       COMMENT '创建人',
--     `create_time`  DATETIME       NULL DEFAULT NULL       COMMENT '创建时间',
--     `updater`      BIGINT         NULL DEFAULT NULL       COMMENT '更新人',
--     `update_time`  DATETIME       NULL DEFAULT NULL       COMMENT '更新时间',
--     `del_flag`     TINYINT(1)     NOT NULL DEFAULT 0      COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
--     PRIMARY KEY (`id`),
--     INDEX `idx_pf_arrival_date` (`arrival_date` ASC)
--     ) ENGINE = InnoDB
--     CHARACTER SET = utf8mb4
--     COLLATE = utf8mb4_general_ci
--     ROW_FORMAT = DYNAMIC
--     COMMENT = '足迹地图表';
--
-- -- ----------------------------
-- -- 表结构：portal_visit（门户访问统计表）
-- -- 幂等建表：仅当表不存在时创建
-- -- 说明：系统生成的日粒度统计数据，无审计列与逻辑删除
-- -- ----------------------------
-- CREATE TABLE IF NOT EXISTS `portal_visit` (
--     `id`          BIGINT     NOT NULL                COMMENT '主键ID',
--     `stat_date`   DATE       NOT NULL                COMMENT '统计日期',
--     `pv`          INT        NOT NULL DEFAULT 0      COMMENT '当日访问量（PV）',
--     `uv`          INT        NOT NULL DEFAULT 0      COMMENT '当日独立访客数（UV）',
--     `create_time` DATETIME   NULL DEFAULT NULL       COMMENT '创建时间',
--     `update_time` DATETIME   NULL DEFAULT NULL       COMMENT '更新时间',
--     PRIMARY KEY (`id`),
--     INDEX `idx_pv_stat_date` (`stat_date` ASC)
--     ) ENGINE = InnoDB
--     CHARACTER SET = utf8mb4
--     COLLATE = utf8mb4_general_ci
--     ROW_FORMAT = DYNAMIC
--     COMMENT = '门户访问统计表';
-- =====================================================================================================================
