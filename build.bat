@echo off
title Build Smart Recommendation Platform
echo Compiling SmartRecommendationPlatform.java...
javac SmartRecommendationPlatform.java
if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

set JAR_CMD=jar
where jar >nul 2>&1
if errorlevel 1 (
    if exist "C:\Program Files\Java\jdk-26.0.2\bin\jar.exe" (
        set "JAR_CMD=C:\Program Files\Java\jdk-26.0.2\bin\jar.exe"
    ) else if exist "%JAVA_HOME%\bin\jar.exe" (
        set "JAR_CMD=%JAVA_HOME%\bin\jar.exe"
    )
)

echo Creating manifest...
echo Main-Class: SmartRecommendationPlatform> manifest.txt

echo Packaging SmartRecommendationPlatform.jar...
"%JAR_CMD%" cfm SmartRecommendationPlatform.jar manifest.txt *.class
if exist manifest.txt del manifest.txt

echo Cleaning up intermediate class files to keep workspace tidy...
if exist *.class del /q *.class

echo ====================================================================
echo Build successful! Standalone SmartRecommendationPlatform.jar created.
echo Workspace kept completely clean with 0 extra .class files.
echo You can run it with: run.bat or java -jar SmartRecommendationPlatform.jar
echo ====================================================================
pause
