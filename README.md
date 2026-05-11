# Turfclan Backend

Backend service for the Turfclan platform built with **Spring Boot**, **Gradle**, and **PostgreSQL**.

## Tech Stack

- **Spring Boot:** `3.3.0`
- **Java:** `22`
- **Gradle:** `8.8.0`
- **PostgreSQL JDBC Driver:** `42.7.7`

## Prerequisites

- Java 22
- Docker Desktop
- PostgreSQL

## Run Locally

The project uses Spring profiles for environment-specific configuration.

- Default profile: `local`
- Other supported profiles: `dev`, `preprod`, `prod`

Start the app locally:

```powershell
.\gradlew.bat clean
.\gradlew.bat bootRun
```

Swagger UI:

```text
http://localhost:8434/swagger-ui.html
```

## Docker Setup

### Build the image

```powershell
docker build -t turfclan-be .
```

### Run the container

```powershell
docker run --name turfclan-be --rm -p 8434:8434 -e SPRING_PROFILES_ACTIVE=local -e SERVER_PORT=8434 -e LOG_LEVEL=INFO -e SPRINGFRAMEWORK_WEB_LOG_LEVEL=ERROR -e AWS_RDS_URL="jdbc:postgresql://host.docker.internal:5432/turfclan" -e RDS_SCHEMA="<schema_name>" -e RDS_USERNAME="<db_user>" -e RDS_PASSWORD="<db_password>3" turfclan-be
```

### Push the image (Optional)

If you need to push the built image to a Docker registry like Docker Hub, use the provided script.
Make sure you are logged in via `docker login` first.

```powershell
.\scripts\docker-push.bat <your_dockerhub_username>
```

> Replace the placeholder database values with your local setup. Do not commit real credentials or secrets.

## Notes

- Use `SPRING_PROFILES_ACTIVE=local` for local development.
- When running in Docker, use `host.docker.internal` to connect to services running on your machine.
