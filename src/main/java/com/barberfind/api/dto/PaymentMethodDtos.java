package com.barberfind.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class PaymentMethodDtos {

    /**
     * Payload para adicionar um cartão de crédito ou débito.
     * O provider_customer_id é gerado pelo gateway (ex: Stripe) no front-end
     * e enviado aqui para armazenar o vínculo.
     */
    public record PaymentMethodRequest(

            /**
             * Ex: "STRIPE", "PAGARME", "MERCADOPAGO"
             */
            @NotBlank(message = "provider_required")
            @Size(max = 50)
            String provider,

            /**
             * Token / customer_id retornado pelo gateway de pagamento.
             * Nunca armazenamos dados de cartão brutos.
             */
            @NotBlank(message = "provider_customer_id_required")
            @Size(max = 255)
            String providerCustomerId,

            /**
             * Tipo do cartão: "CREDIT" ou "DEBIT"
             */
            @NotBlank(message = "card_type_required")
            @Pattern(regexp = "^(CREDIT|DEBIT)$", message = "card_type_invalid")
            String cardType,

            /**
             * Últimos 4 dígitos do cartão (apenas para exibição).
             */
            @Pattern(regexp = "^[0-9]{4}$", message = "last_four_digits_invalid")
            String lastFourDigits,

            /**
             * Bandeira do cartão: ex. "VISA", "MASTERCARD", "ELO"
             */
            @Size(max = 30)
            String brand
    ) {
    }

    public record PaymentMethodResponse(
            String id,
            String provider,
            String cardType,
            String lastFourDigits,
            String brand,
            LocalDateTime createdAt
    ) {
    }
}