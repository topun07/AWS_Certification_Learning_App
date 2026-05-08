package com.example.awsMachineLearningExam.controller;

import com.example.awsMachineLearningExam.model.StudyMaterial;
import com.example.awsMachineLearningExam.repository.StudyMaterialRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/study")
@CrossOrigin(origins = "http://localhost:5173")
public class StudyMaterialController {

    private final StudyMaterialRepository studyRepository;

    public StudyMaterialController(StudyMaterialRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    // 1. READ: Fetch a specific document (WITH X-RAY LOGGING)
    @GetMapping("/{certCode}/{domain}")
    public ResponseEntity<?> getStudyMaterial(@PathVariable String certCode, @PathVariable String domain) {
        // 🚨 This will print to your Spring Boot terminal!
        System.out.println("📡 VUE REQUESTED -> Cert: [" + certCode + "] | Domain: [" + domain + "]");

        return studyRepository.findByCertCodeAndDomain(certCode, domain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🚨 NEW: The Database Dump Endpoint
    @GetMapping("/all")
    public ResponseEntity<?> getAllStudyMaterials() {
        return ResponseEntity.ok(studyRepository.findAll());
    }

    // 2. CREATE: The Master CSV Importer (supports multi-line markdown in quoted fields)
    @PostMapping("/upload")
    public ResponseEntity<?> uploadStudyCSV(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("CSV file is empty.");

        List<StudyMaterial> materials = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // Read the entire file content
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }

            // Parse CSV records (handles multi-line quoted fields)
            List<String[]> records = parseCSV(fileContent.toString());

            // Skip header row, process the rest
            for (int i = 1; i < records.size(); i++) {
                String[] columns = records.get(i);
                if (columns.length >= 4) {
                    StudyMaterial sm = new StudyMaterial();
                    sm.setCertCode(columns[0].trim());
                    sm.setDomain(columns[1].trim());
                    sm.setTitle(columns[2].trim());
                    sm.setContent(columns[3].trim());
                    materials.add(sm);
                }
            }

            studyRepository.saveAll(materials);
            return ResponseEntity.ok(Map.of("message", "Study Matrix Injected!", "count", materials.size()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Pipeline Failure: " + e.getMessage());
        }
    }

    // RFC 4180 compliant CSV parser that handles multi-line quoted fields
    private List<String[]> parseCSV(String content) {
        List<String[]> records = new ArrayList<>();
        List<String> currentRecord = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (inQuotes) {
                if (c == '"') {
                    // Check for escaped quote ""
                    if (i + 1 < content.length() && content.charAt(i + 1) == '"') {
                        currentField.append('"');
                        i++; // skip next quote
                    } else {
                        inQuotes = false; // end of quoted field
                    }
                } else {
                    currentField.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    currentRecord.add(currentField.toString());
                    currentField = new StringBuilder();
                } else if (c == '\n') {
                    currentRecord.add(currentField.toString());
                    currentField = new StringBuilder();
                    if (!currentRecord.isEmpty() && currentRecord.stream().anyMatch(f -> !f.isEmpty())) {
                        records.add(currentRecord.toArray(new String[0]));
                    }
                    currentRecord = new ArrayList<>();
                } else if (c != '\r') {
                    currentField.append(c);
                }
            }
        }

        // Add last record if exists
        if (currentField.length() > 0 || !currentRecord.isEmpty()) {
            currentRecord.add(currentField.toString());
            if (currentRecord.stream().anyMatch(f -> !f.isEmpty())) {
                records.add(currentRecord.toArray(new String[0]));
            }
        }

        return records;
    }

    // 3. CREATE: Direct JSON upload for a single study document
    @PostMapping("/create")
    public ResponseEntity<?> createStudyMaterial(@RequestBody StudyMaterial material) {
        StudyMaterial saved = studyRepository.save(material);
        return ResponseEntity.ok(Map.of("message", "Study material saved!", "id", saved.getId()));
    }

    private String cleanCSV(String input) {
        if (input == null) return "";
        return input.replaceAll("^\"|\"$", "").trim();
    }
}