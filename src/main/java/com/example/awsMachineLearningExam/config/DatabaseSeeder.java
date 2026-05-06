package com.example.awsMachineLearningExam.config; // Change this to match your folder structure if needed

import com.example.awsMachineLearningExam.model.Module;
import com.example.awsMachineLearningExam.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private com.example.awsMachineLearningExam.repository.ReviewRepository reviewRepository;

    private final ModuleRepository moduleRepository;

    public DatabaseSeeder(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Only inject this data if the table is completely empty!
        if (moduleRepository.count() == 0) {

            Module testModule = new Module(
                    "SCS-C02", // This matches the Security Specialty cert code!
                    "IAM Security & Identity Basics",
                    "Master the core concepts of AWS Identity and Access Management, including Policies, Roles, and Federation.",
                    "🛡️", // The emoji icon for the UI
                    List.of("y_Z_X4_w5Jg", "vz_p1tI7eEI"), // Actual YouTube Video IDs
                    "<h1>IAM Study Guide</h1><br><p>Identity and Access Management (IAM) is the heart of AWS security. It is a <b>global</b> service, meaning it is not restricted to a single region.</p><ul><li><b>Users:</b> End users or service accounts.</li><li><b>Groups:</b> A collection of users under one set of permissions.</li><li><b>Roles:</b> Temporary credentials assumed by trusted entities.</li></ul>"
            );

            if (reviewRepository.count() == 0) {
                reviewRepository.save(new com.example.awsMachineLearningExam.model.Review("Sarah J.", 5, "This platform is incredible. I passed the SAA-C03 on my first try!"));
                reviewRepository.save(new com.example.awsMachineLearningExam.model.Review("Michael T.", 4, "The radar chart knew exactly what my weak points were. Highly recommend."));
                reviewRepository.save(new com.example.awsMachineLearningExam.model.Review("David W.", 5, "Way better than reading a textbook. The flashcard matrix saved me hours."));
            }

            moduleRepository.save(testModule);
            System.out.println("✅ Knowledge Forge Seeded with Test Data!");
        }
    }
}