# Knowledge Forge Academy - AWS Deployment Guide

## Prerequisites
- AWS CLI installed and configured (`aws configure`)
- Docker installed
- Your AWS Account ID (run: `aws sts get-caller-identity`)

---

## Step 1: Register Domain (Route 53)

```bash
# Check if knowledgeforgeacademy.com is available
aws route53domains check-domain-availability --domain-name knowledgeforgeacademy.com

# If available, register it ($13/year for .com)
aws route53domains register-domain \
  --domain-name knowledgeforgeacademy.com \
  --duration-in-years 1 \
  --admin-contact FirstName=Aaron,LastName=YourLastName,ContactType=PERSON,CountryCode=US,Email=your@email.com,PhoneNumber=+1.5551234567 \
  --registrant-contact FirstName=Aaron,LastName=YourLastName,ContactType=PERSON,CountryCode=US,Email=your@email.com,PhoneNumber=+1.5551234567 \
  --tech-contact FirstName=Aaron,LastName=YourLastName,ContactType=PERSON,CountryCode=US,Email=your@email.com,PhoneNumber=+1.5551234567 \
  --auto-renew
```

> NOTE: It's easier to do this in the AWS Console: Route 53 > Registered Domains > Register Domain

---

## Step 2: Create SSL Certificate (ACM)

```bash
# Request certificate (must be in us-east-1 for CloudFront)
aws acm request-certificate \
  --region us-east-1 \
  --domain-name knowledgeforgeacademy.com \
  --subject-alternative-names "*.knowledgeforgeacademy.com" \
  --validation-method DNS

# Note the CertificateArn from output - you'll need it later
# Then go to ACM console and click "Create records in Route 53" to validate
```

---

## Step 3: Store Secrets (Secrets Manager)

```bash
# Store your JWT secret
aws secretsmanager create-secret \
  --name knowledgeforge/jwt-secret \
  --secret-string "3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b"

# Store Stripe API key (use your LIVE key for production)
aws secretsmanager create-secret \
  --name knowledgeforge/stripe-api-key \
  --secret-string "sk_live_YOUR_LIVE_KEY_HERE"

# Store Stripe webhook secret
aws secretsmanager create-secret \
  --name knowledgeforge/stripe-webhook-secret \
  --secret-string "whsec_YOUR_LIVE_WEBHOOK_SECRET"
```

---

## Step 4: Create VPC and Networking

```bash
# Create VPC
aws ec2 create-vpc --cidr-block 10.0.0.0/16 --tag-specifications 'ResourceType=vpc,Tags=[{Key=Name,Value=knowledgeforge-vpc}]'
# Note the VpcId

# Enable DNS hostnames
aws ec2 modify-vpc-attribute --vpc-id <VPC_ID> --enable-dns-hostnames

# Create public subnets (2 AZs required for ALB)
aws ec2 create-subnet --vpc-id <VPC_ID> --cidr-block 10.0.1.0/24 --availability-zone us-east-1a --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=knowledgeforge-public-1a}]'
aws ec2 create-subnet --vpc-id <VPC_ID> --cidr-block 10.0.2.0/24 --availability-zone us-east-1b --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=knowledgeforge-public-1b}]'

# Create private subnets (for RDS)
aws ec2 create-subnet --vpc-id <VPC_ID> --cidr-block 10.0.3.0/24 --availability-zone us-east-1a --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=knowledgeforge-private-1a}]'
aws ec2 create-subnet --vpc-id <VPC_ID> --cidr-block 10.0.4.0/24 --availability-zone us-east-1b --tag-specifications 'ResourceType=subnet,Tags=[{Key=Name,Value=knowledgeforge-private-1b}]'

# Create Internet Gateway
aws ec2 create-internet-gateway --tag-specifications 'ResourceType=internet-gateway,Tags=[{Key=Name,Value=knowledgeforge-igw}]'
aws ec2 attach-internet-gateway --internet-gateway-id <IGW_ID> --vpc-id <VPC_ID>

# Create route table for public subnets
aws ec2 create-route-table --vpc-id <VPC_ID> --tag-specifications 'ResourceType=route-table,Tags=[{Key=Name,Value=knowledgeforge-public-rt}]'
aws ec2 create-route --route-table-id <RT_ID> --destination-cidr-block 0.0.0.0/0 --gateway-id <IGW_ID>
aws ec2 associate-route-table --route-table-id <RT_ID> --subnet-id <PUBLIC_SUBNET_1A>
aws ec2 associate-route-table --route-table-id <RT_ID> --subnet-id <PUBLIC_SUBNET_1B>
```

---

## Step 5: Create RDS PostgreSQL

```bash
# Create DB subnet group
aws rds create-db-subnet-group \
  --db-subnet-group-name knowledgeforge-db-subnet \
  --db-subnet-group-description "Knowledge Forge DB subnets" \
  --subnet-ids <PRIVATE_SUBNET_1A> <PRIVATE_SUBNET_1B>

# Create security group for RDS
aws ec2 create-security-group \
  --group-name knowledgeforge-rds-sg \
  --description "RDS access from ECS" \
  --vpc-id <VPC_ID>

# Allow port 5432 from VPC CIDR
aws ec2 authorize-security-group-ingress \
  --group-id <RDS_SG_ID> \
  --protocol tcp \
  --port 5432 \
  --cidr 10.0.0.0/16

# Create the database
aws rds create-db-instance \
  --db-instance-identifier knowledgeforge-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --engine-version 15 \
  --master-username kfadmin \
  --master-user-password YOUR_STRONG_PASSWORD_HERE \
  --allocated-storage 20 \
  --db-name knowledgeforge \
  --vpc-security-group-ids <RDS_SG_ID> \
  --db-subnet-group-name knowledgeforge-db-subnet \
  --no-publicly-accessible \
  --backup-retention-period 7 \
  --storage-encrypted

# Wait for it to be available (takes 5-10 minutes)
aws rds wait db-instance-available --db-instance-identifier knowledgeforge-db

# Get the endpoint
aws rds describe-db-instances --db-instance-identifier knowledgeforge-db --query 'DBInstances[0].Endpoint.Address' --output text
```

---

## Step 6: Create ECR Repository and Push Docker Image

```bash
# Create ECR repository
aws ecr create-repository --repository-name knowledgeforge-api

# Login to ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com

# Build and push (run from project root)
docker build -t knowledgeforge-api .
docker tag knowledgeforge-api:latest <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/knowledgeforge-api:latest
docker push <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/knowledgeforge-api:latest
```

---

## Step 7: Create ECS Cluster and Task Definition

```bash
# Create ECS cluster
aws ecs create-cluster --cluster-name knowledgeforge-cluster

# Create execution role (allows ECS to pull images and write logs)
aws iam create-role \
  --role-name knowledgeforge-ecs-execution \
  --assume-role-policy-document '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"ecs-tasks.amazonaws.com"},"Action":"sts:AssumeRole"}]}'

aws iam attach-role-policy \
  --role-name knowledgeforge-ecs-execution \
  --policy-arn arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy

# Create task role (allows app to access Secrets Manager)
aws iam create-role \
  --role-name knowledgeforge-ecs-task \
  --assume-role-policy-document '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Principal":{"Service":"ecs-tasks.amazonaws.com"},"Action":"sts:AssumeRole"}]}'

aws iam put-role-policy \
  --role-name knowledgeforge-ecs-task \
  --policy-name secrets-access \
  --policy-document '{"Version":"2012-10-17","Statement":[{"Effect":"Allow","Action":["secretsmanager:GetSecretValue"],"Resource":"arn:aws:secretsmanager:us-east-1:<ACCOUNT_ID>:secret:knowledgeforge/*"}]}'

# Create CloudWatch log group
aws logs create-log-group --log-group-name /ecs/knowledgeforge-api
```

Now create the task definition file:

```bash
# Register the task definition (after editing ecs-task-definition.json with your values)
aws ecs register-task-definition --cli-input-json file://ecs-task-definition.json
```

---

## Step 8: Create ALB (Application Load Balancer)

```bash
# Create security group for ALB
aws ec2 create-security-group \
  --group-name knowledgeforge-alb-sg \
  --description "ALB - allow HTTP/HTTPS from internet" \
  --vpc-id <VPC_ID>

aws ec2 authorize-security-group-ingress --group-id <ALB_SG_ID> --protocol tcp --port 443 --cidr 0.0.0.0/0
aws ec2 authorize-security-group-ingress --group-id <ALB_SG_ID> --protocol tcp --port 80 --cidr 0.0.0.0/0

# Create security group for ECS tasks
aws ec2 create-security-group \
  --group-name knowledgeforge-ecs-sg \
  --description "ECS tasks - allow from ALB only" \
  --vpc-id <VPC_ID>

aws ec2 authorize-security-group-ingress --group-id <ECS_SG_ID> --protocol tcp --port 8080 --source-group <ALB_SG_ID>

# Create ALB
aws elbv2 create-load-balancer \
  --name knowledgeforge-alb \
  --subnets <PUBLIC_SUBNET_1A> <PUBLIC_SUBNET_1B> \
  --security-groups <ALB_SG_ID> \
  --scheme internet-facing \
  --type application

# Create target group
aws elbv2 create-target-group \
  --name knowledgeforge-api-tg \
  --protocol HTTP \
  --port 8080 \
  --vpc-id <VPC_ID> \
  --target-type ip \
  --health-check-path /actuator/health \
  --health-check-interval-seconds 30 \
  --healthy-threshold-count 2

# Create HTTPS listener (use your ACM certificate ARN)
aws elbv2 create-listener \
  --load-balancer-arn <ALB_ARN> \
  --protocol HTTPS \
  --port 443 \
  --certificates CertificateArn=<ACM_CERT_ARN> \
  --default-actions Type=forward,TargetGroupArn=<TARGET_GROUP_ARN>

# Create HTTP listener (redirect to HTTPS)
aws elbv2 create-listener \
  --load-balancer-arn <ALB_ARN> \
  --protocol HTTP \
  --port 80 \
  --default-actions Type=redirect,RedirectConfig='{Protocol=HTTPS,Port=443,StatusCode=HTTP_301}'
```

---

## Step 9: Create ECS Service

```bash
aws ecs create-service \
  --cluster knowledgeforge-cluster \
  --service-name knowledgeforge-api \
  --task-definition knowledgeforge-api \
  --desired-count 1 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[<PUBLIC_SUBNET_1A>,<PUBLIC_SUBNET_1B>],securityGroups=[<ECS_SG_ID>],assignPublicIp=ENABLED}" \
  --load-balancers "targetGroupArn=<TARGET_GROUP_ARN>,containerName=knowledgeforge-api,containerPort=8080"
```

---

## Step 10: Deploy Frontend to S3 + CloudFront

```bash
# Build the Vue frontend for production
npm run build

# Create S3 bucket for frontend
aws s3 mb s3://knowledgeforgeacademy-frontend

# Disable block public access (CloudFront will serve it)
aws s3api put-public-access-block \
  --bucket knowledgeforgeacademy-frontend \
  --public-access-block-configuration BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true

# Upload the built files
aws s3 sync dist/ s3://knowledgeforgeacademy-frontend --delete

# Create CloudFront OAC
aws cloudfront create-origin-access-control \
  --origin-access-control-config Name=knowledgeforge-oac,SigningProtocol=sigv4,SigningBehavior=always,OriginAccessControlOriginType=s3
```

Create CloudFront distribution (easier in Console):
1. Go to CloudFront > Create Distribution
2. Origin: S3 bucket (knowledgeforgeacademy-frontend)
3. Origin Access: OAC (select the one you created)
4. Viewer Protocol: Redirect HTTP to HTTPS
5. Alternate Domain: knowledgeforgeacademy.com
6. SSL Certificate: Select your ACM cert (us-east-1)
7. Default Root Object: index.html
8. Error Pages: 403 and 404 both redirect to /index.html (for Vue Router)

---

## Step 11: DNS Configuration (Route 53)

```bash
# Point root domain to CloudFront (frontend)
# In Route 53 hosted zone, create:
# - A record: knowledgeforgeacademy.com -> Alias to CloudFront distribution
# - A record: api.knowledgeforgeacademy.com -> Alias to ALB

# You can do this via CLI or Console. Console is easier:
# Route 53 > Hosted Zones > knowledgeforgeacademy.com > Create Record
```

---

## Step 12: Update Stripe Webhook URL

In Stripe Dashboard:
1. Go to Developers > Webhooks
2. Add endpoint: `https://api.knowledgeforgeacademy.com/api/payment/webhook`
3. Select events: `checkout.session.completed`
4. Copy the new webhook signing secret
5. Update it in Secrets Manager:

```bash
aws secretsmanager update-secret \
  --secret-id knowledgeforge/stripe-webhook-secret \
  --secret-string "whsec_YOUR_NEW_LIVE_WEBHOOK_SECRET"
```

---

## Deploying Updates

### Backend update:
```bash
# Build and push new image
docker build -t knowledgeforge-api .
docker tag knowledgeforge-api:latest <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/knowledgeforge-api:latest
docker push <ACCOUNT_ID>.dkr.ecr.us-east-1.amazonaws.com/knowledgeforge-api:latest

# Force new deployment
aws ecs update-service --cluster knowledgeforge-cluster --service knowledgeforge-api --force-new-deployment
```

### Frontend update:
```bash
npm run build
aws s3 sync dist/ s3://knowledgeforgeacademy-frontend --delete
aws cloudfront create-invalidation --distribution-id <DIST_ID> --paths "/*"
```

---

## Cost Summary (Monthly)

| Service | Cost |
|---------|------|
| RDS db.t3.micro | ~$15 |
| ECS Fargate (0.25 vCPU, 512MB) | ~$10 |
| ALB | ~$16 |
| S3 + CloudFront | ~$2 |
| Route 53 | ~$1 |
| Secrets Manager (3 secrets) | ~$1.20 |
| ACM Certificate | Free |
| **Total** | **~$45/month** |

---

## Checklist Before Going Live

- [ ] Domain registered and DNS propagated
- [ ] ACM certificate validated (green checkmark)
- [ ] RDS instance running and accessible from ECS
- [ ] Docker image pushed to ECR
- [ ] ECS service running with healthy task
- [ ] ALB health checks passing
- [ ] CloudFront serving frontend
- [ ] Stripe webhook updated to production URL
- [ ] Stripe keys switched from test to live
- [ ] Test full flow: Register > Login > Take Quiz > Payment > Premium access
