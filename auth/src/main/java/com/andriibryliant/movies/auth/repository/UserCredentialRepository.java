package com.andriibryliant.movies.auth.repository;

import com.andriibryliant.movies.auth.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    Optional<UserCredential> findByEmail(String email);
}
