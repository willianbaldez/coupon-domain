package br.com.stoom.coupon_domain.adapter.out.persistence;

import br.com.stoom.coupon_domain.domain.model.Coupon;
import br.com.stoom.coupon_domain.domain.port.CouponRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class CouponAdapter implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;

    public CouponAdapter(CouponJpaRepository couponJpaRepository) {
        this.couponJpaRepository = couponJpaRepository;
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponEntity entity = CouponMapper.toJpaEntity(coupon);
        CouponEntity saved = couponJpaRepository.save(entity);
        return CouponMapper.toDomain(saved);
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return couponJpaRepository.findById(id)
                .map(CouponMapper::toDomain);
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        return couponJpaRepository.findByCode(code)
                .map(CouponMapper::toDomain);
    }

    @Override
    public List<Coupon> findAll() {
        return couponJpaRepository.findAll().stream()
                .map(CouponMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCode(String code) {
        return couponJpaRepository.existsByCode(code);
    }
}
