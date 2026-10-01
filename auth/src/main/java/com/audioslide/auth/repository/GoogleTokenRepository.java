package com.audioslide.auth.repository;

import com.audioslide.auth.entity.GoogleToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleTokenRepository extends JpaRepository<GoogleToken, Long> {
    Optional<GoogleToken> findByUser_Id(Long userId);
}
