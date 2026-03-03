package com.barberfind.api.repository;

import com.barberfind.api.domain.UserDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDocumentRepository extends JpaRepository<UserDocument, String> {

    List<UserDocument> findAllByUserId(String userId);

    Optional<UserDocument> findByIdAndUserId(String id, String userId);
}