-- ----------------------------
-- 表结构：sys_config（系统配置表）
-- 幂等建表：仅当表不存在时创建
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_config` (
    `id`           BIGINT        NOT NULL                COMMENT '主键ID',
    `config_key`   VARCHAR(128)  NOT NULL                COMMENT '配置键',
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
    INDEX `idx_config_key` (`config_key` ASC)
    ) ENGINE = InnoDB
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_general_ci
    COMMENT = '系统配置表'
    ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 表结构：sys_user（用户信息表）
-- 幂等建表：仅当表不存在时创建
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`          BIGINT        NOT NULL                COMMENT '用户ID',
    `username`    VARCHAR(128)  NOT NULL                COMMENT '用户名称',
    `password`    VARCHAR(512)  NOT NULL                COMMENT '用户密码',
    `nickname`    VARCHAR(64)   NULL DEFAULT NULL       COMMENT '用户昵称',
    `id_card`     VARCHAR(64)   NULL DEFAULT NULL       COMMENT '用户身份证号码',
    `email`       VARCHAR(64)   NULL DEFAULT NULL       COMMENT '用户邮箱',
    `phone`       VARCHAR(11)   NULL DEFAULT NULL       COMMENT '用户手机号码',
    `gender`      VARCHAR(1)    NOT NULL DEFAULT 'U'    COMMENT '用户性别: U-未知, M-男, F-女',
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
    COMMENT = '用户信息表'
    ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 表结构：sys_role（角色信息表）
-- 幂等建表：仅当表不存在时创建
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id`          BIGINT        NOT NULL                COMMENT '角色ID',
    `name`        VARCHAR(64)   NOT NULL                COMMENT '角色名称',
    `code`        VARCHAR(64)   NOT NULL                COMMENT '角色编码',
    `sort`        INT           NOT NULL DEFAULT 0      COMMENT '角色排序',
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
    COMMENT = '角色信息表'
    ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 表结构：sys_user_role（用户角色关联表）
-- 幂等建表：仅当表不存在时创建
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id`          BIGINT        NOT NULL                COMMENT '主键ID',
    `user_id`     BIGINT        NOT NULL                COMMENT '用户ID',
    `role_id`     BIGINT        NOT NULL                COMMENT '角色ID',
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
    COMMENT = '用户角色关联表'
    ROW_FORMAT = DYNAMIC;

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
    `sort`        INT           NOT NULL DEFAULT 0      COMMENT '菜单排序: 数字越小越靠前',
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
    COMMENT = '菜单表'
    ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 表结构：sys_role_menu（角色菜单关联表）
-- 幂等建表：仅当表不存在时创建
-- ----------------------------
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `id`          BIGINT        NOT NULL                COMMENT '主键ID',
    `role_id`     BIGINT        NOT NULL                COMMENT '角色ID',
    `menu_id`     BIGINT        NOT NULL                COMMENT '菜单ID',
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
    COMMENT = '角色菜单关联表'
    ROW_FORMAT = DYNAMIC;