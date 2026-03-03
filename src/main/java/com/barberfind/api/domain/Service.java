package com.barberfind.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "services")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Service {

    @Id
    @Column(length = 40)
    private String id;

    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "base_price")
    private BigDecimal basePrice;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;
}