package com.barberfind.api.service;

import com.barberfind.api.domain.UserRole;
import com.barberfind.api.dto.UpdateUserRequest;
import com.barberfind.api.repository.BarberRepository;
import com.barberfind.api.repository.UserRepository;
import com.barberfind.api.shared.util.Normalizers;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UpdateUserService {

    private final UserRepository userRepository;
    private final BarberRepository barberRepository;

    @Transactional
    public void update(String targetUserId, String authenticatedUserId, UpdateUserRequest req) {

        // Só pode atualizar a si mesmo
        if (!targetUserId.equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden");
        }

        var user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user_not_found"));

        // Campos comuns — só atualiza se vier não-nulo
        if (req.name() != null)        user.setName(req.name());
        if (req.birthDate() != null)   user.setBirthDate(req.birthDate());
        if (req.hairType() != null)    user.setHairType(req.hairType());
        if (req.hairTexture() != null) user.setHairTexture(req.hairTexture());
        if (req.hasBeard() != null)    user.setHasBeard(req.hasBeard());

        if (req.phone() != null) {
            String normalized = Normalizers.normalizePhone(req.phone());
            // verifica conflito de phone com outro usuário
            if (userRepository.existsByPhone(normalized) &&
                    !normalized.equals(user.getPhone())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "phone_already_in_use");
            }
            user.setPhone(normalized);
        }

        userRepository.save(user);

        // Campos exclusivos de barbeiro
        if (user.getRole() == UserRole.BARBER) {
            var barber = barberRepository.findByUserId(targetUserId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barber_profile_not_found"));

            if (req.bio() != null)             barber.setBio(req.bio());
            if (req.yearsExperience() != null) barber.setYearsExperience(req.yearsExperience());
            if (req.name() != null)            barber.setName(req.name()); // mantém sincronizado

            barberRepository.save(barber);
        }
    }
}