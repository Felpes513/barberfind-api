package com.barberfind.api.controller;

import com.barberfind.api.dto.DocumentDtos.*;
import com.barberfind.api.dto.UpdateUserRequest;
import com.barberfind.api.service.UpdateUserService;
import com.barberfind.api.service.UserDocumentService;
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

    // ── PATCH /api/users/{userId} ─────────────────────────────────────────────

    @PatchMapping
    public ResponseEntity<?> updateUser(
            @PathVariable String userId,
            @AuthenticationPrincipal String authenticatedUserId,
            @Valid @RequestBody UpdateUserRequest req
    ) {
        updateUserService.update(userId, authenticatedUserId, req);
        return ResponseEntity.ok(Map.of("message", "updated"));
    }

    // ── GET /api/users/{userId}/documents ─────────────────────────────────────

    @GetMapping("/documents")
    public ResponseEntity<List<DocumentResponse>> listDocuments(
            @PathVariable String userId,
            @AuthenticationPrincipal String authenticatedUserId
    ) {
        return ResponseEntity.ok(userDocumentService.list(userId, authenticatedUserId));
    }

    // ── POST /api/users/{userId}/documents ────────────────────────────────────

    @PostMapping("/documents")
    public ResponseEntity<DocumentResponse> addDocument(
            @PathVariable String userId,
            @AuthenticationPrincipal String authenticatedUserId,
            @Valid @RequestBody DocumentRequest req
    ) {
        DocumentResponse res = userDocumentService.add(userId, authenticatedUserId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // ── PUT /api/users/{userId}/documents/{documentId} ────────────────────────

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