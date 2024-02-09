#!/bin/bash

# Note: This script must be called from the root of the project:
# ie. `./scripts/uninstall-all-helm-charts.sh`

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

BG_RED='\033[41m'
BG_GREEN='\033[42m'
BG_YELLOW='\033[43m'
BG_BLUE='\033[44m'
BG_DEFAULT='\033[49m'

echo -e "🗑️  ${GREEN}Uninstalling React Release${NC}"
helm uninstall example-react-app-release-1
echo

echo -e "🗑️  ${GREEN}Uninstalling Spring Boot Release${NC}"
helm uninstall example-springboot-release-1
echo

echo -e "🗑️  ${GREEN}Uninstalling Postgres Release${NC}"
helm uninstall example-postgres-release-1