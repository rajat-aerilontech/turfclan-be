#!/bin/bash

# Move to project root (important)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
cd "$PROJECT_ROOT" || exit

echo "Running from: $(pwd)"

# Environment variables
export SPRING_PROFILES_ACTIVE=
export APP_COPYRIGHT=""
export ALLOWED_ORIGIN=""
export LOG_LEVEL=INFO
export SPRINGFRAMEWORK_WEB_LOG_LEVEL=

export AWS_RDS_URL=""
export RDS_SCHEMA=
export RDS_USERNAME=
export RDS_PASSWORD=""

export AWS_ACCESS_KEY=""
export AWS_SECRET_KEY=""

export RECAPTCHA_SECRET_KEY=""
export RECAPTCHA_THRESHOLD_SCORE=

export EMAIL_SERVICE_HOST=
export EMAIL_SERVICE_PORT=
export EMAIL_SERVICE_USERNAME=""
export EMAIL_SERVICE_PASSWORD=""

# Start Redis container
echo "Starting Redis container..."
docker start turfclan-redis >/dev/null 2>&1 || docker run --name turfclan-redis -p 6379:6379 -d redis:latest

# Run Gradle
./gradlew clean
./gradlew bootRun