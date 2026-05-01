# Build the Spring Boot application with Gradle.
FROM docker.arvancloud.ir/gradle:8.7-jdk21 AS build

WORKDIR /workspace

COPY --chown=gradle:gradle settings.gradle.kts build.gradle.kts ./
COPY --chown=gradle:gradle gradle gradle
COPY --chown=gradle:gradle src src

RUN gradle bootJar --no-daemon -x test --info

# Runtime image with only the JRE and the packaged application.
FROM docker.arvancloud.ir/eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/build/libs/*.jar app.jar
COPY src/main/resources/application.properties /app/config/application.properties

ENV SPRING_CONFIG_LOCATION=classpath:/application.properties,/app/config/application.properties

ENTRYPOINT ["java", "-jar", "app.jar"]
