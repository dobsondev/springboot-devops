#!/bin/bash

# Note: This script must be called from the root of the project:
# ie. `./scripts/install-all-helm-charts.sh`

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

SPRINGBOOT_IMAGE_TAG=""
REACT_IMAGE_TAG=""

# Parse command line arguments
for arg in "$@"; do
    if [[ "$arg" == "reactimage="* ]]; then
        REACT_IMAGE_TAG="${arg#*=}"
    elif [[ "$arg" == "springbootimage="* ]]; then
        SPRINGBOOT_IMAGE_TAG="${arg#*=}"
    fi
done

# Install Postgres Helm Release
echo -e "🚀 ${GREEN}Installing Postgres Helm release${NC} example-postgres-release-1"
helm install example-postgres-release-1 helm/postgres
echo
echo -e "⏳ ${GREEN}Waiting for Postgres Pods to be ready...${NC}"
kubectl wait --for=condition=ready pod -l name=postgres-pod
echo
echo -e "📦 ${GREEN}Postgres Pod status:${NC}"
kubectl get pods --selector=name=postgres-pod
echo

# Install Spring Boot Helm Release
echo -e "🚀 ${GREEN}Installing Sping Boot Helm release${NC} example-springboot-release-1"
# Append --set imageTag=${SPRINGBOOT_IMAGE_TAG} if SPRINGBOOT_IMAGE_TAG is provided
if [ -n "$SPRINGBOOT_IMAGE_TAG" ]; then
    echo -e "🛠️  ${GREEN}Overwriting Spring Boot image tag with${NC} ${SPRINGBOOT_IMAGE_TAG}"
    helm install example-springboot-release-1 helm/springboot -f helm/environments/minikube.yaml --set imageTag="${SPRINGBOOT_IMAGE_TAG}"
else
    helm install example-springboot-release-1 helm/springboot -f helm/environments/minikube.yaml
fi
echo
echo -e "⏳ ${GREEN}Waiting for Spring Boot Pods to be ready...${NC}"
kubectl wait --for=condition=ready pod -l name=springboot-pod
echo
echo -e "📦 ${GREEN}Spring Boot Pod status:${NC}"
kubectl get pods --selector=name=springboot-pod
echo

# Install React Helm Release
echo -e "🚀 ${GREEN}Installing React Helm release${NC} example-react-app-release-1"
# Append --set imageTag=${REACT_IMAGE_TAG} if REACT_IMAGE_TAG is provided
if [ -n "$REACT_IMAGE_TAG" ]; then
    echo -e "🛠️  ${GREEN}Overwriting React image tag with${NC} ${SPRINGBOOT_IMAGE_TAG}"
    helm install example-react-app-release-1 helm/react-app -f helm/environments/minikube.yaml --set imageTag="${REACT_IMAGE_TAG}"
else
    helm install example-react-app-release-1 helm/react-app -f helm/environments/minikube.yaml
fi
echo
echo -e "⏳ ${GREEN}Waiting for React Pods to be ready${NC}"
kubectl wait --for=condition=ready pod -l name=react-app-pod
echo
echo -e "📦 ${GREEN}React Pod status:${NC}"
kubectl get pods --selector=name=react-app-pod
echo

# Summary Section
echo -e "----------------------------------- SUMMARY -----------------------------------"
echo -e "     Postgres Helm Release: ${GREEN}example-postgres-release-1${NC}"
springbootImageTag=$(helm get values example-springboot-release-1 | grep -o 'imageTag:.*' | awk '{print $2}')
echo -e "  Spring Boot Helm Release: ${GREEN}example-springboot-release-1${NC} with tag ${GREEN}$springbootImageTag${NC}"
reactImageTag=$(helm get values example-react-app-release-1 | grep -o 'imageTag:.*' | awk '{print $2}')
echo -e "        React Helm Release: ${GREEN}example-react-app-release-1${NC} with tag ${GREEN}$reactImageTag${NC}"
echo -e "--------------------------------------------------------------------------------${NC}"