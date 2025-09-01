# ---- Stage 1: Build JAR ----
FROM maven:3.9-amazoncorretto-21-debian AS build
WORKDIR /app
COPY . .
RUN mvn -B -DskipTests clean package spring-boot:repackage

# ---- Stage 2: Build Native Executable ----
FROM ghcr.io/graalvm/graalvm-community:21.0.2 AS native-builder
WORKDIR /app

# Copy fat JAR
COPY --from=build /app/target/*.jar app.jar

# Install native-image tool
RUN gu install native-image

# Build native executable (no fallback, static binary)
RUN native-image \
    --no-fallback \
    --enable-url-protocols=http,https \
    --install-exit-handlers \
    -jar app.jar app

# ---- Stage 3: Minimal Runtime ----
FROM debian:bookworm-slim AS runtime
WORKDIR /app
COPY --from=native-builder /app/app .
EXPOSE 8080
ENTRYPOINT ["./app"]
