FROM gradle:8.14.0-jdk21 AS builder
WORKDIR /workspace
COPY . .

# Build without leaking secrets into layers
RUN --mount=type=secret,id=gpr_user \
    --mount=type=secret,id=gpr_key \
    gradle clean bootJar -x test --no-daemon --stacktrace \
      -Pgpr.user=$(cat /run/secrets/gpr_user) \
      -Pgpr.key=$(cat /run/secrets/gpr_key)

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]