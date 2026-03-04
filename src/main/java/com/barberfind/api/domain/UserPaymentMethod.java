package com.barberfind.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPaymentMethod {

    @Id
    @Column(length = 40)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Ex: "STRIPE", "PAGARME", "MERCADOPAGO"
     */
    @Column(length = 50)
    private String provider;

    /**
     * Token / customer_id do gateway — nunca dados brutos de cartão.
     */
    @Column(name = "provider_customer_id", length = 255)
    private String providerCustomerId;

    /**
     * "CREDIT" ou "DEBIT"
     */
    @Column(name = "card_type", length = 10)
    private String cardType;

    /**
     * Últimos 4 dígitos — somente para exibição.
     */
    @Column(name = "last_four_digits", length = 4)
    private String lastFourDigits;

    /**
     * Ex: "VISA", "MASTERCARD", "ELO"
     */
    @Column(length = 30)
    private String brand;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}