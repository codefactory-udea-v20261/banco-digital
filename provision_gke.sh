#!/bin/bash

# Configuration
PROJECT_ID="project-2183efe1-b87d-4c12-966"
REGION="us-central1"
CLUSTER_NAME="banco-digital-cluster"
NAMESPACE="banco-digital"
GSA_NAME="microservices-runtime" # Google Service Account
KSA_NAME="banco-digital-ksa"     # Kubernetes Service Account

echo "🚀 Provisioning GKE Cluster: $CLUSTER_NAME..."

# 1. Enable Required APIs
gcloud services enable container.googleapis.com --project=$PROJECT_ID

# 2. Create GKE Cluster (Autopilot is cheaper for small/dev projects)
gcloud container clusters create-auto $CLUSTER_NAME \
    --region $REGION \
    --project $PROJECT_ID

# 3. Get Cluster Credentials
gcloud container clusters get-credentials $CLUSTER_NAME --region $REGION --project=$PROJECT_ID

# 4. Create Namespace
kubectl create namespace $NAMESPACE

# 5. Setup Workload Identity
echo "🔐 Setting up Workload Identity..."

# Create Kubernetes Service Account
kubectl create serviceaccount $KSA_NAME --namespace $NAMESPACE

# Bind KSA to GSA
gcloud iam service-accounts add-iam-policy-binding $GSA_NAME@$PROJECT_ID.iam.gserviceaccount.com \
    --role roles/iam.workloadIdentityUser \
    --member "serviceAccount:$PROJECT_ID.svc.id.goog[$NAMESPACE/$KSA_NAME]" \
    --project=$PROJECT_ID

# Annotate KSA
kubectl annotate serviceaccount $KSA_NAME \
    --namespace $NAMESPACE \
    iam.gke.io/gcp-service-account=$GSA_NAME@$PROJECT_ID.iam.gserviceaccount.com

echo "✅ GKE Provisioning Complete!"
