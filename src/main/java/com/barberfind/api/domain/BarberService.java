package com.barberfind.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "barber_services")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BarberService {

    @Id
    @Column(length = 40)
    private String id;

    @Column(name = "barber_id", length = 40, nullable = false)
    private String barberId;

    @Column(name = "service_id", length = 40, nullable = false)
    private String serviceId;
}