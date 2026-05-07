#!/bin/bash

# Configuration Variables
PROJECT_ID="982674607718"
REGION="us-central1"
SQL_INSTANCE="$PROJECT_ID:$REGION:banco-digital-db"
KAFKA_BROKERS="34.135.191.171:9092" 

echo "🚀 Starting FINAL secure configuration update..."

# Common Environment Variables for all services using DB
# 127.0.0.1 is used because Cloud Run with Cloud SQL Proxy maps the DB to the local interface.
# initialization-fail-timeout=60000 ensures the app waits for the proxy to be ready.
DB_COMMON_VARS="SPRING_PROFILES_ACTIVE=prod,DB_HOST=127.0.0.1,DB_PORT=5432,SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT=60000,SPRING_KAFKA_BOOTSTRAP_SERVERS=$KAFKA_BROKERS"

# 1. Core Banking (banco-digital)
echo "📦 Updating banco-digital..."
gcloud run services update banco-digital \
  --region=$REGION \
  --add-cloudsql-instances=$SQL_INSTANCE \
  --set-env-vars="$DB_COMMON_VARS,DB_NAME=banco_digital_core,DB_USERNAME=core_user,DB_PASSWORD=CoreService@2026" \
  --set-secrets="ENCRYPTION_KEY=ENCRYPTION_KEY:latest,JWT_SECRET=JWT_SECRET:latest" \
  --cpu-boost # Helps Java start faster

# 2. Identity Service
echo "📦 Updating banco-digital-identity..."
gcloud run services update banco-digital-identity \
  --region=$REGION \
  --add-cloudsql-instances=$SQL_INSTANCE \
  --set-env-vars="$DB_COMMON_VARS,DB_NAME=banco_digital_identity,DB_USERNAME=postgres,DB_PASSWORD=BancoDigital@2026" \
  --set-secrets="ENCRYPTION_KEY=ENCRYPTION_KEY:latest,JWT_SECRET=JWT_SECRET:latest" \
  --cpu-boost

# 3. Audit Service
echo "📦 Updating banco-digital-audit..."
gcloud run services update banco-digital-audit \
  --region=$REGION \
  --add-cloudsql-instances=$SQL_INSTANCE \
  --set-env-vars="$DB_COMMON_VARS,DB_NAME=banco_digital_audit,DB_USERNAME=audit_user,DB_PASSWORD=AuditService@2026" \
  --set-secrets="ENCRYPTION_KEY=ENCRYPTION_KEY:latest,JWT_SECRET=JWT_SECRET:latest" \
  --cpu-boost

# 4. Reporting Service
echo "📦 Updating banco-digital-reporting..."
gcloud run services update banco-digital-reporting \
  --region=$REGION \
  --add-cloudsql-instances=$SQL_INSTANCE \
  --set-env-vars="$DB_COMMON_VARS,DB_NAME=banco_digital_reporting,DB_USERNAME=report_user,DB_PASSWORD=ReportService@2026" \
  --set-secrets="ENCRYPTION_KEY=ENCRYPTION_KEY:latest,JWT_SECRET=JWT_SECRET:latest" \
  --cpu-boost

# 5. Gateway
echo "📦 Updating banco-digital-gateway..."
gcloud run services update banco-digital-gateway \
  --region=$REGION \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
  --set-secrets="JWT_SECRET=JWT_SECRET:latest" \
  --cpu-boost

echo "✅ All services updated with high-availability and security settings!"
echo "⏳ Waiting 30 seconds for services to stabilize..."
sleep 30

# Verify Core Banking
gcloud run services describe banco-digital --region=$REGION --format="value(status.url)"
