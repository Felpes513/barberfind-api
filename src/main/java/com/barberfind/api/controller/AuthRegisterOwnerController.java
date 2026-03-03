package com.barberfind.api.controller;

import com.barberfind.api.dto.RegisterOwnerRequest;
import com.barberfind.api.dto.RegisterOwnerResponse;
import com.barberfind.api.service.RegisterOwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/register")
@RequiredArgsConstructor
public class AuthRegisterOwnerController {

    private final RegisterOwnerService registerOwnerService;

    @PostMapping("/owner")
    public ResponseEntity<?> registerOwner(@Valid @RequestBody RegisterOwnerRequest req) {
        RegisterOwnerResponse res = registerOwnerService.register(req);

        return ResponseEntity.status(201).body(Map.of(
                "message", "registered",
                "data", res
        ));
    }
}