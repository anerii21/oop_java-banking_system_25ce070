#!/usr/bin/env bash
# ==============================================================================
# OOP Java Banking System - Execution Script (macOS / Linux)
# Practical / Roll No: 25CE070 - Aneri Patel
# GitHub: https://github.com/anerii21/oop_java-banking_system_25ce070.git
# ==============================================================================

set -e

# Change directory to the script's root folder
cd "$(dirname "$0")"

echo "=========================================================="
echo " Starting OOP Java Banking Management System (25CE070)..."
echo "=========================================================="

# Check for Java installation
if ! command -v java &> /dev/null || ! command -v javac &> /dev/null; then
    echo "Error: Java Development Kit (JDK) is required to run this project."
    echo "Please install JDK 11 or higher and ensure 'java' and 'javac' are in your PATH."
    exit 1
fi

# Create bin and data directories if needed
mkdir -p bin data

# Compile Java sources
echo "Compiling Java sources..."
javac -d bin -sourcepath src $(find src -name "*.java")

# Run main application
echo "Starting Application..."
java -cp bin com.bank.Main
