package com.barberfind.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "barbers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BarberEntity {

    @Id
    @Column(length = 40)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String name;

    @Column(columnDefinition = "text")
    private String bio;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    private BigDecimal rating;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}