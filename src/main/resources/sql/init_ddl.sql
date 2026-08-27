-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
-- 幂等建表：仅当表不存在时创建，已存在则跳过（不重建、不丢数据）
CREATE TABLE IF NOT EXISTS `sys_user`
(
    `id`          bigint       NOT NULL COMMENT '用户ID',
    `username`    varchar(128) NOT NULL COMMENT '用户名称',
    `password`    varchar(512) NOT NULL COMMENT '用户密码',
    `nickname`    varchar(64) NULL DEFAULT NULL COMMENT '用户昵称',
    `id_card`     varchar(64) NULL DEFAULT NULL COMMENT '用户身份证号码',
    `email`       varchar(64) NULL DEFAULT NULL COMMENT '用户邮箱',
    `phone`       varchar(11) NULL DEFAULT NULL COMMENT '用户手机号码',
    `gender`      varchar(1) NULL DEFAULT 'U' COMMENT '用户性别:U-未知,M-男,F-女',
    `avatar`      varchar(512) NULL DEFAULT NULL COMMENT '用户头像地址',
    `status`      tinyint(1)   NULL DEFAULT 0 COMMENT '用户状态:0-启用,1-禁用',
    `remark`      varchar(512) NULL DEFAULT NULL COMMENT '备注',
    `creator`     bigint NULL DEFAULT NULL COMMENT '创建人',
    `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     bigint NULL DEFAULT NULL COMMENT '更新人',
    `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`    tinyint(1)    NULL DEFAULT 0 COMMENT '逻辑删除标识:0-未删除,1-已删除',
    PRIMARY KEY (`id`),
    INDEX         `idx_u_username` (`username` ASC),
    INDEX         `idx_u_status` (`status` ASC)
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '用户信息表'
  ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
-- 系统配置表：存储 app.* 业务可调配置，运行期由 ConfigService 实时查库读取
-- 幂等建表：仅当表不存在时创建，已存在则跳过（不重建、不丢数据）
CREATE TABLE IF NOT EXISTS `sys_config`
(
    `id`           bigint       NOT NULL COMMENT '主键ID',
    `config_key`   varchar(128) NOT NULL COMMENT '配置键（app 之下的点分路径，如 captcha.sms-expire）',
    `config_value` varchar(4000) NULL DEFAULT NULL COMMENT '配置值（统一字符串存储）',
    `value_type`   varchar(16)  NOT NULL DEFAULT 'STRING' COMMENT '值类型:STRING,INTEGER,LONG,BOOLEAN',
    `config_group` varchar(64) NULL DEFAULT NULL COMMENT '分组（app 的一级子项，如 name、captcha 等）',
    `is_active`    tinyint(1)   NULL DEFAULT 1 COMMENT '是否启用:1-启用,0-停用',
    `remark`       varchar(512) NULL DEFAULT NULL COMMENT '备注',
    `creator`      bigint NULL DEFAULT NULL COMMENT '创建人',
    `create_time`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`      bigint NULL DEFAULT NULL COMMENT '更新人',
    `update_time`  datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag`     tinyint(1)  NULL DEFAULT 0 COMMENT '逻辑删除标识:0-未删除,1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_cfg_key` (`config_key` ASC)
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '系统配置表（app.* 配置落库）'
  ROW_FORMAT = DYNAMIC;
