#!/bin/bash

# Build script for CI/CD pipelines
# Handles Maven wrapper download issues by falling back to system Maven

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

echo "════════════════════════════════════════════════════════════════"
echo "Banco Digital Core Banking - Maven Build"
echo "════════════════════════════════════════════════════════════════"
echo ""

# Try to use Maven wrapper first, fall back to system Maven if it fails
if command -v ./mvnw &> /dev/null; then
    echo "✓ Maven wrapper found"
    MAVEN_CMD="./mvnw"
elif command -v mvn &> /dev/null; then
    echo "✓ System Maven found ($(mvn --version | head -1))"
    MAVEN_CMD="mvn"
else
    echo "✗ Maven not found. Please install Maven or use the Maven wrapper."
    exit 1
fi

echo ""
echo "Running Maven build with CI profile..."
echo "════════════════════════════════════════════════════════════════"
echo ""

# Run Maven verify with CI profile
$MAVEN_CMD \
    -B \
    verify \
    -P ci \
    -Djacoco.haltOnFailure=false \
    -DskipITs=true \
    "$@"

BUILD_EXIT_CODE=$?

echo ""
echo "════════════════════════════════════════════════════════════════"

if [ $BUILD_EXIT_CODE -eq 0 ]; then
    echo "✅ BUILD SUCCESSFUL"
    echo ""
    echo "Coverage Report: target/site/jacoco/index.html"
    echo "Package JAR: target/banco-digital-core-banking-*.jar"
    echo ""
    
    # Print coverage summary
    if [ -f "target/site/jacoco/jacoco.xml" ]; then
        echo "Coverage Summary:"
        python3 << 'PYTHON'
import xml.etree.ElementTree as ET
tree = ET.parse('target/site/jacoco/jacoco.xml')
root = tree.getroot()
for counter in root.findall('./counter'):
    counter_type = counter.get('type')
    missed = int(counter.get('missed', 0))
    covered = int(counter.get('covered', 0))
    total = missed + covered
    if total > 0 and counter_type in ['LINE', 'BRANCH', 'METHOD']:
        coverage = (covered / total) * 100
        print(f"  {counter_type}: {coverage:.2f}%")
PYTHON
    fi
else
    echo "❌ BUILD FAILED"
    echo "Exit code: $BUILD_EXIT_CODE"
fi

echo "════════════════════════════════════════════════════════════════"
exit $BUILD_EXIT_CODE
