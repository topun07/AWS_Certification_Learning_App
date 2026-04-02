package com.example.awsMachineLearningExam.service;

import com.example.awsMachineLearningExam.model.User;
import com.example.awsMachineLearningExam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User recordStudySession(String username) {
        // 1. Find the user (Without Optional)
        User user = userRepository.findByUsername(username);

        // Manually check if the user exists
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        LocalDate today = LocalDate.now();
        LocalDate lastDate = user.getLastStudyDate();

        // 2. SCENARIO 1: First time studying ever, or they broke their streak
        if (lastDate == null || lastDate.isBefore(today.minusDays(1))) {
            user.setCurrentStreak(1);
            user.setLastStudyDate(today);
        }
        // 3. SCENARIO 2: They studied yesterday. Streak continues!
        else if (lastDate.isEqual(today.minusDays(1))) {
            user.setCurrentStreak(user.getCurrentStreak() + 1);
            user.setLastStudyDate(today);
        }
        // SCENARIO 3: They already studied today. Do nothing.

        // 4. Save and return the updated user
        return userRepository.save(user);
    }
}