package com.barberfind.api.service;

import com.barberfind.api.domain.BarberEntity;
import com.barberfind.api.domain.User;
import com.barberfind.api.domain.UserRole;
import com.barberfind.api.dto.RegisterBarberRequest;
import com.barberfind.api.dto.RegisterBarberResponse;
import com.barberfind.api.repository.BarberRepository;
import com.barberfind.api.repository.UserRepository;
import com.barberfind.api.shared.util.Cuid;
import com.barberfind.api.shared.util.Normalizers; // <- ajuste se o pacote/nome for diferente
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterBarberService {

    private final UserRepository userRepository;
    private final BarberRepository barberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterBarberResponse register(RegisterBarberRequest req) {
        final String email = Normalizers.normalizeEmail(req.email());
        final String phone = Normalizers.normalizePhone(req.phone());

        // 1) email único
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email_already_in_use");
        }

        // 2) phone único
        if (userRepository.existsByPhone(phone)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "phone_already_in_use");
        }

        // 3) cria user (role BARBER)
        var user = new User();
        user.setId(Cuid.generate());
        user.setName(req.name());
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(UserRole.BARBER);
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // 4) cria barber vinculado ao user
        var barber = new BarberEntity();
        barber.setId(Cuid.generate());
        barber.setUser(user);
        barber.setName(req.name()); // ok manter duplicado se teu schema pede
        barber.setBio(req.bio());
        barber.setYearsExperience(req.yearsExperience());
        barber.setRating(BigDecimal.ZERO);
        barber.setCreatedAt(LocalDateTime.now());
        barber.setUpdatedAt(LocalDateTime.now());
        barberRepository.save(barber);

        return new RegisterBarberResponse(
                user.getId(),
                barber.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}