package com.barberfind.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "barbershop_barbers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BarbershopBarber {

    @Id
    @Column(length = 40)
    private String id;

    @Column(name = "barbershop_id", length = 40, nullable = false)
    private String barbershopId;

    @Column(name = "barber_id", length = 40, nullable = false)
    private String barberId;

    private Boolean active;
}