package com.barberfind.api.service;

import com.barberfind.api.domain.UserPreferences;
import com.barberfind.api.dto.UserPreferencesRequest;
import com.barberfind.api.dto.UserPreferencesResponse;
import com.barberfind.api.repository.UserPreferencesRepository;
import com.barberfind.api.repository.UserRepository;
import com.barberfind.api.shared.util.Cuid;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserPreferencesService {

    private final UserPreferencesRepository preferencesRepository;
    private final UserRepository userRepository;

    // ── Get ───────────────────────────────────────────────────────────────────

    public UserPreferencesResponse get(String targetUserId, String authenticatedUserId) {
        checkOwnership(targetUserId, authenticatedUserId);

        var prefs = preferencesRepository.findByUserId(targetUserId)
                .orElseGet(() -> defaultPreferences(targetUserId));

        return toResponse(prefs);
    }

    // ── Update (upsert) ───────────────────────────────────────────────────────

    @Transactional
    public UserPreferencesResponse update(String targetUserId, String authenticatedUserId,
                                          UserPreferencesRequest req) {
        checkOwnership(targetUserId, authenticatedUserId);

        var prefs = preferencesRepository.findByUserId(targetUserId)
                .orElseGet(() -> {
                    var user = userRepository.findById(targetUserId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user_not_found"));
                    return UserPreferences.builder()
                            .id(Cuid.generate())
                            .user(user)
                            .build();
                });

        if (req.theme() != null)    prefs.setTheme(req.theme());
        if (req.language() != null) prefs.setLanguage(req.language());

        preferencesRepository.save(prefs);
        return toResponse(prefs);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private UserPreferences defaultPreferences(String userId) {
        // Retorna um objeto com defaults sem persistir (usuário ainda não configurou)
        return UserPreferences.builder()
                .id("_default")
                .theme("LIGHT")
                .language("pt-BR")
                .build();
    }

    private UserPreferencesResponse toResponse(UserPreferences p) {
        return new UserPreferencesResponse(
                p.getId(),
                p.getTheme(),
                p.getLanguage(),
                p.getUpdatedAt()
        );
    }

    private void checkOwnership(String targetUserId, String authenticatedUserId) {
        if (!targetUserId.equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden");
        }
    }
}