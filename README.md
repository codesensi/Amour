# Amour

`Amour`（法语中意为"爱"）——爱慕情侣小站后端服务，提供门户站点与管理后台全部接口；配套前端工程：[AmourWeb](https://github.com/codesensi/AmourWeb)。

## 技术栈

- Java 21 + Spring Boot 4 + MyBatis-Flex
- H2 文件数据库（`MODE=MySQL`，启动时自动执行幂等初始化脚本）
- Sa-Token（JWT 简单模式）+ Caffeine 本地缓存

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

- **系统管理**：用户、角色、路由菜单（RBAC 权限）、参数配置、数据字典、文件管理、登录与操作日志、通知中心（业务事件触发写入）、缓存管理
- **业务管理**：仪表盘数据聚合、纪念日、点点滴滴、情侣日记、恋爱画册、恋爱清单、足迹地图、留言簿、时间胶囊

### 基础能力

- **登录态**：Sa-Token JWT 简单模式 + 图形验证码
- **权限模型**：RBAC，接口按 `@SaCheckPermission` 权限码鉴权，未登录请求由拦截器统一拒绝
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

| 配置键                | 用途                                                          |
| --------------------- | -------------------------------------------------------------- |
| `security.amap-key`   | 高德 Web 端 JS API Key，前端地图 JS SDK 消费                  |
| `security.amap-code`  | 高德安全密钥 jscode，仅服务端 `/_AMapService` 代理转发时附加  |
| `security.uapi-key`   | UApiPro 接口密钥，一言 / QQ 信息查询以 `X-API-KEY` 请求头携带 |

- `security.amap-code` 未配置时，`/_AMapService` 按高德错误语义返回「密钥未配置」
- `security.uapi-key` 未配置时，一言 / QQ 信息查询自动降级（官方头像拼接、空数据），不影响页面访问
- 接口地址与超时等非密钥项在 `application.yml` 的 `app` 段配置，改动需重启生效

## 快速开始

```bash
# 本地启动（http://localhost:9666，H2 控制台 http://localhost:9666/h2-console）
gradlew.bat bootRun

# 编译与测试
gradlew.bat compileJava test

# 打包（build/libs/Amour-*.jar）
gradlew.bat bootJar
```

## Docker 部署

镜像发布在 Docker Hub（`codesensi/amour`），提供 `latest` 与具体版本号双标签，部署机无需构建，直接 `docker run` 即可。支持两种部署形态：

### 分离部署（前后端独立容器）

后端只提供接口，页面由前端工程 [AmourWeb](https://github.com/codesensi/AmourWeb) 的 nginx 容器承载。

1. 构建并推送后端镜像（末尾的 `.` 表示使用当前路径下的 `Dockerfile`，可根据实际情况指定路径；推送前需 `docker login`；每次发布替换版本号标签）

```bash
docker build -t codesensi/amour:latest -t codesensi/amour:1.0.0 .
docker push codesensi/amour:latest
docker push codesensi/amour:1.0.0
```

2. 启动后端容器（`9666` 为服务端口；`/app/data` 持久化 H2 数据库与上传文件，`/app/logs` 持久化应用日志）

```bash
docker run -dp 9666:9666 --name amour \
  --restart unless-stopped \
  -v /docker/amour/data:/app/data -v /docker/amour/logs:/app/logs \
  codesensi/amour:latest
```

3. 启动前端容器：使用配套前端工程的镜像，与后端加入同一 `docker` 网络，以 `-e BACKEND_ORIGIN=http://amour:9666` 启动即可（详见前端工程 README）

### 单体部署（前后端一体）

把前端构建产物并入后端镜像，单容器同时承载页面与接口，无需前端容器与 `BACKEND_ORIGIN` 反代。

1. 前端构建：在配套前端工程执行 `pnpm build`，将产物 `dist/` 拷贝至本工程 `src/main/resources/static/`
2. 构建并推送后端镜像（命令同上，dist 随 jar 打包）
3. 启动容器（命令同分离部署）
4. 浏览器直接访问 `http://<主机>:9666`

说明：前后端同源请求，无需 `BACKEND_ORIGIN` 与反代；前端为 hash 路由，刷新与深链接无需 SPA 兜底配置；静态资源不参与登录拦截。

更新版本：重新构建并推送镜像后，删除旧容器并以同名重新 `docker run`。

回滚版本：将启动命令中的镜像标签由 `latest` 替换为对应历史版本号（如 `codesensi/amour:1.0.0`）重新创建容器即可。

说明：

- 容器内置 `HEALTHCHECK`（actuator `/actuator/health` 自检,真实探测数据源连通性），`docker ps` 可见健康状态；应用启动含建表与数据初始化，`--start-period` 已放宽到 60s
- 镜像默认激活 `prod` profile（`SPRING_PROFILES_ACTIVE=prod`），本地联调其他环境时用 `-e SPRING_PROFILES_ACTIVE=dev` 覆盖
- 数据库密码与 JWT 秘钥可用环境变量覆盖 `application-prod.yml` 默认值：`-e SPRING_DATASOURCE_PASSWORD=...`、`-e SA_TOKEN_JWTSECRETKEY=...`（注意后者为无下划线的 Spring Boot relaxed binding 写法，环境变量优先级高于 yml）
- `TZ` 默认 `Asia/Shanghai`；镜像内置 JVM 基线参数 `JAVA_OPTS`（堆取容器内存 75%、OOM 自动转储、GC 日志均落 `/app/logs`），需要调整时用 `-e JAVA_OPTS=...` 整体覆盖
- 以非 root 用户（`amour`，UID 1001）运行 JVM；容器以 root 启动并自动把挂载卷属主修正为 1001，（rootless Docker 下容器内 chown 无效，仍需在宿主机手动调整）
- H2 文件库与上传文件统一落在容器 `/app/data`，日志落在 `/app/logs`，均已声明挂载点；不挂卷则数据随容器删除而丢失
