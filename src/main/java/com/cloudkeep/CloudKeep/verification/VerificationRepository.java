package com.cloudkeep.CloudKeep.verification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
    Optional<Verification> findByCode(String code);
    Optional<Verification> findByCodeAndUserEmail(String code, String email);
}
