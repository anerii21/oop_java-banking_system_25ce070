@echo off
REM ==============================================================================
REM OOP Java Banking System - Execution Script (Windows)
REM Practical / Roll No: 25CE070 - Aneri Patel
REM GitHub: https://github.com/anerii21/oop_java-banking_system_25ce070.git
REM ==============================================================================

echo ==========================================================
echo  Starting OOP Java Banking Management System (25CE070)...
echo ==========================================================

where java >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: 'java' command not found. Please install JDK 11 or higher.
    pause
    exit /b 1
)

where javac >nul 2>nul
if %errorlevel% neq 0 (
    echo Error: 'javac' command not found. Please ensure JDK is added to PATH.
    pause
    exit /b 1
)

if not exist bin mkdir bin
if not exist data mkdir data

echo Compiling Java sources...
dir /s /B src\*.java > sources.txt
javac -d bin -sourcepath src @sources.txt
del sources.txt

if %errorlevel% equ 0 (
    echo Starting Application...
    java -cp bin com.bank.Main
) else (
    echo Compilation failed.
)
pause
