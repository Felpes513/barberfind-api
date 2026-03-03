package com.barberfind.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "barber_availability")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class BarberAvailability {

    @Id
    @Column(length = 40)
    private String id;

    @Column(name = "barber_id", length = 40, nullable = false)
    private String barberId;

    private Integer weekday; // 0=domingo, 1=segunda, ..., 6=sábado

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;
}