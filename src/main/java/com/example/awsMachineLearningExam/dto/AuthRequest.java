package com.example.awsMachineLearningExam.dto;

import org.jspecify.annotations.Nullable;

public class AuthRequest {
    public String username;
    public String email;
    public String passwordhash;
    public String fullName; // Optional for login
}