@echo off
setlocal enabledelayedexpansion

echo ========================================================
echo   Smart Recommendation Service Management Platform     
echo                  Build Script                           
echo ========================================================

if not exist "out" mkdir out

echo [1/2] Compiling Java source files...
javac -encoding UTF-8 -d out -sourcepath src src/com/recommendation/platform/Main.java
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)
echo [OK] Compilation successful.

echo [2/2] Packaging runnable JAR...
set JAR_BIN=jar
where jar >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    if exist "%JAVA_HOME%\bin\jar.exe" (
        set "JAR_BIN=%JAVA_HOME%\bin\jar.exe"
    ) else if exist "C:\Program Files\Java\jdk-26.0.2\bin\jar.exe" (
        set "JAR_BIN=C:\Program Files\Java\jdk-26.0.2\bin\jar.exe"
    )
)

"%JAR_BIN%" cfm SmartRecommendationPlatform.jar manifest.txt -C out .
if %ERRORLEVEL% EQU 0 (
    echo [OK] SmartRecommendationPlatform.jar packaged successfully.
) else (
    echo [WARNING] Could not package JAR with "%JAR_BIN%". Classes are available in out/ directory.
)

echo.
echo ========================================================
echo   Build completed successfully!
echo ========================================================
pause
