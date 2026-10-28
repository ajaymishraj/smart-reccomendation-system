@echo off
setlocal

echo ========================================================
echo   Starting Smart Recommendation Service Platform       
echo   NIET Greater Noida | Capstone Project #256           
echo ========================================================

if exist "SmartRecommendationPlatform.jar" (
    echo Launching from SmartRecommendationPlatform.jar...
    java -jar SmartRecommendationPlatform.jar %*
) else (
    echo JAR not found. Launching from out/ directory...
    java -cp out com.recommendation.platform.Main %*
)

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Application exited with error code %ERRORLEVEL%.
    pause
)
