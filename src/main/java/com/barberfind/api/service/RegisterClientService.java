package com.barberfind.api.service;

import com.barberfind.api.domain.User;
import com.barberfind.api.domain.UserRole;
import com.barberfind.api.dto.RegisterClientRequest;
import com.barberfind.api.repository.UserRepository;
import com.barberfind.api.shared.util.Cuid;
import com.barberfind.api.shared.util.Normalizers; // <- ajuste se o pacote/nome for diferente
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
        final String email = Normalizers.normalizeEmail(req.email());
        final String phone = Normalizers.normalizePhone(req.phone());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            // 409
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email_already_in_use");
        }

        // se você já criou existsByPhone no repo, use:
        if (userRepository.existsByPhone(phone)) {
            // 409
            throw new ResponseStatusException(HttpStatus.CONFLICT, "phone_already_in_use");
        }

        User u = new User();
        u.setId(Cuid.generate());
        u.setRole(UserRole.CLIENT);
        u.setName(req.name());
        u.setEmail(email);
        u.setPhone(phone);

        u.setPasswordHash(passwordEncoder.encode(req.password()));
        u.setBirthDate(req.birthDate());
        u.setHairType(req.hairType());
        u.setHairTexture(req.hairTexture());
        u.setHasBeard(req.hasBeard());

        return userRepository.save(u);
    }
}