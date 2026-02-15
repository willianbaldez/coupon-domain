package br.com.stoom.coupon_domain.adapter.out.persistence;

import br.com.stoom.coupon_domain.domain.model.Coupon;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CouponAdapter implements br.com.stoom.coupon_domain.domain.port.CouponRepository {

    private final CouponRepository couponRepository;

    public CouponAdapter(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponEntity entity = CouponMapper.toJpaEntity(coupon);
        CouponEntity saved = couponRepository.save(entity);
        return CouponMapper.toDomain(saved);
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return couponRepository.findById(id)
                .map(CouponMapper::toDomain);
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        return couponRepository.findByCode(code)
                .map(CouponMapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return couponRepository.existsByCode(code);
    }
}
