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

        if (user.getCurrentStreak() == null) {
            user.setCurrentStreak(0);
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

    public User awardXp(String username, int correctCount, int totalQuestions) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Safety net for old ghost accounts!
        if (user.getTotalXp() == null) {
            user.setTotalXp(0);
        }

        // Calculate the base XP (10 per correct answer)
        int earnedXp = correctCount * 10;

        // Add the Flawless Victory Bonus!
        if (correctCount == totalQuestions && totalQuestions > 0) {
            earnedXp += 500;
        }

        // Add it to their lifetime total and save
        user.setTotalXp(user.getTotalXp() + earnedXp);

        return userRepository.save(user);
    }
}