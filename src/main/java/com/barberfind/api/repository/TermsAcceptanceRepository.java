package com.barberfind.api.repository;

import com.barberfind.api.domain.TermsAcceptance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermsAcceptanceRepository extends JpaRepository<TermsAcceptance, String> {

    Optional<TermsAcceptance> findTopByUserIdOrderByAcceptedAtDesc(String userId);
}