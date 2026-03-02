package com.barberfind.api.auth.controller;

import com.barberfind.api.auth.dto.RegisterClientRequest;
import com.barberfind.api.auth.service.RegisterClientService;
import com.barberfind.api.users.domain.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.Map;

@RestController
@RequestMapping("/api/auth/register")
public class AuthRegisterController {

    private final RegisterClientService registerClientService;

    public AuthRegisterController(RegisterClientService registerClientService) {
        this.registerClientService = registerClientService;
    }

    @PostMapping("/client")
    public ResponseEntity<?> registerClient(@Valid @RequestBody RegisterClientRequest req) {
        User u = registerClientService.register(req);

        return ResponseEntity.status(201).body(Map.of(
                "message", "registered",
                "data", Map.of(
                        "id", u.getId(),
                        "role", u.getRole().name(),
                        "email", u.getEmail()
                )
        ));
    }
}