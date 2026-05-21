@echo off

set SPRING_PROFILES_ACTIVE=local
set APP_COPYRIGHT=2026 Turfclan, Inc. All rights reserved.
set ALLOWED_ORIGIN=*
set LOG_LEVEL=INFO
set SPRINGFRAMEWORK_WEB_LOG_LEVEL=ERROR
set AWS_RDS_URL=jdbc:postgresql://localhost:5432/turfclan?serverTimezone=Asia/Kolkata
set RDS_USERNAME=postgres
set RDS_PASSWORD=Admin@123
set AWS_ACCESS_KEY=
set AWS_SECRET_KEY=
set S3_BUCKET_NAME=
set AWS_REGION=
set RECAPTCHA_SECRET_KEY=
set RECAPTCHA_THRESHOLD_SCORE=0.5
set EMAIL_SERVICE_HOST=smtp.gmail.com
set EMAIL_SERVICE_PORT=587
set EMAIL_SERVICE_USERNAME=
set EMAIL_SERVICE_PASSWORD=
set EMAIL_FROM_ADDRESS=

:: Start Redis container
echo Starting Redis container...
docker start turfclan-redis >nul 2>&1
if errorlevel 1 (
    echo Redis container not found or not running. Creating a new one...
    docker run --name turfclan-redis -p 6379:6379 -d redis:latest
)

call .\gradlew.bat clean
call .\gradlew.bat bootRun