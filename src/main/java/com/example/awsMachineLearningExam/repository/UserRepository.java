package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // This import is key!

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Change this to return Optional so the .isPresent() logic works
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}