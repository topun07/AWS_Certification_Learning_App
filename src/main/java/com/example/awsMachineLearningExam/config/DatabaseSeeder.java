package com.example.awsMachineLearningExam.config;

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

        // 🚨 INDEPENDENT CHECK 1: Seed the Modules
        if (moduleRepository.count() == 0) {

            Module testModule = new Module(
                    "SCS-C02",
                    "IAM Security & Identity Basics",
                    "Master the core concepts of AWS Identity and Access Management, including Policies, Roles, and Federation.",
                    "🛡️",
                    List.of("y_Z_X4_w5Jg", "vz_p1tI7eEI"),
                    "<h1>IAM Study Guide</h1><br><p>Identity and Access Management (IAM) is the heart of AWS security. It is a <b>global</b> service, meaning it is not restricted to a single region.</p><ul><li><b>Users:</b> End users or service accounts.</li><li><b>Groups:</b> A collection of users under one set of permissions.</li><li><b>Roles:</b> Temporary credentials assumed by trusted entities.</li></ul>"
            );

            moduleRepository.save(testModule);

        }

        // 🚨 INDEPENDENT CHECK 2: Seed the Reviews
        if (reviewRepository.count() == 0) {
            reviewRepository.save(new com.example.awsMachineLearningExam.model.Review("Sarah J.", 5, "This platform is incredible. I passed the SAA-C03 on my first try!", "Solutions Architect"));
            reviewRepository.save(new com.example.awsMachineLearningExam.model.Review("Michael T.", 4, "The radar chart knew exactly what my weak points were. Highly recommend.", "Data Engineer"));
            reviewRepository.save(new com.example.awsMachineLearningExam.model.Review("David W.", 5, "Way better than reading a textbook. The flashcard matrix saved me hours.", "Cloud Practitioner"));
        }
    }
}