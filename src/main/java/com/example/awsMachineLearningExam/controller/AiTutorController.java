package com.example.awsMachineLearningExam.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class AiTutorController {

    @Value("${aws.bedrock.region:us-east-1}")
    private String bedrockRegion;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @PostMapping("/tutor")
    public ResponseEntity<?> askTutor(@RequestBody Map<String, String> payload) {
        try {
            String questionText = payload.getOrDefault("questionText", "");
            String userAnswer = payload.getOrDefault("userAnswer", "");
            String correctAnswer = payload.getOrDefault("correctAnswer", "");
            String explanation = payload.getOrDefault("explanation", "");
            String certCode = payload.getOrDefault("certCode", "AWS");

            String prompt = buildPrompt(questionText, userAnswer, correctAnswer, explanation, certCode);

            // Call Bedrock via AWS SDK (using runtime invoke)
            String response = callBedrock(prompt);

            return ResponseEntity.ok(Map.of("response", response));

        } catch (Exception e) {
            System.out.println("AI Tutor Error: " + e.getMessage());
            return ResponseEntity.ok(Map.of("response",
                "I'm having trouble connecting right now. Here's a tip: Review the explanation above and focus on WHY the correct answer is right, not just WHAT it is. Understanding the underlying AWS service behavior is key."));
        }
    }

    private String buildPrompt(String questionText, String userAnswer, String correctAnswer, String explanation, String certCode) {
        return String.format("""
            You are an AWS certification tutor helping a student who got a question wrong on the %s exam.
            Be concise, friendly, and educational. Use 3-5 sentences max.
            
            QUESTION: %s
            
            STUDENT'S WRONG ANSWER: %s
            
            CORRECT ANSWER: %s
            
            EXISTING EXPLANATION: %s
            
            Explain WHY their answer is wrong and WHY the correct answer is right.
            Use a real-world analogy if helpful. Don't repeat the explanation verbatim - add new insight.
            """, certCode, questionText, userAnswer, correctAnswer, explanation);
    }

    private String callBedrock(String prompt) throws Exception {
        // Use AWS SDK v2 Bedrock Runtime
        software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient client =
            software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient.builder()
                .region(software.amazon.awssdk.regions.Region.of(bedrockRegion))
                .build();

        String requestBody = String.format("""
            {
                "anthropic_version": "bedrock-2023-05-31",
                "max_tokens": 300,
                "messages": [
                    {
                        "role": "user",
                        "content": "%s"
                    }
                ]
            }
            """, prompt.replace("\"", "\\\"").replace("\n", "\\n"));

        software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest request =
            software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest.builder()
                .modelId("anthropic.claude-3-haiku-20240307-v1:0")
                .contentType("application/json")
                .body(software.amazon.awssdk.core.SdkBytes.fromUtf8String(requestBody))
                .build();

        software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse response =
            client.invokeModel(request);

        String responseBody = response.body().asUtf8String();

        // Parse the response JSON to extract the text
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(responseBody);
        return root.get("content").get(0).get("text").asText();
    }
}
