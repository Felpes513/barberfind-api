package com.barberfind.api.controller;

import com.barberfind.api.dto.LoginRequest;
import com.barberfind.api.dto.LoginResponse;
import com.barberfind.api.service.LoginService;
import com.barberfind.api.service.LogoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthLoginController {

    private final LoginService loginService;
    private final LogoutService logoutService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(loginService.login(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logoutService.revokeToken(token);
        }
        return ResponseEntity.noContent().build();
    }
}