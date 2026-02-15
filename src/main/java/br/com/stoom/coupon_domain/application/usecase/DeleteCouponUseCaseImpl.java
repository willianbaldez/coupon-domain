package br.com.stoom.coupon_domain.application.usecase;

import br.com.stoom.coupon_domain.application.port.in.DeleteCouponUseCase;
import br.com.stoom.coupon_domain.domain.exception.CouponNotFoundException;
import br.com.stoom.coupon_domain.domain.model.Coupon;
import br.com.stoom.coupon_domain.domain.port.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteCouponUseCaseImpl implements DeleteCouponUseCase {

    private final CouponRepository couponRepository;

    public DeleteCouponUseCaseImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    @Transactional
    public void execute(String couponCode) {
        String normalizedCode = couponCode.toUpperCase();

        Coupon coupon = couponRepository.findByCode(normalizedCode)
                .orElseThrow(() -> new CouponNotFoundException(
                        "Cupom não encontrado com o código '" + normalizedCode + "'"
                ));

        coupon.delete();

        couponRepository.save(coupon);
    }
}
