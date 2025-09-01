# syntax=docker/dockerfile:1.6

############################################
# 1) Build native binary with GraalVM + Maven
############################################
FROM ghcr.io/graalvm/native-image-community:21 AS builder
# The GraalVM community images are based on Oracle Linux. Use microdnf to install Maven + git (for deps).
RUN microdnf -y install maven git findutils tar gzip && microdnf clean all

WORKDIR /workspace

# Optional: speed up Maven by caching the local repo between builds
# (Docker BuildKit is required for --mount=type=cache)
# Also pre-copy just the pom to warm dependency cache.
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -e -DskipTests -Pnative package

# Now copy sources
COPY src ./src

# Build native image (AOT). Spring Boot 3.x + native plugin supports `-Pnative`.
# compile-no-fork avoids spawning a new JVM (faster, fewer surprises).
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -e -DskipTests -Pnative org.graalvm.buildtools:native-maven-plugin:compile-no-fork

# If you prefer the usual package goal (also triggers native via profile), use:
# RUN --mount=type=cache,target=/root/.m2 mvn -q -e -DskipTests -Pnative package

# Figure out the produced binary path.
# By default: target/<artifactId>
# With your pom it should be: target/jaamebaade
# We’ll just verify and set an ARG we can reuse.
ARG BIN=target/jaamebaade
RUN test -f "${BIN}"

############################################
# 2) Minimal runtime image (glibc)
############################################
# Distroless cc provides glibc and basic runtime libs, perfect for Graal native images.
FROM gcr.io/distroless/cc-debian12:nonroot

# Copy the native binary
COPY --from=builder /workspace/target/jaamebaade /app/jaamebaade

# Expose Spring’s default port (change if configured otherwise)
EXPOSE 8080

# Environment you might need (tweak as you like)
# Example: enable Netty native resolver if you use it, set TZ, etc.
ENV TZ=UTC \
    JAVA_TOOL_OPTIONS=""

# Run as nonroot (distroless user already exists)
USER nonroot:nonroot

# For graceful shutdown signals
ENTRYPOINT ["/app/jaamebaade"]
