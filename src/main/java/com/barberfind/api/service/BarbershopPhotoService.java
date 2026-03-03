package com.barberfind.api.service;

import com.barberfind.api.domain.BarbershopPhoto;
import com.barberfind.api.dto.ServiceDtos.*;
import com.barberfind.api.repository.BarbershopPhotoRepository;
import com.barberfind.api.repository.BarbershopRepository;
import com.barberfind.api.security.AuthUtils;
import com.barberfind.api.shared.util.Cuid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BarbershopPhotoService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );

    // Limite de 5MB por imagem (Base64 aumenta ~33%, então 5MB real ≈ 6.8MB em Base64)
    private static final int MAX_BASE64_LENGTH = 6_800_000;

    private final BarbershopPhotoRepository photoRepo;
    private final BarbershopRepository barbershopRepo;

    @Transactional
    public PhotoResponse add(String barbershopId, PhotoAddRequest req) {
        checkOwner(barbershopId);

        // Valida media type
        if (!ALLOWED_TYPES.contains(req.mediaType().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "invalid_media_type. Allowed: image/jpeg, image/png, image/webp, image/gif");
        }

        // Valida tamanho
        if (req.imageData().length() > MAX_BASE64_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "image_too_large. Max size: 5MB");
        }

        // Valida que é Base64 válido
        try {
            Base64.getDecoder().decode(req.imageData().replaceAll("\\s", ""));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid_base64");
        }

        // Monta data URL para armazenamento: "data:image/jpeg;base64,/9j/..."
        String dataUrl = "data:" + req.mediaType() + ";base64," + req.imageData();

        var photo = BarbershopPhoto.builder()
                .id(Cuid.generate())
                .barbershopId(barbershopId)
                .imageUrl(dataUrl)
                .build();

        photoRepo.save(photo);

        return new PhotoResponse(photo.getId(), photo.getImageUrl(), photo.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<PhotoResponse> list(String barbershopId) {
        return photoRepo.findAllByBarbershopId(barbershopId).stream()
                .map(p -> new PhotoResponse(p.getId(), p.getImageUrl(), p.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void delete(String barbershopId, String photoId) {
        checkOwner(barbershopId);

        var photo = photoRepo.findByIdAndBarbershopId(photoId, barbershopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "photo_not_found"));

        photoRepo.delete(photo);
    }

    private void checkOwner(String barbershopId) {
        String userId = AuthUtils.getUserId();
        barbershopRepo.findByIdAndOwnerUserId(barbershopId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "not_owner_or_barbershop_not_found"));
    }
}