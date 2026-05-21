#!/bin/bash
# EC2 User Data - Auto-setup script for knowledgeforge-api
# Runs automatically on first boot as root

set -e
exec > /var/log/user-data.log 2>&1
echo "=== Starting EC2 setup: $(date) ==="

# Update system
yum update -y

# Install Docker
yum install -y docker
service docker start
systemctl enable docker
usermod -a -G docker ec2-user

# Install AWS CLI v2 (needed for ECR login)
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
  -e DATABASE_URL=jdbc:postgresql://knowledgeforge-db.cgzgia0ci4u8.us-east-1.rds.amazonaws.com:5432/knowledgeforge \
  -e DATABASE_USERNAME=kfadmin \
  -e DATABASE_PASSWORD=KnowledgeForge2026! \
  -e CORS_ORIGIN=https://knowledgeforgeacademy.com \
  -e JWT_SECRET=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b \
  -e STRIPE_API_KEY=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 \
  -e STRIPE_WEBHOOK_SECRET=whsec_NLdY0SUbJKXzTUd8jQlWPHO7riY06KCM \
  774075997131.dkr.ecr.us-east-1.amazonaws.com/knowledgeforge-api:latest

echo "=== Setup complete: $(date) ==="
echo "Container status:"
docker ps
