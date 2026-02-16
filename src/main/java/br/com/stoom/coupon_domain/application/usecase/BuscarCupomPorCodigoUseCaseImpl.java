package br.com.stoom.coupon_domain.application.usecase;

import br.com.stoom.coupon_domain.application.port.in.BuscarCupomPorCodigoUseCase;
import br.com.stoom.coupon_domain.domain.exception.CouponNotFoundException;
import br.com.stoom.coupon_domain.domain.model.Coupon;
import br.com.stoom.coupon_domain.domain.port.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuscarCupomPorCodigoUseCaseImpl implements BuscarCupomPorCodigoUseCase {

    private final CouponRepository couponRepository;

    public BuscarCupomPorCodigoUseCaseImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Coupon execute(String codigo) {
        String normalizedCode = codigo.toUpperCase();

        return couponRepository.findByCode(normalizedCode)
                .orElseThrow(() -> new CouponNotFoundException(
                        "Cupom não encontrado com o código '" + normalizedCode + "'"
                ));
    }
}
