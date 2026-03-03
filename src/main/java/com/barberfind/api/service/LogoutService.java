package com.barberfind.api.service;

import com.barberfind.api.domain.RevokedToken;
import com.barberfind.api.repository.RevokedTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class LogoutService {

    private final RevokedTokenRepository revokedTokenRepository;

    public LogoutService(RevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }

    public void revokeToken(String token) {
        if (!revokedTokenRepository.existsByToken(token)) {
            revokedTokenRepository.save(new RevokedToken(token));
        }
    }

    public boolean isRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }
}