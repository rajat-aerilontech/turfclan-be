@echo off
setlocal

if "%~1"=="" (
    echo Usage: %0 ^<dockerhub_username^>
    echo Example: %0 mydockerusername
    exit /b 1
)

set DOCKERHUB_USERNAME=%~1
set IMAGE_NAME=turfclan-be

echo Resolving project version...
for /f %%i in ('.\gradlew -q printVersion') do set IMAGE_TAG=%%i
echo Using version: %IMAGE_TAG%

echo Building Docker image %IMAGE_NAME%...
docker build -t %IMAGE_NAME% .
if %ERRORLEVEL% neq 0 (
    echo Docker build failed.
    exit /b %ERRORLEVEL%
)

echo.
echo Tagging image...
docker tag %IMAGE_NAME% %DOCKERHUB_USERNAME%/%IMAGE_NAME%:%IMAGE_TAG%
docker tag %IMAGE_NAME% %DOCKERHUB_USERNAME%/%IMAGE_NAME%:latest

echo.
echo Pushing image to Docker Hub...
docker push %DOCKERHUB_USERNAME%/%IMAGE_NAME%:%IMAGE_TAG%
docker push %DOCKERHUB_USERNAME%/%IMAGE_NAME%:latest
if %ERRORLEVEL% neq 0 (
    echo Docker push failed. Make sure you are logged in using 'docker login'.
    exit /b %ERRORLEVEL%
)

echo.
echo Successfully built and pushed %DOCKERHUB_USERNAME%/%IMAGE_NAME%:%IMAGE_TAG% and latest
endlocal
