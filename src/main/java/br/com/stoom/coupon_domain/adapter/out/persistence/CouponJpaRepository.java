package br.com.stoom.coupon_domain.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CouponJpaRepository extends JpaRepository<CouponEntity, UUID> {

    Optional<CouponEntity> findByCode(String code);

    boolean existsByCode(String code);
}
