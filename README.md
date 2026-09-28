# Amour

`Amour`（法语中意为"爱"）——爱慕情侣小站后端服务，提供门户站点与管理后台全部接口；配套前端工程：[AmourWeb](https://github.com/codesensi/AmourWeb)。

版本演进与各版本变更内容见 [CHANGELOG.md](CHANGELOG.md)，版本号遵循语义化版本。

## 在线体验

演示站点：<https://amour-demo.codesensi.cn:1443/>（已开启演示模式，仅开放只读操作，密码均为 `123456`）

| 账号    | 角色       |
| ------- | ---------- |
| `admin` | 超级管理员 |
| `li`    | 男主       |
| `su`    | 女主       |

## 技术栈

- Java 21 + Spring Boot 4 + MyBatis-Flex
- H2 文件数据库（`MODE=MySQL`，启动时自动执行幂等初始化脚本）
- Sa-Token（JWT 简单模式）+ Caffeine 本地缓存

## 本地开发

环境要求：JDK 21（Gradle 版本由 Wrapper 自管，无需单独安装 Gradle）。

```bash
# 启动（默认 dev 环境，http://localhost:9666）
./gradlew bootRun        # Windows 为 gradlew.bat bootRun

# 编译 + 测试
./gradlew compileJava test
```

| 项        | 说明                                                                                                                              |
| --------- | --------------------------------------------------------------------------------------------------------------------------------- |
| 内置账号  | `admin`（超级管理员）/ `li`（男主）/ `su`（女主），密码均为 `123456`                                                              |
| H2 数据库 | 文件库位于 `./data/amour_dev`，首次启动自动建库；`init_ddl.sql` / `init_dml.sql` 每次启动执行且保持幂等（种子数据按唯一键防重入） |
| H2 控制台 | dev 环境开启：<http://localhost:9666/h2-console>（JDBC URL 与用户名见 `application-dev.yml`，密码为空）                           |
| 生产配置  | `application-prod.yml`（H2 密码、JWT 密钥等），容器部署时的环境变量覆盖见下文 Docker 部署                                         |

## 功能特性

### 门户站点（免登录，`/portal/**`）

- **情侣主页**：首屏男女主展示信息与一言文案（随机下发、失败降级）
- **纪念日**：重要日期的展示与倒计时
- **点点滴滴**：生活动态记录
- **情侣日记**：双人日记
- **恋爱画册**：照片墙（仅下发「显示」状态的照片）
- **恋爱清单**：心愿清单与完成状态
- **足迹地图**：到访城市记录，前端以高德地图渲染
- **留言簿**：访客留言上墙与提交（提交防滥用依赖 `@RateLimit` 按 IP 限流）
- **时间胶囊**：写给未来的信
- **公共配置 / 数据字典**：站点名称、备案文案、验证码开关等展示配置与枚举项下发（敏感配置服务端剔除）
- **访问统计**：访问量上报与累计查询

### 管理后台（登录态 + 权限码，`/sys/**`、`/admin/**`）

- **系统管理**：用户、角色（含角色数据权限分配）、路由菜单（RBAC 权限）、参数配置、数据字典、文件管理、登录与操作日志、通知中心（业务事件触发写入）、缓存管理
- **业务管理**：仪表盘数据聚合、纪念日、点点滴滴、情侣日记、恋爱画册、恋爱清单、足迹地图、留言簿、时间胶囊

### 基础能力

- **登录态**：Sa-Token JWT 简单模式 + 图形验证码
- **权限模型**：RBAC，接口按 `@SaCheckPermission` 权限码鉴权，未登录请求由拦截器统一拒绝
- **行级数据权限**：按「角色 × 业务模块」配置可见/可改范围档位（`all`-全部数据 / `self`-仅本人，如主角角色初始为「全可读 + 仅本人可改」），无配置按 `self` 兜底、超管角色判定层硬编码 `all/all`；判定结果回填到分页行的 `canEdit`，前端操作列按钮与状态开关联动禁用，后端 `assertEditable` 写保护兜底
- **接口限流**：`@RateLimit` 按 IP 限流，阈值由参数配置动态管理（高德代理、留言提交等免登录接口防滥用）
- **演示模式**：开关开启后拦截全部数据写操作，供演示环境只读体验
- **操作日志**：AOP 切面采集，专用线程池异步落库，绝不反压业务线程
- **文件管理**：上传/预览/下载，按业务类型限额，本地存储 `{bizType}/{yyyyMM}/{fileId}.{ext}` 组织
- **高德服务代理**：`/_AMapService` 转发高德 Web 服务并在服务端附加 jscode，避免前端暴露安全密钥
- **QQ 信息查询**：按 QQ 号查询头像与昵称，供留言、资料展示等免登录场景复用

## 项目架构

```
Amour/src/main/java/cn/codesensi/amour
├── AmourApplication.java   # 启动类
├── advice/                 # 统一响应包装（Result{success, code, msg, data, timestamp, traceId}）
├── aspect/                 # AOP 切面（操作日志、接口限流）
├── common/                 # 通用常量、异常定义（如 RbacConst 公开路径清单）
├── config/                 # 配置类（WebMvc 拦截链、线程池、缓存、Logbook 等）
├── controller/             # 控制层（薄壳，只做参数校验与响应组织）
│   ├── portal/             #   门户接口（免登录，/portal/**）
│   ├── system/             #   系统管理（/sys/**）
│   ├── admin/              #   业务管理（/admin/**）
│   └── *.java              #   登录/验证码/文件/QQ 信息/高德代理等根路径接口
├── filter/                 # Servlet 过滤器
├── handler/                # 全局异常处理
├── interceptor/            # 拦截器（演示模式拦截）
├── mapper/                 # MyBatis-Flex 数据访问层
├── model/
│   ├── entity/             # 数据库实体（雪花 ID 主键）
│   ├── request/            # 入参模型（jakarta validation 校验注解）
│   ├── response/           # 响应模型
│   ├── dto/                # 服务层数据传输对象
│   └── converter/          # MapStruct 实体转换器
├── security/               # 安全相关
└── service/                # 业务层（接口 + impl 实现，业务逻辑承载）
```

## 接口约定

- 统一响应 `Result{ success, code, msg, data, timestamp, traceId? }`
- 分页五字段：`records / pageNumber / pageSize / totalRow / totalPage`
- 雪花 ID 防精度丢失：所有标识类 `Long` 字段序列化为字符串，前端一律按 `string` 消费
- 时间格式：`LocalDateTime` 为 `yyyy-MM-dd HH:mm:ss`，`LocalDate` 为 `yyyy-MM-dd`
- 接口前缀：`/portal/**` 免登录；`/sys/**`、`/admin/**` 需登录并校验权限码；`/file/view/**`、`/qq-info`、`/_AMapService/**` 等免登录场景在拦截器单独放行

## 第三方服务

第三方接口的密钥统一在 **管理后台-参数配置**（sys_config `security` 分组）维护，修改即热更新，无需重启：

| 配置键               | 用途                                                          |
| -------------------- | ------------------------------------------------------------- |
| `security.amap-key`  | 高德 Web 端 JS API Key，前端地图 JS SDK 消费                  |
| `security.amap-code` | 高德安全密钥 jscode，仅服务端 `/_AMapService` 代理转发时附加  |
| `security.uapi-key`  | UApiPro 接口密钥，一言 / QQ 信息查询以 `X-API-KEY` 请求头携带 |

- `security.amap-code` 未配置时，`/_AMapService` 按高德错误语义返回「密钥未配置」
- `security.uapi-key` 未配置时，一言 / QQ 信息查询自动降级（官方头像拼接、空数据），不影响页面访问
- 接口地址与超时等非密钥项在 `application.yml` 的 `app` 段配置，改动需重启生效

## 分支与发版

- **分支模型**：单主干——`main` 为长期分支，功能以短生命周期分支开发，合入后删除
- **提交校验**：推送的提交信息遵循 Conventional Commits，由 CI（`Commit Lint`）自动校验
- **发版流程**：`CHANGELOG.md` 定稿版本段 → 在 `main` 上创建 `v*` 附注标签并推送 → Actions 自动创建 GitHub Release（正文为对应版本段落，tag 含 `-` 的预发布标识会自动标记为 pre-release）

## Docker 部署

镜像发布在 Docker Hub（`codesensi/amour`），部署命令：

```bash
docker run -dp 9666:9666 --name amour \
  --restart unless-stopped \
  -v /docker/amour/data:/app/data \
  -v /docker/amour/logs:/app/logs \
  -e SPRING_DATASOURCE_PASSWORD='H2密码' \
  -e SATOKEN_JWTSECRETKEY='JWT密钥' \
  codesensi/amour:latest
```

| 参数                              | 说明                                                                             |
| --------------------------------- | -------------------------------------------------------------------------------- |
| `-d`                              | 后台运行，容器在后台守护                                                         |
| `-p 9666:9666`                    | 端口映射（宿主机端口:容器端口）；9666 为服务端口，单体部署时浏览器直接访问该端口 |
| `--name amour`                    | 容器名，供 `docker ps`、`docker logs` 等后续操作引用                             |
| `--restart unless-stopped`        | 重启策略：容器崩溃或 Docker 守护进程重启后自动拉起，手动停止后不复活             |
| `-v /docker/amour/data:/app/data` | 持久化 H2 数据库与上传文件，不挂载则数据随容器删除而丢失                         |
| `-v /docker/amour/logs:/app/logs` | 持久化应用日志与 GC 日志                                                         |
| `-e SPRING_DATASOURCE_PASSWORD`   | H2 连接密码，覆盖内置默认值（生产务必修改；密码在首次建库时写入库文件）          |
| `-e SATOKEN_JWTSECRETKEY`         | JWT 签名密钥，覆盖内置默认值（生产务必修改；更换后所有已登录 token 失效）        |
| `codesensi/amour:latest`          | 镜像标签：`latest` 为最新版本，替换为具体版本号（如 `1.0.0`）可用于回滚          |

### 注意事项

- **H2 密码与库文件绑定**：密码在首次建库时写入 `.mv.db` 文件。挂载已用旧密码初始化过的数据卷时，`SPRING_DATASOURCE_PASSWORD` 必须与原库一致，否则连接失败（`Wrong user name or password`）；换密码需挂**全新数据卷**（首次启动按新密码初始化），或用旧密码连接后执行 `ALTER USER sa SET PASSWORD '新密码'`。
