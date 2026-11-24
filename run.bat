@echo off
title Smart Recommendation Service Management Platform (Capstone #256)
echo ====================================================================
echo Starting Smart Recommendation Service Management Platform...
echo Capstone Project #256 - UN SDG 9: Industry, Innovation & Infrastructure
echo Embedded HTTP Server: http://localhost:8080/
echo ====================================================================
if exist SmartRecommendationPlatform.jar (
    java -jar SmartRecommendationPlatform.jar
) else (
    java SmartRecommendationPlatform.java
)
if errorlevel 1 (
    echo.
    echo Application exited with an error code: %errorlevel%
    pause
)
