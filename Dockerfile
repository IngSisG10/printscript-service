FROM gradle:8.14.0-jdk21 AS builder
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
ENV GRADLE_OPTS="-Dorg.gradle.vfs.watch=false -Dorg.gradle.daemon=false"

RUN --mount=type=secret,id=gpr_user \
    --mount=type=secret,id=gpr_key \
    test -s /run/secrets/gpr_user && test -s /run/secrets/gpr_key && \
    gradle clean bootJar -x test --no-daemon --stacktrace \
      -Pgpr.user="$(cat /run/secrets/gpr_user)" \
      -Pgpr.key="$(cat /run/secrets/gpr_key)"

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=builder --chown=app:app /home/gradle/src/build/libs/*.jar /app/app.jar
USER app
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["java","-jar","/app/app.jar"]