package com.example.awsMachineLearningExam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AwsExamWithSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(AwsExamWithSpringBootApplication.class, args);
    }

}
