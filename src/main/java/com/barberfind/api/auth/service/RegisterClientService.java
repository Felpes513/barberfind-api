package com.barberfind.api.auth.service;

import com.barberfind.api.auth.dto.RegisterClientRequest;
import com.barberfind.api.shared.util.Cuid;
import com.barberfind.api.users.domain.User;
import com.barberfind.api.users.repository.UserRepository;
import com.barberfind.api.users.domain.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RegisterClientService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterClientService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterClientRequest req) {
        String email = req.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email_already_in_use");
        }

        User u = new User();
        u.setId(Cuid.generate());
        u.setRole(UserRole.CLIENT);
        u.setName(req.name());
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setPhone(req.phone());
        u.setBirthDate(req.birthDate());
        u.setHairType(req.hairType());
        u.setHairTexture(req.hairTexture());
        u.setHasBeard(req.hasBeard());

        return userRepository.save(u);
    }
}