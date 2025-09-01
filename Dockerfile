# Start with a base image containing Java runtime and Maven
FROM maven:3.9-amazoncorretto-21-debian AS build

# Make source folder
RUN mkdir -p /workspace

WORKDIR /workspace

# Copy the pom.xml file and download dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline


# Copy the source code
COPY src src

# Builds the application and stores it in /target
RUN mvn package -DskipTests

# A new stage so that we won't need maven in the final image
FROM openjdk:21

# Make application folder
RUN mkdir -p /app

WORKDIR /app

# Copy the jar file from the previous stage
COPY --from=build /workspace/target/*.jar app.jar

# Add the application.properties file to the container
COPY ./application.properties /app/config/application.properties

# Set environment variables to define the location of application.properties
ENV SPRING_CONFIG_LOCATION=classpath:/application.properties,/app/config/application.properties


# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]
