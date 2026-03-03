package com.barberfind.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "barbershop_photos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BarbershopPhoto {

    @Id
    @Column(length = 40)
    private String id;

    @Column(name = "barbershop_id", length = 40, nullable = false)
    private String barbershopId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}