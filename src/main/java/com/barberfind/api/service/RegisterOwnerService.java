package com.barberfind.api.service;

import com.barberfind.api.domain.User;
import com.barberfind.api.domain.UserRole;
import com.barberfind.api.dto.RegisterOwnerRequest;
import com.barberfind.api.dto.RegisterOwnerResponse;
import com.barberfind.api.repository.UserRepository;
import com.barberfind.api.shared.util.Cuid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterOwnerService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterOwnerResponse register(RegisterOwnerRequest req) {
        String email = req.email().trim().toLowerCase();
        String phone = req.phone().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email_already_in_use");
        }
        if (userRepository.existsByPhone(phone)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "phone_already_in_use");
        }

        var u = new User();
        u.setId(Cuid.generate());
        u.setRole(UserRole.OWNER);
        u.setName(req.name());
        u.setEmail(email);
        u.setPhone(phone);
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setCreatedAt(LocalDateTime.now());
        u.setUpdatedAt(LocalDateTime.now());

        userRepository.save(u);

        return new RegisterOwnerResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getRole().name()
        );
    }
}