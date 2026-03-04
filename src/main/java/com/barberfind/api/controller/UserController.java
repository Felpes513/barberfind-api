package com.barberfind.api.controller;

import com.barberfind.api.dto.AcceptTermsRequest;
import com.barberfind.api.dto.ChangePasswordRequest;
import com.barberfind.api.dto.DocumentDtos.*;
import com.barberfind.api.dto.PaymentMethodDtos.*;
import com.barberfind.api.dto.UpdateUserRequest;
import com.barberfind.api.dto.UserPreferencesRequest;
import com.barberfind.api.dto.UserPreferencesResponse;
import com.barberfind.api.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/{userId}")
@RequiredArgsConstructor
public class UserController {

    private final UpdateUserService updateUserService;
    private final UserDocumentService userDocumentService;
    private final ChangePasswordService changePasswordService;
    private final UserPaymentMethodService paymentMethodService;
    private final UserPreferencesService preferencesService;
    private final TermsAcceptanceService termsAcceptanceService;

    // =========================================================================
    // Dados pessoais
    // PATCH /api/users/{userId}
    // =========================================================================

    @PatchMapping
    public ResponseEntity<?> updateUser(
            @PathVariable String userId,
            @AuthenticationPrincipal String authenticatedUserId,
            @Valid @RequestBody UpdateUserRequest req
    ) {
        updateUserService.update(userId, authenticatedUserId, req);
        return ResponseEntity.ok(Map.of("message", "updated"));
    }

    // =========================================================================
    // Senha
    // PUT /api/users/{userId}/password
    // =========================================================================

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @PathVariable String userId,
            @AuthenticationPrincipal String authenticatedUserId,
            @Valid @RequestBody ChangePasswordRequest req
    ) {
        changePasswordService.change(userId, authenticatedUserId, req);
        return ResponseEntity.ok(Map.of("message", "password_updated"));
    }

    // =========================================================================
    // Cartões de crédito / débito
    // GET    /api/users/{userId}/payment-methods
    // POST   /api/users/{userId}/payment-methods
    // DELETE /api/users/{userId}/payment-methods/{methodId}
    // =========================================================================

    @GetMapping("/payment-methods")
    public ResponseEntity<List<PaymentMethodResponse>> listPaymentMethods(
            @PathVariable String userId,
            @AuthenticationPrincipal String authenticatedUserId
    ) {
        return ResponseEntity.ok(paymentMethodService.list(userId, authenticatedUserId));
    }

    @PostMapping("/payment-methods")
    public ResponseEntity<PaymentMethodResponse> addPaymentMethod(
            @PathVariable String userId,
            @AuthenticationPrincipal String authenticatedUserId,
            @Valid @RequestBody PaymentMethodRequest req
    ) {
        PaymentMethodResponse res = paymentMethodService.add(userId, authenticatedUserId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @DeleteMapping("/payment-methods/{methodId}")
    public ResponseEntity<?> deletePaymentMethod(
            @PathVariable String userId,
            @PathVariable String methodId,
            @AuthenticationPrincipal String authenticatedUserId
    ) {
        paymentMethodService.delete(userId, authenticatedUserId, methodId);
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // Preferências (tema e idioma)
    // GET  /api/users/{userId}/preferences
    // PUT  /api/users/{userId}/preferences
    // =========================================================================

    @GetMapping("/preferences")
    public ResponseEntity<UserPreferencesResponse> getPreferences(
            @PathVariable String userId,
            @AuthenticationPrincipal String authenticatedUserId
    ) {
        return ResponseEntity.ok(preferencesService.get(userId, authenticatedUserId));
    }

    @PutMapping("/preferences")
    public ResponseEntity<UserPreferencesResponse> updatePreferences(
            @PathVariable String userId,
            @AuthenticationPrincipal String authenticatedUserId,
            @Valid @RequestBody UserPreferencesRequest req
    ) {
        return ResponseEntity.ok(preferencesService.update(userId, authenticatedUserId, req));
    }

    // =========================================================================
    // Termos de uso
    // POST /api/users/{userId}/accept-terms
    // =========================================================================

    @PostMapping("/accept-terms")
    public ResponseEntity<?> acceptTerms(
            @PathVariable String userId,
            @AuthenticationPrincipal String authenticatedUserId,
            @Valid @RequestBody AcceptTermsRequest req
    ) {
        Map<String, Object> result = termsAcceptanceService.accept(userId, authenticatedUserId, req);
        return ResponseEntity.ok(result);
    }

    // =========================================================================
    // Documentos (já existia — mantido aqui para centralizar o controller)
    // GET  /api/users/{userId}/documents
    // POST /api/users/{userId}/documents
    // PUT  /api/users/{userId}/documents/{documentId}
    // =========================================================================

    @GetMapping("/documents")
    public ResponseEntity<List<DocumentResponse>> listDocuments(
            @PathVariable String userId,
            @AuthenticationPrincipal String authenticatedUserId
    ) {
        return ResponseEntity.ok(userDocumentService.list(userId, authenticatedUserId));
    }

    @PostMapping("/documents")
    public ResponseEntity<DocumentResponse> addDocument(
            @PathVariable String userId,
            @AuthenticationPrincipal String authenticatedUserId,
            @Valid @RequestBody DocumentRequest req
    ) {
        DocumentResponse res = userDocumentService.add(userId, authenticatedUserId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/documents/{documentId}")
    public ResponseEntity<DocumentResponse> updateDocument(
            @PathVariable String userId,
            @PathVariable String documentId,
            @AuthenticationPrincipal String authenticatedUserId,
            @Valid @RequestBody DocumentUpdateRequest req
    ) {
        DocumentResponse res = userDocumentService.update(userId, authenticatedUserId, documentId, req);
        return ResponseEntity.ok(res);
    }
}