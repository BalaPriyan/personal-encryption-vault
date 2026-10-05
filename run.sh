#!/usr/bin/env bash
# ==============================================================================
# Aegis Vault — AI-Powered Personal Encryption Service
# Launch & Management Script
# ==============================================================================

set -euo pipefail

# Color palette (Atlassian / Modern Terminal)
BOLD='\033[1m'
CYAN='\033[0;36m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

# Determine project root directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

print_banner() {
    echo -e "${CYAN}${BOLD}"
    echo "  ================================================================"
    echo "     AEGIS VAULT — AI-POWERED PERSONAL ENCRYPTION SERVICE         "
    echo "  ================================================================"
    echo -e "${NC}"
}

print_usage() {
    echo -e "${BOLD}Usage:${NC} ./run.sh [OPTIONS] [PORT]"
    echo ""
    echo -e "${BOLD}Options:${NC}"
    echo -e "  ${GREEN}[PORT]${NC}                 Start web server on custom port (default: 8080)"
    echo -e "  ${GREEN}-p, --port <port>${NC}      Specify server port"
    echo -e "  ${GREEN}-t, --test${NC}             Run the JUnit 5 test suite"
    echo -e "  ${GREEN}-b, --build${NC}            Build and verify project classes"
    echo -e "  ${GREEN}-c, --clean${NC}            Clean Gradle build artifacts"
    echo -e "  ${GREEN}-h, --help${NC}             Display this help menu"
    echo ""
    echo -e "${BOLD}Examples:${NC}"
    echo "  ./run.sh                  # Starts on http://localhost:8080"
    echo "  ./run.sh 9090             # Starts on http://localhost:9090"
    echo "  ./run.sh --port 8888      # Starts on http://localhost:8888"
    echo "  ./run.sh --test           # Runs the test suite"
    echo ""
}

# Check for Java installation
check_java() {
    if ! command -v java >/dev/null 2>&1; then
        echo -e "${RED}${BOLD}Error: Java is not installed or not in your PATH.${NC}"
        echo "Please install JDK 21 or newer (tested with OpenJDK 26)."
        exit 1
    fi
}

# Resolve Gradle executable (prefer ./gradlew, fallback to system gradle)
resolve_gradle() {
    if [ -x "./gradlew" ]; then
        GRADLE_CMD="./gradlew"
    elif command -v gradle >/dev/null 2>&1; then
        GRADLE_CMD="gradle"
    else
        echo -e "${RED}${BOLD}Error: Neither ./gradlew nor system gradle was found.${NC}"
        echo "Please install Gradle or generate the wrapper using 'gradle wrapper'."
        exit 1
    fi
}

# --- Main CLI Argument Parsing ---
PORT=8080
RUN_TESTS=false
RUN_BUILD=false
RUN_CLEAN=false

while [[ $# -gt 0 ]]; do
    case "$1" in
        -h|--help)
            print_banner
            print_usage
            exit 0
            ;;
        -t|--test)
            RUN_TESTS=true
            shift
            ;;
        -b|--build)
            RUN_BUILD=true
            shift
            ;;
        -c|--clean)
            RUN_CLEAN=true
            shift
            ;;
        -p|--port)
            if [[ -n "${2:-}" && "$2" =~ ^[0-9]+$ ]]; then
                PORT="$2"
                shift 2
            else
                echo -e "${RED}Error: --port requires a valid integer argument.${NC}"
                exit 1
            fi
            ;;
        *)
            if [[ "$1" =~ ^[0-9]+$ ]]; then
                PORT="$1"
                shift
            else
                echo -e "${RED}Unknown option: $1${NC}"
                echo "Use ./run.sh --help for available commands."
                exit 1
            fi
            ;;
    esac
done

check_java
resolve_gradle

# Handle Clean command
if [ "$RUN_CLEAN" = true ]; then
    print_banner
    echo -e "${BLUE}Cleaning build artifacts...${NC}"
    $GRADLE_CMD clean
    echo -e "${GREEN}Clean completed successfully.${NC}"
    exit 0
fi

# Handle Test command
if [ "$RUN_TESTS" = true ]; then
    print_banner
    echo -e "${BLUE}Running JUnit 5 cryptographic and AI test suite...${NC}"
    $GRADLE_CMD test --info
    echo ""
    echo -e "${GREEN}${BOLD}✓ All tests passed!${NC}"
    echo -e "Report: ${CYAN}build/reports/tests/test/index.html${NC}"
    exit 0
fi

# Handle Build command
if [ "$RUN_BUILD" = true ]; then
    print_banner
    echo -e "${BLUE}Building project classes...${NC}"
    $GRADLE_CMD classes
    echo -e "${GREEN}${BOLD}✓ Build succeeded.${NC}"
    exit 0
fi

# Default: Run the application
print_banner

echo -e "  ${BLUE}●${NC} Engine:       ${BOLD}AES-256-GCM + PBKDF2-SHA256 (65,536 rounds)${NC}"
echo -e "  ${BLUE}●${NC} AI Advisor:   ${BOLD}Active (Deep PII & Secret Leak Detection)${NC}"
echo -e "  ${BLUE}●${NC} Sentinel:     ${BOLD}Active (Velocity & Intrusion Shield)${NC}"
echo -e "  ${BLUE}●${NC} Duress PIN:   ${BOLD}Active (9999 triggers Decoy Vault)${NC}"
echo -e "  ${BLUE}●${NC} Port:         ${GREEN}${BOLD}${PORT}${NC}"
echo -e "  ${BLUE}●${NC} Dashboard:    ${CYAN}${BOLD}http://localhost:${PORT}${NC}"
echo ""
echo -e "  ${YELLOW}${BOLD}Showcase Demo Credentials:${NC}"
echo -e "  Master Key: ${BOLD}CyberVault2026!SecureKey#99${NC}"
echo -e "  Duress PIN: ${BOLD}9999${NC} (Unlocks harmless decoy records)"
echo ""
echo -e "  ${MAGENTA}Press Ctrl+C to stop the vault server.${NC}"
echo "  ----------------------------------------------------------------"
echo ""

# Launch application passing port argument
export PORT
exec $GRADLE_CMD run --args="$PORT"
