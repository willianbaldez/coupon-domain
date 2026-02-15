package br.com.stoom.coupon_domain.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCouponRequest(
        @NotBlank(message = "O código é obrigatório")
        String code,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @NotNull(message = "O valor de desconto é obrigatório")
        @DecimalMin(value = "0.5", message = "O valor de desconto deve ser no mínimo 0.5")
        BigDecimal discountValue,

        @NotNull(message = "A data de expiração é obrigatória")
        @FutureOrPresent(message = "A data de expiração não pode estar no passado")
        LocalDate expirationDate,

        boolean published
) {
}
