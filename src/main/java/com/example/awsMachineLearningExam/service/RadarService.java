package com.example.awsMachineLearningExam.service;

import com.example.awsMachineLearningExam.dto.RadarScoreResponseDTO;
import com.example.awsMachineLearningExam.model.CategoryMastery;
import com.example.awsMachineLearningExam.repository.CategoryMasteryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RadarService {

    // ✅ FIX: We removed @Autowired. The variable is now 'final'.
    private final CategoryMasteryRepository masteryRepo;

    // ✅ FIX: Constructor Injection (This makes IntelliJ happy and your app more secure)
    public RadarService(CategoryMasteryRepository masteryRepo) {
        this.masteryRepo = masteryRepo;
    }

    private static final Map<String, List<String>> CERT_DOMAINS = new HashMap<>();

    static {
        CERT_DOMAINS.put("CLF-C02", List.of("Cloud Concepts", "Security & Compliance", "Cloud Tech & Services", "Billing & Pricing"));
        CERT_DOMAINS.put("SOA-C02", List.of("Monitoring & Reporting", "High Availability", "Deployment & Provisioning", "Storage & Data", "Security & Compliance", "Networking"));
        CERT_DOMAINS.put("DVA-C02", List.of("Development with AWS", "Security", "Deployment", "Troubleshooting"));
        CERT_DOMAINS.put("SAA-C03", List.of("Secure Architectures", "Resilient Architectures", "High-Performing", "Cost-Optimized"));
        CERT_DOMAINS.put("SAP-C02", List.of("Design for Complexity", "New Solutions", "Migration Planning", "Cost Control", "Continuous Improvement"));
        CERT_DOMAINS.put("DOP-C02", List.of("SDLC Automation", "IaC & Config", "Monitoring & Logging", "Policies & Standards", "Incident Response", "High Availability"));
        CERT_DOMAINS.put("DEA-C01", List.of("Data Ingestion", "Data Store Management", "Data Operations", "Security & Governance"));
        CERT_DOMAINS.put("MLA-C01", List.of("Data Preparation", "ML Architecture", "Model Training", "Deployment & Ops", "Security"));
        CERT_DOMAINS.put("AIP-C01", List.of("AI/ML Fundamentals", "Generative AI", "Foundation Models", "Responsible AI"));
        CERT_DOMAINS.put("SCS-C02", List.of("Threat Detection", "Logging & Monitoring", "Infrastructure Security", "IAM", "Data Protection"));
        CERT_DOMAINS.put("ANS-C01", List.of("Network Design", "Network Implementation", "Network Management", "Network Security"));
        CERT_DOMAINS.put("AIF-C01", List.of("Fundamentals of AI & ML", "Fundamentals of Generative AI", "Applications of Foundation Models", "Responsible AI & Security"));
    }

    public RadarScoreResponseDTO calculateUserProficiency(Long userId, String certCode) {

        List<Integer> calculatedScores = new ArrayList<>();

        List<String> targetDomains = CERT_DOMAINS.getOrDefault(certCode, CERT_DOMAINS.get("CLF-C02"));

        for (String domain : targetDomains) {

            // ✅ FIX: This now matches the updated Repository method perfectly
            Optional<CategoryMastery> masteryRecord = masteryRepo.findByUserIdAndCategory(userId, domain);
            if (masteryRecord.isPresent()) {
                // 🚨 ACTION REQUIRED: Open CategoryMastery.java to see what your score variable is called.
                int score = masteryRecord.get().getMasteryPercentage();
                calculatedScores.add(score);
            } else {
                calculatedScores.add(0);
            }
        }

        return new RadarScoreResponseDTO(certCode, calculatedScores);
    }
}