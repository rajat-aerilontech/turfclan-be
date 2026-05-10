@echo off

set SPRING_PROFILES_ACTIVE=local
set APP_COPYRIGHT=2026 Turfclan, Inc. All rights reserved.
set ALLOWED_ORIGIN=*
set LOG_LEVEL=INFO
set SPRINGFRAMEWORK_WEB_LOG_LEVEL=ERROR
set AWS_RDS_URL=
set RDS_SCHEMA=turfclan_schema
set RDS_USERNAME=postgres
set RDS_PASSWORD=Admin@123
set AWS_ACCESS_KEY=
set AWS_SECRET_KEY=
set RECAPTCHA_SECRET_KEY=
set RECAPTCHA_THRESHOLD_SCORE=0.5

call .\gradlew.bat clean
call .\gradlew.bat bootRun --debug-jvm