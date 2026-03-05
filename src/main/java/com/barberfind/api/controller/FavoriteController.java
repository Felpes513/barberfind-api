package com.barberfind.api.controller;

import com.barberfind.api.dto.FavoriteDtos.*;
import com.barberfind.api.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // GET /api/users/{userId}/favorites
    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> list(
            @PathVariable String userId,
            @AuthenticationPrincipal String authenticatedUserId
    ) {
        return ResponseEntity.ok(favoriteService.list(userId, authenticatedUserId));
    }

    // POST /api/users/{userId}/favorites
    @PostMapping
    public ResponseEntity<FavoriteResponse> add(
            @PathVariable String userId,
            @AuthenticationPrincipal String authenticatedUserId,
            @Valid @RequestBody FavoriteRequest req
    ) {
        FavoriteResponse res = favoriteService.add(userId, authenticatedUserId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // DELETE /api/users/{userId}/favorites/{favoriteId}
    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<?> remove(
            @PathVariable String userId,
            @PathVariable String favoriteId,
            @AuthenticationPrincipal String authenticatedUserId
    ) {
        favoriteService.remove(userId, authenticatedUserId, favoriteId);
        return ResponseEntity.noContent().build();
    }
}