-- ============================================
-- 表结构：sys_user（用户信息表）
-- 幂等建表：仅当表不存在时创建
-- ============================================
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`          BIGINT       NOT NULL                COMMENT '用户ID',
    `username`    VARCHAR(128) NOT NULL                COMMENT '用户名称',
    `password`    VARCHAR(512) NOT NULL                COMMENT '用户密码',
    `nickname`    VARCHAR(64)  NULL DEFAULT NULL       COMMENT '用户昵称',
    `id_card`     VARCHAR(64)  NULL DEFAULT NULL       COMMENT '用户身份证号码',
    `email`       VARCHAR(64)  NULL DEFAULT NULL       COMMENT '用户邮箱',
    `phone`       VARCHAR(11)  NULL DEFAULT NULL       COMMENT '用户手机号码',
    `gender`      VARCHAR(1)   NULL DEFAULT 'U'        COMMENT '用户性别: U-未知, M-男, F-女',
    `avatar`      VARCHAR(512) NULL DEFAULT NULL       COMMENT '用户头像地址',
    `status`      TINYINT(1)   NULL DEFAULT 0          COMMENT '用户状态: 0-启用, 1-禁用',
    `remark`      VARCHAR(512) NULL DEFAULT NULL       COMMENT '备注',
    `creator`     BIGINT       NULL DEFAULT NULL       COMMENT '创建人',
    `create_time` DATETIME     NULL DEFAULT NULL       COMMENT '创建时间',
    `updater`     BIGINT       NULL DEFAULT NULL       COMMENT '更新人',
    `update_time` DATETIME     NULL DEFAULT NULL       COMMENT '更新时间',
    `del_flag`    TINYINT(1)   NULL DEFAULT 0          COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    INDEX `idx_su_username` (`username` ASC),
    INDEX `idx_su_status`   (`status` ASC)
    ) ENGINE = InnoDB
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_general_ci
    COMMENT = '用户信息表'
    ROW_FORMAT = DYNAMIC;

-- ============================================
-- 表结构：sys_config（系统配置表）
-- 幂等建表：仅当表不存在时创建
-- ============================================
CREATE TABLE IF NOT EXISTS `sys_config` (
    `id`          BIGINT        NOT NULL                COMMENT '主键ID',
    `c_key`       VARCHAR(128)  NOT NULL                COMMENT '配置键',
    `c_value`     VARCHAR(4000) NULL DEFAULT NULL       COMMENT '配置值',
    `v_type`      VARCHAR(16)   NULL DEFAULT NULL       COMMENT '值类型: STRING, INTEGER, LONG, BOOLEAN',
    `c_group`     VARCHAR(64)   NULL DEFAULT NULL       COMMENT '分组',
    `status`      TINYINT(1)    NULL DEFAULT 0          COMMENT '配置状态: 0-启用, 1-禁用',
    `remark`      VARCHAR(512)  NULL DEFAULT NULL       COMMENT '备注',
    `creator`     BIGINT        NULL DEFAULT NULL       COMMENT '创建人',
    `create_time` DATETIME      NULL DEFAULT NULL       COMMENT '创建时间',
    `updater`     BIGINT        NULL DEFAULT NULL       COMMENT '更新人',
    `update_time` DATETIME      NULL DEFAULT NULL       COMMENT '更新时间',
    `del_flag`    TINYINT(1)    NULL DEFAULT 0          COMMENT '逻辑删除标识: 0-未删除, 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `idx_sc_c_key` (`c_key` ASC)
    ) ENGINE = InnoDB
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_general_ci
    COMMENT = '系统配置表'
    ROW_FORMAT = DYNAMIC;