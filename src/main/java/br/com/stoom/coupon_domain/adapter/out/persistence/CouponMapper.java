package br.com.stoom.coupon_domain.adapter.out.persistence;

import br.com.stoom.coupon_domain.domain.model.Coupon;
import br.com.stoom.coupon_domain.domain.model.CouponCode;
import br.com.stoom.coupon_domain.domain.model.DiscountValue;
import br.com.stoom.coupon_domain.domain.model.ExpirationDate;

public final class CouponMapper {

    private CouponMapper() {
    }

    public static CouponEntity toJpaEntity(Coupon coupon) {
        return new CouponEntity(
                coupon.getId(),
                coupon.getCode().value(),
                coupon.getDescription(),
                coupon.getDiscountValue().value(),
                coupon.getExpirationDate().value(),
                coupon.isPublished(),
                coupon.isDeleted(),
                coupon.getDeletedAt(),
                coupon.getCreatedAt()
        );
    }

    public static Coupon toDomain(CouponEntity entity) {
        return Coupon.reconstitute(
                entity.getId(),
                CouponCode.reconstitute(entity.getCode()),
                entity.getDescription(),
                DiscountValue.reconstitute(entity.getDiscountValue()),
                ExpirationDate.reconstitute(entity.getExpirationDate()),
                entity.isPublished(),
                entity.isDeleted(),
                entity.getDeletedAt(),
                entity.getCreatedAt()
        );
    }
}
