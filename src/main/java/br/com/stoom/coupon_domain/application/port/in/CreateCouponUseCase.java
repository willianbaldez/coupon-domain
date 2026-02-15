package br.com.stoom.coupon_domain.application.port.in;

import br.com.stoom.coupon_domain.domain.model.Coupon;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CreateCouponUseCase {

    Coupon execute(CreateCouponCommand command);

    record CreateCouponCommand(
            String code,
            String description,
            BigDecimal discountValue,
            LocalDate expirationDate,
            boolean published
    ) {
    }
}
