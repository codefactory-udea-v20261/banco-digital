# Deploying Banco Digital to Google Cloud Platform

This guide covers deploying the Core Banking service to Google Cloud Platform using Cloud Run and Cloud SQL.

## Prerequisites

1. **Google Cloud Project**: Create a GCP project and enable required APIs:
   ```bash
   gcloud services enable cloudbuild.googleapis.com
   gcloud services enable run.googleapis.com
   gcloud services enable sqladmin.googleapis.com
   gcloud services enable artifactregistry.googleapis.com
   gcloud services enable container.googleapis.com
   ```

2. **Cloud SQL Instance**: Create a PostgreSQL Cloud SQL instance
   ```bash
   gcloud sql instances create banco-digital-db \
     --database-version=POSTGRES_15 \
     --tier=db-f1-micro \
     --region=us-central1
   ```

3. **Create Database**:
   ```bash
   gcloud sql databases create banco_digital_core \
     --instance=banco-digital-db
   ```

4. **Docker Repository**: Create an Artifact Registry repository
   ```bash
   gcloud artifacts repositories create banco-digital \
     --repository-format=docker \
     --location=us-central1
   ```

## Deployment Steps

### Option 1: Using Cloud Build (Recommended)

1. **Configure Cloud Build secrets** (if using SonarQube):
   ```bash
   echo -n "your-sonarqube-token" | gcloud secrets create sonarqube-token --data-file=-
   ```

2. **Grant Cloud Build access to secrets**:
   ```bash
   gcloud projects add-iam-policy-binding PROJECT_ID \
     --member=serviceAccount:PROJECT_NUMBER@cloudbuild.gserviceaccount.com \
     --role=roles/secretmanager.secretAccessor
   ```

3. **Deploy via Cloud Build**:
   ```bash
   gcloud builds submit \
     --config=cloudbuild.yaml \
     --substitutions=\
   _REGISTRY=us-central1-docker.pkg.dev,\
   _SERVICE_NAME=banco-digital-core-banking,\
   _REGION=us-central1,\
   _SONARQUBE_HOST=https://sonarqube.example.com,\
   _SONARQUBE_TOKEN=$(gcloud secrets versions access latest --secret=sonarqube-token)
   ```

### Option 2: Manual Docker Build and Deploy

1. **Build Docker image**:
   ```bash
   docker build \
     -t us-central1-docker.pkg.dev/PROJECT_ID/banco-digital/banco-digital-core-banking:latest \
     .
   ```

2. **Push to Artifact Registry**:
   ```bash
   docker push us-central1-docker.pkg.dev/PROJECT_ID/banco-digital/banco-digital-core-banking:latest
   ```

3. **Deploy to Cloud Run**:
   ```bash
   gcloud run deploy banco-digital-core-banking \
     --image=us-central1-docker.pkg.dev/PROJECT_ID/banco-digital/banco-digital-core-banking:latest \
     --platform=managed \
     --region=us-central1 \
     --memory=512Mi \
     --cpu=1 \
     --allow-unauthenticated \
     --set-env-vars=\
   APP_PROFILE=gcp,\
   DB_HOST=10.20.30.40,\
   DB_PORT=5432,\
   DB_NAME=banco_digital_core,\
   DB_USERNAME=postgres,\
   DB_PASSWORD=YOUR_PASSWORD,\
   JWT_SECRET=YOUR_JWT_SECRET,\
   JWT_EXPIRATION_MS=3600000
   ```

## Cloud SQL Connection from Cloud Run

Cloud Run requires one of these approaches to connect to Cloud SQL:

### Using Cloud SQL Auth Proxy (Recommended)
Add the Cloud SQL Auth Proxy sidecar in your Cloud Run configuration:

```bash
gcloud run deploy banco-digital-core-banking \
  --image=us-central1-docker.pkg.dev/PROJECT_ID/banco-digital/banco-digital-core-banking:latest \
  --add-cloudsql-instances=PROJECT_ID:us-central1:banco-digital-db \
  ...
```

### Using Private IP
1. Configure your VPC and Cloud SQL instance with Private IP
2. Deploy Cloud Run on the same VPC

### Environment Variables Setup
Set the following environment variables in Cloud Run:

```
DB_HOST=10.20.30.40              # Private IP of Cloud SQL
DB_PORT=5432
DB_NAME=banco_digital_core
DB_USERNAME=postgres
DB_PASSWORD=$(gcloud secrets versions access latest --secret=db-password)
JWT_SECRET=$(gcloud secrets versions access latest --secret=jwt-secret)
JWT_EXPIRATION_MS=3600000
APP_PROFILE=gcp
```

## Monitoring and Logging

### View Cloud Run Logs
```bash
gcloud run logs read banco-digital-core-banking \
  --region=us-central1 \
  --limit=100
```

### View Cloud Build Logs
```bash
gcloud builds log --limit=100
```

### Cloud SQL Monitoring
```bash
gcloud sql operations list --instance=banco-digital-db
```

## Testing Deployment

```bash
# Get the Cloud Run service URL
SERVICE_URL=$(gcloud run services describe banco-digital-core-banking \
  --region=us-central1 \
  --format='value(status.url)')

# Test health endpoint
curl $SERVICE_URL/actuator/health
```

## Security Considerations

1. **Secrets Management**: Use Google Secret Manager for sensitive data
   ```bash
   echo -n "your-jwt-secret" | gcloud secrets create jwt-secret --data-file=-
   ```

2. **Network Security**:
   - Use Cloud SQL private IP when possible
   - Configure VPC and firewall rules appropriately
   - Use Cloud Armor for DDoS protection

3. **Identity and Access Management**:
   - Grant minimal required roles to service accounts
   - Use Workload Identity for container authentication

4. **Container Security**:
   - Scan images for vulnerabilities
   - Use least privilege container images

## CI/CD Integration

The `cloudbuild.yaml` file supports:
- Automated builds on git push
- Running tests (including coverage)
- SonarQube integration
- Docker image creation and push
- Deployment to Cloud Run

Connect your repository:
```bash
gcloud builds connect --repository-name=banco-digital
```

## Rollback Procedure

Cloud Run automatically maintains previous service revisions:

```bash
# List revisions
gcloud run revisions list --service=banco-digital-core-banking --region=us-central1

# Route traffic to a previous revision
gcloud run services update-traffic banco-digital-core-banking \
  --to-revisions REVISION_NAME=100 \
  --region=us-central1
```

## Cost Optimization

- Use Cloud Run's auto-scaling (0 instances when idle)
- Configure appropriate memory limits (512Mi is often sufficient)
- Use Cloud SQL shared-core instances for development
- Set up budget alerts

## Troubleshooting

### Cloud SQL Connection Issues
- Verify Cloud SQL instance is running
- Check firewall rules
- Confirm credentials are correct
- Review Cloud Run logs

### Deployment Failures
- Check Cloud Build logs: `gcloud builds log --limit=100`
- Verify all required environment variables are set
- Ensure Docker image builds successfully locally

### Performance Issues
- Monitor Cloud Run metrics in Cloud Console
- Check Cloud SQL CPU/memory usage
- Review application logs for errors
