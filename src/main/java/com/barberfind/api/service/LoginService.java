package com.barberfind.api.service;

import com.barberfind.api.dto.LoginRequest;
import com.barberfind.api.dto.LoginResponse;
import com.barberfind.api.repository.UserRepository;
import com.barberfind.api.security.JwtProvider;
import com.barberfind.api.shared.util.Normalizers;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest req) {
        final String email = Normalizers.normalizeEmail(req.email());

        var user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid_credentials"));

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid_credentials");
        }

        String token = jwtProvider.generateToken(user.getId(), user.getRole().name());

        return new LoginResponse(
                token,
                user.getId(),
                user.getRole().name(),
                user.getName(),
                user.getEmail()
        );
    }
}