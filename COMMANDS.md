# Quick Command Reference

## Build & Test Commands

### Standard Build (Recommended)
```bash
./build.sh
```

### Maven Verify with CI Profile
```bash
mvn -B verify -P ci -Djacoco.haltOnFailure=false -DskipITs=true
```

### Run Tests Only
```bash
mvn clean test -DskipITs
```

### Run with Coverage Report
```bash
mvn clean test -DskipITs && open target/site/jacoco/index.html
```

## Docker Commands

### Start All Services
```bash
docker-compose up --build
```

### Start Services (Background)
```bash
docker-compose up -d --build
```

### Stop Services
```bash
docker-compose down
```

### View Logs
```bash
docker-compose logs -f core-banking
```

### Test Health Endpoint
```bash
curl http://localhost:8080/actuator/health
```

### Access Database
```bash
docker-compose exec postgres psql -U postgres -d banco_digital_core
```

## SonarQube Commands

### Scan with SonarQube
```bash
mvn clean verify sonar:sonar \
  -Dsonar.host.url=https://your-sonarqube \
  -Dsonar.login=YOUR_TOKEN \
  -Dsonar.organization=codefactory-udea-v20261
```

### Dry Run (Local Analysis Only)
```bash
mvn sonar:sonar -Dsonar.dryRun=true
```

## Google Cloud Commands

### Deploy to Cloud Run
```bash
gcloud run deploy banco-digital-core-banking \
  --image=us-central1-docker.pkg.dev/PROJECT_ID/banco-digital/banco-digital-core-banking:latest \
  --platform=managed \
  --region=us-central1 \
  --set-env-vars=APP_PROFILE=gcp,DB_HOST=YOUR_HOST,DB_PORT=5432,DB_NAME=banco_digital_core
```

### Submit Cloud Build
```bash
gcloud builds submit --config=cloudbuild.yaml
```

### View Cloud Build Logs
```bash
gcloud builds log --limit=100
```

### List Cloud Run Services
```bash
gcloud run services list --region=us-central1
```

### Get Cloud Run Service URL
```bash
gcloud run services describe banco-digital-core-banking \
  --region=us-central1 \
  --format='value(status.url)'
```

## Code Quality Commands

### Check Test Coverage Report
```bash
open target/site/jacoco/index.html
```

### Generate Coverage Report
```bash
mvn jacoco:report
```

### Show Coverage Statistics
```bash
python3 << 'EOF'
import xml.etree.ElementTree as ET
tree = ET.parse('target/site/jacoco/jacoco.xml')
root = tree.getroot()
for counter in root.findall('./counter'):
    counter_type = counter.get('type')
    missed = int(counter.get('missed', 0))
    covered = int(counter.get('covered', 0))
    total = missed + covered
    if total > 0:
        coverage = (covered / total) * 100
        print(f"{counter_type}: {coverage:.2f}%")
EOF
```

## Development Workflow

### New Feature Development
```bash
# 1. Create feature branch
git checkout -b feature/new-feature

# 2. Make changes and run tests
./build.sh

# 3. View coverage
open target/site/jacoco/index.html

# 4. Commit with good message
git commit -m "feature: add new feature"

# 5. Push to remote
git push origin feature/new-feature

# 6. Create PR/MR
```

### Before Pushing
```bash
# Run complete verification
./build.sh

# Check no uncommitted changes break build
git status

# Verify coverage is acceptable
open target/site/jacoco/index.html
```

## Debugging Commands

### Run Specific Test
```bash
mvn test -Dtest=YourTestClassName
```

### Run with Debug Output
```bash
mvn test -X
```

### Skip Tests
```bash
mvn clean package -DskipTests
```

### Show Dependency Tree
```bash
mvn dependency:tree
```

### Update Dependencies
```bash
mvn versions:display-dependency-updates
```

## Database Commands

### Create Cloud SQL Instance
```bash
gcloud sql instances create banco-digital-db \
  --database-version=POSTGRES_15 \
  --tier=db-f1-micro \
  --region=us-central1
```

### Create Database
```bash
gcloud sql databases create banco_digital_core \
  --instance=banco-digital-db
```

### Connect to Cloud SQL
```bash
gcloud sql connect banco-digital-db \
  --user=postgres \
  --database=banco_digital_core
```

### Backup Database
```bash
gcloud sql backups create \
  --instance=banco-digital-db
```

## Monitoring Commands

### View Application Logs
```bash
gcloud run logs read banco-digital-core-banking \
  --region=us-central1 \
  --limit=100
```

### View CPU/Memory Usage
```bash
gcloud run services describe banco-digital-core-banking \
  --region=us-central1 --format=json | jq '.status.conditions'
```

### Stream Live Logs
```bash
gcloud logging read \
  "resource.type=cloud_run_revision AND resource.labels.service_name=banco-digital-core-banking" \
  --limit=100 \
  --format=json
```

## Cleanup Commands

### Remove Build Artifacts
```bash
mvn clean
```

### Remove Docker Images
```bash
docker-compose down
docker rmi $(docker images -q) --force
```

### Remove Local Coverage Reports
```bash
rm -rf target/site/jacoco/
```

### Clean GCP Resources
```bash
# Stop Cloud Run service (doesn't delete)
gcloud run services update banco-digital-core-banking \
  --no-traffic --region=us-central1

# Delete service
gcloud run services delete banco-digital-core-banking \
  --region=us-central1
```

## Environment Setup

### Set GCP Project
```bash
export GCP_PROJECT=your-project-id
gcloud config set project $GCP_PROJECT
```

### Set SonarQube Token
```bash
export SONARQUBE_HOST=https://your-sonarqube
export SONARQUBE_TOKEN=your-token
```

### Set Database Credentials
```bash
export DB_HOST=localhost
export DB_PORT=5433
export DB_NAME=banco_digital_core
export DB_USERNAME=postgres
export DB_PASSWORD=admin
```

## Useful Aliases

Add to `.bashrc` or `.zshrc`:
```bash
alias build="./build.sh"
alias dcup="docker-compose up --build"
alias dcdown="docker-compose down"
alias dclogs="docker-compose logs -f core-banking"
alias coverage="open target/site/jacoco/index.html"
alias sonar-scan="mvn clean verify sonar:sonar -Dsonar.host.url=$SONARQUBE_HOST -Dsonar.login=$SONARQUBE_TOKEN"
alias gcp-logs="gcloud run logs read banco-digital-core-banking --region=us-central1"
```

## Status Check Commands

### Verify Everything Works
```bash
echo "🔍 Checking project status..."

# Check build
echo "✓ Building project..."
./build.sh > /dev/null 2>&1 && echo "  ✅ Build successful" || echo "  ❌ Build failed"

# Check Docker
echo "✓ Checking Docker..."
docker-compose config --quiet > /dev/null 2>&1 && echo "  ✅ Docker Compose valid" || echo "  ❌ Docker config invalid"

# Check coverage
echo "✓ Checking coverage..."
if [ -f "target/site/jacoco/jacoco.xml" ]; then
    python3 << 'EOF'
import xml.etree.ElementTree as ET
tree = ET.parse('target/site/jacoco/jacoco.xml')
root = tree.getroot()
for counter in root.findall('./counter'):
    if counter.get('type') == 'LINE':
        missed = int(counter.get('missed', 0))
        covered = int(counter.get('covered', 0))
        total = missed + covered
        coverage = (covered / total) * 100
        status = "✅" if coverage >= 80 else "⚠️"
        print(f"  {status} Coverage: {coverage:.2f}%")
EOF
fi

echo ""
echo "🎉 All checks complete!"
```

---

**Pro Tip**: Create a Makefile with these commands for easier access:
```makefile
.PHONY: build test docker-up docker-down coverage sonar deploy

build:
	./build.sh

test:
	mvn clean test -DskipITs

docker-up:
	docker-compose up --build

docker-down:
	docker-compose down

coverage:
	open target/site/jacoco/index.html

sonar:
	mvn clean verify sonar:sonar

deploy:
	gcloud builds submit --config=cloudbuild.yaml
```

Then use: `make build`, `make test`, etc.
