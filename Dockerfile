FROM gradle:8.7-jdk21 AS builder
WORKDIR /home/gradle/src

# 1) Declarar args que vienen del workflow
ARG GITHUB_ACTOR
ARG GITHUB_TOKEN

# 2) Copiar código con permisos del user gradle
COPY --chown=gradle:gradle . .

# 3) Escribir credenciales para repos privados (GitHub Packages, etc.)
RUN mkdir -p /home/gradle/.gradle \
 && printf "gpr.user=%s\ngpr.key=%s\n" "$GITHUB_ACTOR" "$GITHUB_TOKEN" > /home/gradle/.gradle/gradle.properties

# (opcional pero útil para evitar problemas): desactiva watcher y daemon
ENV GRADLE_OPTS="-Dorg.gradle.vfs.watch=false -Dorg.gradle.daemon=false"

# 4) Build
RUN gradle clean bootJar -x test --no-daemon --stacktrace

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=builder --chown=app:app /home/gradle/src/build/libs/*.jar /app/app.jar
USER app
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["java","-jar","/app/app.jar"]