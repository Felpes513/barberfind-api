package com.barberfind.api.service;

import com.barberfind.api.domain.UserDocument;
import com.barberfind.api.dto.DocumentDtos.*;
import com.barberfind.api.repository.UserDocumentRepository;
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
public class UserDocumentService {

    private final UserDocumentRepository documentRepository;
    private final UserRepository userRepository;

    // ── List ──────────────────────────────────────────────────────────────────

    public List<DocumentResponse> list(String targetUserId, String authenticatedUserId) {
        checkOwnership(targetUserId, authenticatedUserId);

        return documentRepository.findAllByUserId(targetUserId)
                .stream()
                .map(d -> new DocumentResponse(
                        d.getId(),
                        d.getDocumentType(),
                        d.getDocumentNumber(),
                        d.getCreatedAt()
                ))
                .toList();
    }

    // ── Add ───────────────────────────────────────────────────────────────────

    @Transactional
    public DocumentResponse add(String targetUserId, String authenticatedUserId, DocumentRequest req) {
        checkOwnership(targetUserId, authenticatedUserId);

        var user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user_not_found"));

        var doc = UserDocument.builder()
                .id(Cuid.generate())
                .user(user)
                .documentType(req.documentType())
                .documentNumber(req.documentNumber())
                .build();

        documentRepository.save(doc);

        return new DocumentResponse(
                doc.getId(),
                doc.getDocumentType(),
                doc.getDocumentNumber(),
                doc.getCreatedAt()
        );
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public DocumentResponse update(String targetUserId, String authenticatedUserId,
                                   String documentId, DocumentUpdateRequest req) {
        checkOwnership(targetUserId, authenticatedUserId);

        var doc = documentRepository.findByIdAndUserId(documentId, targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "document_not_found"));

        if (req.documentType() != null)   doc.setDocumentType(req.documentType());
        if (req.documentNumber() != null) doc.setDocumentNumber(req.documentNumber());

        documentRepository.save(doc);

        return new DocumentResponse(
                doc.getId(),
                doc.getDocumentType(),
                doc.getDocumentNumber(),
                doc.getCreatedAt()
        );
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void checkOwnership(String targetUserId, String authenticatedUserId) {
        if (!targetUserId.equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden");
        }
    }
}