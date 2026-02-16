package br.com.stoom.coupon_domain.domain.port;

import br.com.stoom.coupon_domain.domain.model.Coupon;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(UUID id);

    Optional<Coupon> findByCode(String code);

    List<Coupon> findAll();

    boolean existsByCode(String code);
}
