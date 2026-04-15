package com.example.awsMachineLearningExam.config;

import com.example.awsMachineLearningExam.model.CourseModule;
import com.example.awsMachineLearningExam.repository.CourseModuleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner seedKnowledgeForge(CourseModuleRepository repository) {
        return args -> {
            // Only seed the data if the table is completely empty
            if (repository.count() == 0) {
                System.out.println("Initializing Jedi Archives... Uploading Full AWS Curriculum.");

                List<CourseModule> modules = new ArrayList<>();

                // ==========================================
                // FOUNDATIONAL CERTIFICATIONS
                // ==========================================

                // --- CLF-C02: Cloud Practitioner ---
                modules.add(new CourseModule("clf-concepts", "CLF-C02", "Cloud Concepts", "☁️", "Understand the value proposition of the AWS Cloud.", "<h3>Cloud Concepts</h3><p>Focuses on cloud economics and architecture design principles.</p>", List.of()));
                modules.add(new CourseModule("clf-security", "CLF-C02", "Security and Compliance", "🛡️", "Understand the AWS Shared Responsibility Model.", "<h3>Security & Compliance</h3><p>Focuses on AWS IAM, compliance programs, and foundational security.</p>", List.of()));
                modules.add(new CourseModule("clf-tech", "CLF-C02", "Cloud Technology and Services", "💻", "Explore compute, networking, database, and storage services.", "<h3>Technology & Services</h3><p>An overview of core AWS services like EC2, S3, RDS, and VPC.</p>", List.of()));
                modules.add(new CourseModule("clf-billing", "CLF-C02", "Billing, Pricing, and Support", "💳", "Master AWS pricing models and billing tools.", "<h3>Billing & Pricing</h3><p>Understand the Total Cost of Ownership (TCO) and AWS Organizations.</p>", List.of()));

                // --- AIF-C01: AI Practitioner ---
                modules.add(new CourseModule("aif-fundamentals", "AIF-C01", "Fundamentals of AI and ML", "🧠", "Core concepts of Artificial Intelligence and Machine Learning.", "<h3>AI/ML Fundamentals</h3><p>Understanding models, training data, and inference.</p>", List.of()));
                modules.add(new CourseModule("aif-genai", "AIF-C01", "Fundamentals of Generative AI", "✨", "Explore LLMs, diffusion models, and prompt engineering.", "<h3>Generative AI</h3><p>How GenAI differs from traditional ML.</p>", List.of()));
                modules.add(new CourseModule("aif-models", "AIF-C01", "Applications of Foundation Models", "🏗️", "Use cases for existing foundation models.", "<h3>Foundation Models</h3><p>Applying models to text, image, and code generation.</p>", List.of()));
                modules.add(new CourseModule("aif-responsible", "AIF-C01", "Guidelines for Responsible AI", "⚖️", "Bias, fairness, explainability, and transparency.", "<h3>Responsible AI</h3><p>Ensuring AI models are built and deployed ethically.</p>", List.of()));
                modules.add(new CourseModule("aif-security", "AIF-C01", "Security, Compliance, and Governance", "🛡️", "Securing AI workloads and data privacy.", "<h3>AI Security</h3><p>Protecting training data and model endpoints.</p>", List.of()));

                // ==========================================
                // ASSOCIATE CERTIFICATIONS
                // ==========================================

                // --- SAA-C03: Solutions Architect ---
                modules.add(new CourseModule("saa-secure", "SAA-C03", "Design Secure Architectures", "🛡️", "Secure access, workloads, and data.", "<h3>Secure Architectures</h3><p>VPC security, IAM roles, and KMS encryption.</p>", List.of("a9__D53WsUs")));
                modules.add(new CourseModule("saa-resilient", "SAA-C03", "Design Resilient Architectures", "🏗️", "Scalable, loosely coupled, and fault-tolerant architectures.", "<h3>Resilient Architectures</h3><p>Auto Scaling, Route 53, and multi-AZ deployments.</p>", List.of()));
                modules.add(new CourseModule("saa-performing", "SAA-C03", "Design High-Performing Architectures", "⚡", "Optimize compute, storage, and networking.", "<h3>High Performance</h3><p>Elasticache, CloudFront, and optimized EC2 instance types.</p>", List.of()));
                modules.add(new CourseModule("saa-cost", "SAA-C03", "Design Cost-Optimized Architectures", "💰", "Maximize efficiency and minimize AWS spend.", "<h3>Cost Optimization</h3><p>Spot instances, S3 storage tiers, and AWS Budgets.</p>", List.of()));

                // --- DVA-C02: Developer ---
                modules.add(new CourseModule("dva-dev", "DVA-C02", "Development with AWS Services", "👨‍💻", "Code and interact with AWS APIs.", "<h3>AWS Development</h3><p>Lambda, API Gateway, DynamoDB, and the AWS SDK.</p>", List.of()));
                modules.add(new CourseModule("dva-security", "DVA-C02", "Security", "🔒", "Authenticate and authorize applications.", "<h3>Developer Security</h3><p>Cognito, Secrets Manager, and IAM permissions.</p>", List.of()));
                modules.add(new CourseModule("dva-deploy", "DVA-C02", "Deployment", "🚀", "CI/CD pipelines and infrastructure as code.", "<h3>Deployment</h3><p>Elastic Beanstalk, CodePipeline, and SAM.</p>", List.of()));
                modules.add(new CourseModule("dva-troubleshoot", "DVA-C02", "Troubleshooting and Optimization", "🐛", "Debug code and optimize performance.", "<h3>Troubleshooting</h3><p>X-Ray, CloudWatch Logs, and optimizing API calls.</p>", List.of()));

                // --- SOA-C02: SysOps Administrator ---
                modules.add(new CourseModule("soa-monitoring", "SOA-C02", "Monitoring, Logging, and Remediation", "📈", "Observe and respond to system events.", "<h3>Monitoring</h3><p>CloudWatch alarms, EventBridge, and automated remediation.</p>", List.of()));
                modules.add(new CourseModule("soa-reliability", "SOA-C02", "Reliability and Business Continuity", "🔄", "Implement backup and disaster recovery strategies.", "<h3>Reliability</h3><p>AWS Backup, multi-region deployments, and pilot light strategies.</p>", List.of()));
                modules.add(new CourseModule("soa-deployment", "SOA-C02", "Deployment, Provisioning, and Automation", "🚀", "Automate infrastructure rollout.", "<h3>Automation</h3><p>CloudFormation, Systems Manager, and OpsWorks.</p>", List.of()));
                modules.add(new CourseModule("soa-security", "SOA-C02", "Security and Compliance", "🔒", "Implement and manage security controls.", "<h3>Security</h3><p>Security Hub, GuardDuty, and AWS Config.</p>", List.of()));
                modules.add(new CourseModule("soa-networking", "SOA-C02", "Networking and Content Delivery", "🌐", "Manage VPCs, routing, and CDNs.", "<h3>Networking</h3><p>VPC Peering, Transit Gateway, and Route 53 routing policies.</p>", List.of()));
                modules.add(new CourseModule("soa-cost", "SOA-C02", "Cost and Performance Optimization", "📉", "Right-size resources and track billing.", "<h3>Cost & Performance</h3><p>Cost Explorer, Compute Optimizer, and Savings Plans.</p>", List.of()));

                // --- DEA-C01: Data Engineering ---
                modules.add(new CourseModule("dea-ingestion", "DEA-C01", "Data Ingestion and Transformation", "📥", "Build data pipelines and ETL workflows.", "<h3>Ingestion & ETL</h3><p>AWS Glue, Kinesis Data Streams, and Step Functions.</p>", List.of()));
                modules.add(new CourseModule("dea-store", "DEA-C01", "Data Store Management", "🗄️", "Manage data lakes and data warehouses.", "<h3>Data Stores</h3><p>Amazon S3 data lakes, Redshift, and DynamoDB.</p>", List.of()));
                modules.add(new CourseModule("dea-operations", "DEA-C01", "Data Operations and Support", "⚙️", "Maintain and monitor data systems.", "<h3>Data Ops</h3><p>Monitoring Glue jobs, handling failed pipelines, and scheduling.</p>", List.of()));
                modules.add(new CourseModule("dea-security", "DEA-C01", "Data Security and Governance", "🏛️", "Secure sensitive data at rest and in transit.", "<h3>Data Governance</h3><p>AWS Lake Formation, Macie, and KMS encryption.</p>", List.of()));

                // ==========================================
                // PROFESSIONAL CERTIFICATIONS
                // ==========================================

                // --- SAP-C02: Solutions Architect Professional ---
                modules.add(new CourseModule("sap-complex", "SAP-C02", "Design Solutions for Organizational Complexity", "🏢", "Multi-account and hybrid network strategies.", "<h3>Organizational Complexity</h3><p>AWS Organizations, Control Tower, and Direct Connect.</p>", List.of()));
                modules.add(new CourseModule("sap-new", "SAP-C02", "Design for New Solutions", "💡", "Architect enterprise-grade greenfield applications.", "<h3>New Solutions</h3><p>Microservices, serverless event-driven architectures.</p>", List.of()));
                modules.add(new CourseModule("sap-improve", "SAP-C02", "Continuous Improvement for Existing Solutions", "📈", "Refactor and optimize legacy systems.", "<h3>Continuous Improvement</h3><p>Strangler fig pattern, optimizing legacy databases.</p>", List.of()));
                modules.add(new CourseModule("sap-migration", "SAP-C02", "Accelerate Workload Migration", "🚀", "Move massive workloads to the cloud.", "<h3>Migration</h3><p>AWS Migration Hub, Application Discovery Service, and Snowball.</p>", List.of()));

                // --- DOP-C02: DevOps Engineer Professional ---
                modules.add(new CourseModule("dop-sdlc", "DOP-C02", "SDLC Automation", "🔄", "Automate the entire software development lifecycle.", "<h3>SDLC Automation</h3><p>CodeCommit, CodeBuild, CodeDeploy, and advanced pipelines.</p>", List.of()));
                modules.add(new CourseModule("dop-iac", "DOP-C02", "Configuration Management and IaC", "📜", "Manage infrastructure at scale.", "<h3>IaC</h3><p>CloudFormation StackSets, CDK, and Systems Manager Automation.</p>", List.of()));
                modules.add(new CourseModule("dop-resilient", "DOP-C02", "Resilient Cloud Solutions", "🏗️", "Design self-healing systems.", "<h3>Resilience</h3><p>Chaos engineering, highly available CI/CD, and ASG lifecycle hooks.</p>", List.of()));
                modules.add(new CourseModule("dop-monitoring", "DOP-C02", "Monitoring and Logging", "📊", "Enterprise-wide observability.", "<h3>Observability</h3><p>CloudWatch logs integration, Elasticsearch, and centralized logging.</p>", List.of()));
                modules.add(new CourseModule("dop-incident", "DOP-C02", "Incident and Event Response", "🚨", "Automate responses to security and operational events.", "<h3>Incident Response</h3><p>EventBridge rules triggering Lambda for automated remediation.</p>", List.of()));
                modules.add(new CourseModule("dop-security", "DOP-C02", "Security and Compliance", "🛡️", "Embed security into the CI/CD pipeline (DevSecOps).", "<h3>DevSecOps</h3><p>Automated security scanning, Config rules, and IAM policy boundaries.</p>", List.of()));

                // --- AIP-C01: Generative AI Developer ---
                modules.add(new CourseModule("aip-integration", "AIP-C01", "Foundation Model Integration & Data Management", "🔗", "Integrate models and manage training data.", "<h3>FM Integration</h3><p>Data prep for RAG and fine-tuning.</p>", List.of()));
                modules.add(new CourseModule("aip-implement", "AIP-C01", "Implementation and Integration", "🛠️", "Deploy models into production applications.", "<h3>Implementation</h3><p>Amazon Bedrock APIs, LangChain, and vector databases.</p>", List.of()));
                modules.add(new CourseModule("aip-safety", "AIP-C01", "AI Safety, Security, and Governance", "🔒", "Ensure GenAI applications are safe and compliant.", "<h3>Safety & Governance</h3><p>Guardrails for Amazon Bedrock and preventing prompt injection.</p>", List.of()));
                modules.add(new CourseModule("aip-optimize", "AIP-C01", "Operational Efficiency and Optimization", "⚡", "Optimize GenAI latency and cost.", "<h3>Optimization</h3><p>Provisioned throughput and evaluating model selection.</p>", List.of()));
                modules.add(new CourseModule("aip-test", "AIP-C01", "Testing, Validation, and Troubleshooting", "🧪", "Evaluate model hallucinations and performance.", "<h3>Testing</h3><p>Model evaluation techniques and hallucination detection.</p>", List.of()));

                // ==========================================
                // SPECIALTY CERTIFICATIONS
                // ==========================================

                // --- MLS-C01: Machine Learning ---
                modules.add(new CourseModule("mls-data", "MLS-C01", "Data Engineering", "📊", "Prepare data for ML models.", "<h3>Data Engineering</h3><p>Amazon SageMaker Data Wrangler, EMR, and Glue.</p>", List.of()));
                modules.add(new CourseModule("mls-eda", "MLS-C01", "Exploratory Data Analysis", "🔍", "Analyze and visualize datasets.", "<h3>EDA</h3><p>Handling missing values, feature engineering, and outliers.</p>", List.of()));
                modules.add(new CourseModule("mls-modeling", "MLS-C01", "Modeling", "🧠", "Train and evaluate ML algorithms.", "<h3>Modeling</h3><p>Built-in SageMaker algorithms, hyperparameter tuning, and model metrics.</p>", List.of()));
                modules.add(new CourseModule("mls-mlops", "MLS-C01", "ML Implementation and Operations", "⚙️", "Deploy and manage models in production.", "<h3>MLOps</h3><p>SageMaker endpoints, model drift, and A/B testing.</p>", List.of()));

                // --- SCS-C02: Security ---
                modules.add(new CourseModule("scs-threat", "SCS-C02", "Threat Detection and Incident Response", "🎯", "Identify and respond to security threats.", "<h3>Threat Detection</h3><p>Amazon GuardDuty, Macie, and automated incident response.</p>", List.of()));
                modules.add(new CourseModule("scs-log", "SCS-C02", "Security Logging and Monitoring", "👁️", "Centralize and analyze security logs.", "<h3>Logging</h3><p>CloudTrail, VPC Flow Logs, and Athena queries for security.</p>", List.of()));
                modules.add(new CourseModule("scs-infra", "SCS-C02", "Infrastructure Security", "🏰", "Secure networks and compute resources.", "<h3>Infrastructure Security</h3><p>WAF, Shield, Network Firewalls, and endpoint security.</p>", List.of()));
                modules.add(new CourseModule("scs-iam", "SCS-C02", "Identity and Access Management", "🔑", "Design enterprise identity solutions.", "<h3>IAM</h3><p>Cross-account roles, identity federation, and Cognito.</p>", List.of()));
                modules.add(new CourseModule("scs-data", "SCS-C02", "Data Protection", "💾", "Encrypt data at rest and in transit.", "<h3>Data Protection</h3><p>Advanced KMS key policies, CloudHSM, and certificate management.</p>", List.of()));
                modules.add(new CourseModule("scs-gov", "SCS-C02", "Management and Security Governance", "📜", "Enforce security standards across organizations.", "<h3>Governance</h3><p>AWS Organizations SCPs, Control Tower, and AWS Config conformance packs.</p>", List.of()));

                // --- ANS-C01: Advanced Networking ---
                modules.add(new CourseModule("ans-design", "ANS-C01", "Network Design", "📐", "Architect complex hybrid and multi-cloud networks.", "<h3>Network Design</h3><p>Transit Gateway architectures, Direct Connect topologies, and DNS strategies.</p>", List.of()));
                modules.add(new CourseModule("ans-implement", "ANS-C01", "Network Implementation", "🔌", "Deploy advanced networking components.", "<h3>Implementation</h3><p>VPC configuration, Route 53 Resolver, and load balancing at scale.</p>", List.of()));
                modules.add(new CourseModule("ans-manage", "ANS-C01", "Network Management and Operation", "🎛️", "Monitor and troubleshoot network health.", "<h3>Management</h3><p>Network Access Analyzer, Reachability Analyzer, and Traffic Mirroring.</p>", List.of()));
                modules.add(new CourseModule("ans-security", "ANS-C01", "Network Security, Compliance, and Governance", "🔐", "Secure traffic routing and borders.", "<h3>Network Security</h3><p>AWS Network Firewall, WAF, and secure hybrid connections.</p>", List.of()));

                // Save all 56 modules to the database in one massive batch!
                repository.saveAll(modules);
                System.out.println("Database Seeding Complete! Over 50 Course Modules locked and loaded.");
            }
        };
    }
}