# Multi-stage Docker build for the Turfclan Spring Boot application
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /workspace

COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle build.gradle ./
COPY application ./application
COPY common ./common
COPY user-module ./user-module
COPY booking-module ./booking-module
COPY ground-module ./ground-module
COPY community-module ./community-module
COPY chat-module ./chat-module
COPY tournament-module ./tournament-module
COPY notification-module ./notification-module
COPY sports-directory-module ./sports-directory-module

RUN chmod +x ./gradlew && ./gradlew :application:bootJar --no-daemon

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# --- Install PostGIS/GDAL dependencies ---
RUN apt-get update && apt-get install -y \
    postgis \
    gdal-bin \
    libgdal-dev \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /workspace/application/build/libs/*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=local
ENV SERVER_PORT=8434
ENV JAVA_OPTS=""

EXPOSE 8434

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
