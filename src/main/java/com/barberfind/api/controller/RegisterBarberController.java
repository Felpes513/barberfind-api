package com.barberfind.api.controller;

import com.barberfind.api.dto.RegisterBarberRequest;
import com.barberfind.api.dto.RegisterBarberResponse;
import com.barberfind.api.service.RegisterBarberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/register")
@RequiredArgsConstructor
public class RegisterBarberController {

    private final RegisterBarberService registerBarberService;

    @PostMapping("/barber")
    public ResponseEntity<?> registerBarber(@Valid @RequestBody RegisterBarberRequest req) {
        RegisterBarberResponse res = registerBarberService.register(req);

        return ResponseEntity.status(201).body(Map.of(
                "message", "registered",
                "data", Map.of(
                        "userId", res.userId(),
                        "barberId", res.barberId(),
                        "name", res.name(),
                        "email", res.email()
                )
        ));
    }
}