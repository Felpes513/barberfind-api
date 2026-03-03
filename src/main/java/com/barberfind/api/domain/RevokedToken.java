package com.barberfind.api.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private LocalDateTime revokedAt;

    public RevokedToken() {}

    public RevokedToken(String token) {
        this.token = token;
        this.revokedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getToken() { return token; }
    public LocalDateTime getRevokedAt() { return revokedAt; }
}