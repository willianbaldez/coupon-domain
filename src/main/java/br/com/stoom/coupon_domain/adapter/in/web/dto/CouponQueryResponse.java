package br.com.stoom.coupon_domain.adapter.in.web.dto;

import br.com.stoom.coupon_domain.domain.model.Coupon;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CouponQueryResponse(
        String codigo,
        BigDecimal valorDesconto,
        LocalDate dataExpiracao,
        boolean removido
) {

    public static CouponQueryResponse from(Coupon coupon) {
        return new CouponQueryResponse(
                coupon.getCode().value(),
                coupon.getDiscountValue().value(),
                coupon.getExpirationDate().value(),
                coupon.isDeleted()
        );
    }
}
