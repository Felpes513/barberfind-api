package com.barberfind.api.service;

import com.barberfind.api.domain.UserPaymentMethod;
import com.barberfind.api.dto.PaymentMethodDtos.*;
import com.barberfind.api.repository.UserPaymentMethodRepository;
import com.barberfind.api.repository.UserRepository;
import com.barberfind.api.shared.util.Cuid;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPaymentMethodService {

    private final UserPaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    // ── List ──────────────────────────────────────────────────────────────────

    public List<PaymentMethodResponse> list(String targetUserId, String authenticatedUserId) {
        checkOwnership(targetUserId, authenticatedUserId);

        return paymentMethodRepository.findAllByUserId(targetUserId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Add ───────────────────────────────────────────────────────────────────

    @Transactional
    public PaymentMethodResponse add(String targetUserId, String authenticatedUserId,
                                     PaymentMethodRequest req) {
        checkOwnership(targetUserId, authenticatedUserId);

        var user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user_not_found"));

        // Evita duplicatas do mesmo token de gateway
        boolean alreadyExists = paymentMethodRepository
                .findAllByUserId(targetUserId)
                .stream()
                .anyMatch(p -> p.getProviderCustomerId().equals(req.providerCustomerId()));

        if (alreadyExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "payment_method_already_exists");
        }

        var method = UserPaymentMethod.builder()
                .id(Cuid.generate())
                .user(user)
                .provider(req.provider())
                .providerCustomerId(req.providerCustomerId())
                .cardType(req.cardType())
                .lastFourDigits(req.lastFourDigits())
                .brand(req.brand())
                .build();

        paymentMethodRepository.save(method);
        return toResponse(method);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    public void delete(String targetUserId, String authenticatedUserId, String methodId) {
        checkOwnership(targetUserId, authenticatedUserId);

        var method = paymentMethodRepository.findByIdAndUserId(methodId, targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "payment_method_not_found"));

        paymentMethodRepository.delete(method);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private PaymentMethodResponse toResponse(UserPaymentMethod m) {
        return new PaymentMethodResponse(
                m.getId(),
                m.getProvider(),
                m.getCardType(),
                m.getLastFourDigits(),
                m.getBrand(),
                m.getCreatedAt()
        );
    }

    private void checkOwnership(String targetUserId, String authenticatedUserId) {
        if (!targetUserId.equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden");
        }
    }
}