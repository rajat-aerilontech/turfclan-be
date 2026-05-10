# Multi-stage Docker build for the Turfclan Spring Boot application
FROM eclipse-temurin:22-jdk-jammy AS builder
WORKDIR /workspace

COPY . .

RUN chmod +x ./gradlew && ./gradlew :application:bootJar --no-daemon

FROM eclipse-temurin:22-jre-jammy
WORKDIR /app

# --- Install PostGIS/GDAL dependencies ---
RUN apt-get update && apt-get install -y \
    postgresql-client \
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
