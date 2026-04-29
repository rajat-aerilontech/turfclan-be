@echo off

set SPRING_PROFILES_ACTIVE=local
set APP_COPYRIGHT=2026 Aerilon Tech LLP Solutions, Inc. All rights reserved.
set ALLOWED_ORIGIN=*
set LOG_LEVEL=INFO
set SPRINGFRAMEWORK_WEB_LOG_LEVEL=ERROR
set AWS_RDS_URL=jdbc:postgresql://localhost:5432/turfclan?serverTimezone=Asia/Kolkata&useLegacyDatetimeCode=false
set RDS_SCHEMA=turfclan_schema
set RDS_USERNAME=postgres
set RDS_PASSWORD=Admin@123

call .\gradlew.bat clean
call .\gradlew.bat bootRun --debug-jvm