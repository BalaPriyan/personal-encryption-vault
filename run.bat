@echo off
rem Aegis Vault — AI-Powered Personal Encryption Service
rem Windows Batch Run Script

setlocal enabledelayedexpansion

cd /d "%~dp0"

set "PORT=8080"
set "ACTION=RUN"

if "%~1"=="--help" goto :help
if "%~1"=="-h" goto :help
if "%~1"=="--test" goto :test
if "%~1"=="-t" goto :test
if "%~1"=="--build" goto :build
if "%~1"=="-b" goto :build
if "%~1"=="--clean" goto :clean
if "%~1"=="-c" goto :clean

if not "%~1"=="" (
    set "PORT=%~1"
)

echo ================================================================
echo    AEGIS VAULT — AI-POWERED PERSONAL ENCRYPTION SERVICE
echo ================================================================
echo  * Cipher:      AES-256-GCM + PBKDF2-SHA256 (65,536 rounds)
echo  * AI Advisor:  Active (Deep PII & Secret Leak Detection)
echo  * Sentinel:    Active (Velocity & Intrusion Shield)
echo  * Duress PIN:  Active (9999 triggers Decoy Vault)
echo  * Port:        %PORT%
echo  * Dashboard:   http://localhost:%PORT%
echo ----------------------------------------------------------------
echo  Showcase Demo Credentials:
echo  Master Key: CyberVault2026!SecureKey#99
echo  Duress PIN: 9999 (Unlocks harmless decoy records)
echo ================================================================
echo.

if exist "gradlew.bat" (
    call gradlew.bat run --args="%PORT%"
) else (
    call gradle run --args="%PORT%"
)
goto :eof

:test
echo Running JUnit 5 test suite...
if exist "gradlew.bat" (
    call gradlew.bat test
) else (
    call gradle test
)
goto :eof

:build
echo Building project classes...
if exist "gradlew.bat" (
    call gradlew.bat classes
) else (
    call gradle classes
)
goto :eof

:clean
echo Cleaning build artifacts...
if exist "gradlew.bat" (
    call gradlew.bat clean
) else (
    call gradle clean
)
goto :eof

:help
echo Usage: run.bat [PORT] [OPTIONS]
echo.
echo Options:
echo   [PORT]       Custom port number (default: 8080)
echo   --test, -t   Run test suite
echo   --build, -b  Build classes
echo   --clean, -c  Clean build artifacts
echo   --help, -h   Show this help message
goto :eof
