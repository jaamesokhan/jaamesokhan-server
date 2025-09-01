FROM ghcr.io/graalvm/graalvm-community:21.0.2 AS native-maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*
RUN gu install native-image
WORKDIR /app
COPY . .
# This triggers the native plugin because of -Pnative
RUN mvn -B -DskipTests -Pnative clean package

FROM debian:bookworm-slim
WORKDIR /app
# The native binary name is usually your artifactId (e.g., jaamebaade)
COPY --from=native-maven /app/target/jaamebaade /app/app
EXPOSE 8080
ENTRYPOINT ["./app"]
