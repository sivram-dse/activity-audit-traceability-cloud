# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Create non-root user for better security (Cloud Run supports this)
RUN useradd -u 10001 -m appuser
USER 10001

COPY --from=build /app/target/*.jar app.jar

# Cloud Run sets PORT (default 8080). Spring will bind to it via server.port=${PORT:8080}
EXPOSE 8080

# Conservative JVM flags for container environments
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=20 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["java","-jar","/app/app.jar"]
