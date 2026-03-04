package com.barberfind.api.repository;

import com.barberfind.api.domain.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPreferencesRepository extends JpaRepository<UserPreferences, String> {

    Optional<UserPreferences> findByUserId(String userId);
}