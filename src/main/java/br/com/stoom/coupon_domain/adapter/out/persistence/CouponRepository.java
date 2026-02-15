package br.com.stoom.coupon_domain.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CouponRepository extends JpaRepository<CouponEntity, UUID> {

    java.util.Optional<CouponEntity> findByCode(String code);

    boolean existsByCode(String code);
}
