# ===== 构建阶段:JDK 21 + Gradle Wrapper =====
FROM eclipse-temurin:21-jdk-alpine AS build-stage

WORKDIR /app

# 先拷贝构建脚本与 Wrapper 单独拉取依赖,最大化利用层缓存(源码变更不触发依赖重解析)
COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle build.gradle ./
RUN chmod +x gradlew
RUN ./gradlew --no-daemon dependencies

COPY src ./src
RUN ./gradlew bootJar --no-daemon

# ===== 运行阶段:仅 JRE =====
FROM eclipse-temurin:21-jre-alpine AS production-stage

WORKDIR /app

# 时区统一北京时间(日志与业务时间戳一致)
ENV TZ=Asia/Shanghai
# 默认激活生产配置(application-prod.yml);本地联调时用 -e SPRING_PROFILES_ACTIVE=dev 覆盖
ENV SPRING_PROFILES_ACTIVE=prod
# JVM 基线参数:
# - 堆上限取容器内存 75%,其余留给 Metaspace/虚拟线程栈/H2 页缓存等堆外开销
# - OOM 自动转储 + GC 日志均落 /app/logs(日志卷),排障后可去掉 -Xlog 段
# 运行时用 -e JAVA_OPTS=... 可整体覆盖基线(ENV 覆盖是整体替换,不是追加)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs -Xlog:gc*:file=/app/logs/gc.log:time,uptime,tags:filecount=5,filesize=10m"

COPY --from=build-stage /app/build/libs/*.jar app.jar

# 创建非 root 专用用户(固定 UID 便于卷权限管理);挂载点目录先建好并赋属主,具名卷首次挂载自动继承属主;
# su-exec 供入口脚本修正挂载卷属主后降权运行 JVM
RUN addgroup -S amour && adduser -S -G amour -u 1001 amour \
    && mkdir -p /app/data /app/logs \
    && chown -R amour:amour /app \
    && apk add --no-cache su-exec

# H2 文件库(./data/amour_dev)与本地文件存储(./data/files)均落在 /app/data,挂卷即可持久化
VOLUME /app/data
# 应用日志(./logs/Amour_dev.log 及滚动归档),同样挂卷持久化
VOLUME /app/logs

EXPOSE 9666

# 健康检查:actuator 健康端点(真实探测数据源连接),状态 UP 即视为就绪
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s \
  CMD wget -qO- http://127.0.0.1:9666/actuator/health | grep -q '"status":"UP"' || exit 1

# 容器以 root 启动,先修正挂载卷属主(bind-mount 宿主目录属主不可预知),
# 再经 su-exec 降权为 amour 运行 JVM;exec 保证 java 直接接收容器信号,配合优雅停机(shutdown: graceful)
ENTRYPOINT ["sh", "-c", "chown -R amour:amour /app/data /app/logs && exec su-exec amour:amour java $JAVA_OPTS -jar app.jar"]
