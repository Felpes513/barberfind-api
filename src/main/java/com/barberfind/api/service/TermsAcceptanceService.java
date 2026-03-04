package com.barberfind.api.service;

import com.barberfind.api.domain.TermsAcceptance;
import com.barberfind.api.dto.AcceptTermsRequest;
import com.barberfind.api.repository.TermsAcceptanceRepository;
import com.barberfind.api.repository.UserRepository;
import com.barberfind.api.shared.util.Cuid;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TermsAcceptanceService {

    private final TermsAcceptanceRepository termsRepository;
    private final UserRepository userRepository;

    @Transactional
    public Map<String, Object> accept(String targetUserId, String authenticatedUserId,
                                      AcceptTermsRequest req) {

        if (!targetUserId.equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden");
        }

        // O frontend deve mandar accepted = true. Se false, rejeitamos.
        if (Boolean.FALSE.equals(req.accepted())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "terms_must_be_accepted");
        }

        var user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user_not_found"));

        // Verifica se essa versão já foi aceita (evita duplicatas)
        boolean alreadyAccepted = termsRepository
                .findTopByUserIdOrderByAcceptedAtDesc(targetUserId)
                .map(t -> t.getTermsVersion().equals(req.termsVersion()))
                .orElse(false);

        if (alreadyAccepted) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "terms_version_already_accepted");
        }

        var acceptance = TermsAcceptance.builder()
                .id(Cuid.generate())
                .user(user)
                .termsVersion(req.termsVersion())
                .build();

        termsRepository.save(acceptance);

        return Map.of(
                "message", "terms_accepted",
                "termsVersion", acceptance.getTermsVersion(),
                "acceptedAt", acceptance.getAcceptedAt()
        );
    }
}