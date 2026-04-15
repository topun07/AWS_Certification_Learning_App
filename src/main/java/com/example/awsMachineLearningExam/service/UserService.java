package com.example.awsMachineLearningExam.service;

import com.example.awsMachineLearningExam.model.AppUser;
import com.example.awsMachineLearningExam.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService {

    private final AppUserRepository userRepository;

    // Modern Constructor Injection (replaces @Autowired)
    public UserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AppUser recordStudySession(String username) {
        // 1. Find the user safely using .orElseThrow() instead of manual null checks
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found in Jedi Archives: " + username));

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

    public AppUser awardXp(String username, int correctCount, int totalQuestions) {
        // 1. Find the user safely
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found in Jedi Archives: " + username));

        // 2. Calculate the base XP (10 per correct answer)
        int earnedXp = correctCount * 10;

        // 3. Add the Flawless Victory Bonus!
        if (correctCount == totalQuestions && totalQuestions > 0) {
            earnedXp += 500;
        }

        // 4. Add it to their lifetime total and save (using getXp() instead of getTotalXp())
        user.setXp(user.getXp() + earnedXp);

        return userRepository.save(user);
    }
}