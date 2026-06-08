#!/bin/bash
# EC2 User Data - Auto-setup script for knowledgeforge-api
# Runs automatically on first boot as root
# NOTE: Replace env var values before use - never commit real secrets

set -e
exec > /var/log/user-data.log 2>&1
echo "=== Starting EC2 setup: $(date) ==="

yum update -y
yum install -y docker
service docker start
systemctl enable docker
usermod -a -G docker ec2-user

yum install -y unzip
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
./aws/install
rm -rf awscliv2.zip aws/

echo "=== Logging into ECR ==="
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin \
  774075997131.dkr.ecr.us-east-1.amazonaws.com

echo "=== Pulling image ==="
docker pull 774075997131.dkr.ecr.us-east-1.amazonaws.com/knowledgeforge-api:latest

echo "=== Starting container ==="
docker run -d \
  --name knowledgeforge-api \
  --restart always \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=REPLACE \
  -e DATABASE_USERNAME=REPLACE \
  -e DATABASE_PASSWORD=REPLACE \
  -e CORS_ORIGIN=https://knowledgeforgeacademy.com \
  -e JWT_SECRET=REPLACE \
  -e STRIPE_API_KEY=REPLACE \
  -e STRIPE_WEBHOOK_SECRET=REPLACE \
  774075997131.dkr.ecr.us-east-1.amazonaws.com/knowledgeforge-api:latest

echo "=== Setup complete: $(date) ==="
docker ps
