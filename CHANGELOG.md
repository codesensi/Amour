# 更新日志

本项目的所有显著变更都记录在本文件中。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.1.0/)，版本号遵循[语义化版本](https://semver.org/lang/zh-CN/)，提交信息遵循 [Conventional Commits](https://www.conventionalcommits.org/zh-hans/)。

## [Unreleased]

<!-- 未发布的变更记录在此段，发布时改为 [x.y.z] - 日期 -->

## [1.1.3] - 2026-09-28

### 新增

- Release 自动化：新增 GitHub Actions 工作流，推送 `v*` 标签后自动从本文件提取对应版本段落创建 GitHub Release

### 变更

- README 完善：补充本地开发指引（内置账号、H2 数据库、初始化脚本）、行级数据权限说明与 Docker 部署参数说明（含敏感配置环境变量覆盖）
- 工程版本号由 `0.0.1-SNAPSHOT` 正式化，当前为 `1.1.3`

### 移除

- 清理重构后遗留的未使用源文件（旧工具类、控制器与模型等）

## [1.1.2] - 2026-09-28

### 新增

- 行级数据权限：按「角色 × 业务模块」配置可见/可改范围档位（`all`-全部数据 / `self`-仅本人），判定层含超管硬编码 `all/all` 短路与 `self` 兜底
- 角色数据权限分配接口（`GET /sys/role/data-scope/{id}`、`PUT /sys/role/assign-data-scope`）与管理端分配抽屉（权限码 `system:role:scope`）
- 业务模块分页行回填 `canEdit`，前端操作列按钮与状态开关联动门控，后端 `assertEditable` 写保护兜底
- 主角角色初始数据权限种子：全部模块「`all` 可读 + `self` 可改」

### 变更

- 门户展示类种子数据（照片/足迹/纪念日/清单/时间胶囊）创建人归属修正为两位主角用户交替，不再统一归属 admin
- 情侣日记种子数据创建人与记录人业务归属对齐

### 修复

- 管理端分页接口 `canEdit` 未透出至 Response 导致操作按钮门控失效的问题
- 前端 mock 分页统一注入 `canEdit`，与后端回填契约对齐

## [1.0.0] - 2026-09-24

### 新增

- **门户站点**：情侣主页、纪念日、点点滴滴、情侣日记、恋爱画册、恋爱清单、足迹地图（高德地图渲染）、留言簿（IP 限流防滥用）、时间胶囊、公共配置/数据字典下发、访问统计
- **管理后台**：用户、角色、路由菜单（RBAC 权限）、参数配置、数据字典、文件管理、登录与操作日志、通知中心、缓存管理，以及各业务模块内容管理与仪表盘
- **基础能力**：Sa-Token JWT 简单模式登录态 + 图形验证码、`@SaCheckPermission` 权限码鉴权、`@RateLimit` 接口限流、演示模式（只读拦截）、AOP 操作日志异步落库、文件上传/预览/下载（按业务类型限额）、高德服务代理（服务端附加 jscode）、QQ 信息查询
- **数据存储**：H2 文件数据库（`MODE=MySQL`），`init_ddl.sql` / `init_dml.sql` 启动幂等初始化
- **部署**：Dockerfile（非 root 运行、优雅停机、Actuator 健康检查、挂载卷持久化），H2 密码与 JWT 密钥支持环境变量覆盖

[Unreleased]: https://github.com/codesensi/Amour/compare/v1.1.3...HEAD
[1.1.3]: https://github.com/codesensi/Amour/compare/v1.1.2...v1.1.3
[1.1.2]: https://github.com/codesensi/Amour/compare/v1.0.0...v1.1.2
[1.0.0]: https://github.com/codesensi/Amour/releases/tag/v1.0.0
