package br.com.stoom.coupon_domain.application.usecase;

import br.com.stoom.coupon_domain.application.port.in.CreateCouponUseCase;
import br.com.stoom.coupon_domain.domain.exception.CouponCodeAlreadyExistsException;
import br.com.stoom.coupon_domain.domain.model.Coupon;
import br.com.stoom.coupon_domain.domain.port.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCouponUseCaseImpl implements CreateCouponUseCase {

    private final CouponRepository couponRepository;

    public CreateCouponUseCaseImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    @Transactional
    public Coupon execute(CreateCouponCommand command) {
        Coupon coupon = Coupon.create(
                command.code(),
                command.description(),
                command.discountValue(),
                command.expirationDate(),
                command.published()
        );

        if (couponRepository.existsByCode(coupon.getCode().value())) {
            throw new CouponCodeAlreadyExistsException(
                    "Já existe um cupom com o código '" + coupon.getCode().value() + "'"
            );
        }

        return couponRepository.save(coupon);
    }
}
