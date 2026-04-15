package com.example.awsMachineLearningExam.repository;

import com.example.awsMachineLearningExam.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    // Optional prevents null-pointer exceptions if the user doesn't exist
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}