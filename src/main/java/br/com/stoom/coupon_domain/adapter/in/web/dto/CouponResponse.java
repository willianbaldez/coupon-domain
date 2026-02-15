package br.com.stoom.coupon_domain.adapter.in.web.dto;

import br.com.stoom.coupon_domain.domain.model.Coupon;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record CouponResponse(
        UUID id,
        String code,
        String description,
        BigDecimal discountValue,
        LocalDate expirationDate,
        boolean published,
        boolean deleted,
        LocalDateTime deletedAt,
        LocalDateTime createdAt,
        boolean expired,
        boolean active
) {

    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode().value(),
                coupon.getDescription(),
                coupon.getDiscountValue().value(),
                coupon.getExpirationDate().value(),
                coupon.isPublished(),
                coupon.isDeleted(),
                coupon.getDeletedAt(),
                coupon.getCreatedAt(),
                coupon.isExpired(),
                coupon.isActive()
        );
    }
}
