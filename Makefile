# ==============================================================================
# Makefile for OOP Java Banking System (25CE070)
# ==============================================================================

JAVAC = javac
JAVA = java
SRC_DIR = src
BIN_DIR = bin
DATA_DIR = data
MAIN_CLASS = com.bank.Main
TEST_CLASS = com.bank.BankingSystemTest

.PHONY: all build run test clean help

all: build

build:
	@mkdir -p $(BIN_DIR) $(DATA_DIR)
	@echo "Compiling Java sources..."
	$(JAVAC) -d $(BIN_DIR) -sourcepath $(SRC_DIR) $$(find $(SRC_DIR) -name "*.java")
	@echo "Build complete."

run: build
	@echo "Launching Banking Application..."
	$(JAVA) -cp $(BIN_DIR) $(MAIN_CLASS)

test: build
	@echo "Running verification test suite..."
	$(JAVA) -cp $(BIN_DIR) $(TEST_CLASS)

clean:
	@echo "Cleaning compiled classes..."
	rm -rf $(BIN_DIR)
	@echo "Clean complete."

help:
	@echo "Available commands:"
	@echo "  make build  - Compile all Java sources into bin/"
	@echo "  make run    - Compile and launch the interactive Banking System"
	@echo "  make test   - Run automated test suite verifying OOP rules"
	@echo "  make clean  - Remove compiled class files"
