package com.barberfind.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "terms_acceptances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermsAcceptance {

    @Id
    @Column(length = 40)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "terms_version", length = 20, nullable = false)
    private String termsVersion;

    @Column(name = "accepted_at", nullable = false)
    private LocalDateTime acceptedAt;

    @PrePersist
    public void prePersist() {
        if (acceptedAt == null) acceptedAt = LocalDateTime.now();
    }
}