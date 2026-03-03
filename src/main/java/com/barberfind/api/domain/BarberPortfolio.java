package com.barberfind.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "barber_portfolio")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BarberPortfolio {

    @Id
    @Column(length = 40)
    private String id;

    @Column(name = "barber_id", length = 40, nullable = false)
    private String barberId;

    @Column(name = "image_url", columnDefinition = "text")
    private String imageUrl;

    @Column(name = "hair_type")
    private String hairType;

    @Column(name = "style_tag")
    private String styleTag;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}