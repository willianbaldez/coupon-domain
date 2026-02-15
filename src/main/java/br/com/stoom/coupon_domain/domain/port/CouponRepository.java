package br.com.stoom.coupon_domain.domain.port;

import br.com.stoom.coupon_domain.domain.model.Coupon;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(UUID id);

    Optional<Coupon> findByCode(String code);

    boolean existsByCode(String code);
}
