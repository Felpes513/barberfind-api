package com.barberfind.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "barbershop_services")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BarbershopService {

    @Id
    @Column(length = 40)
    private String id;

    @Column(name = "barbershop_id", length = 40, nullable = false)
    private String barbershopId;

    @Column(name = "service_id", length = 40, nullable = false)
    private String serviceId;

    @Column(name = "custom_price")
    private BigDecimal customPrice;
}