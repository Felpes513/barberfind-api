package com.barberfind.api.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class InitiController {

    @GetMapping(value = "/initi", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> initi() {
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "msg", "API BarberFind a responder."
        ));
    }
}
