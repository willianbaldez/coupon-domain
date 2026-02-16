package br.com.stoom.coupon_domain.application.usecase;

import br.com.stoom.coupon_domain.application.port.in.BuscarTodosCuponsUseCase;
import br.com.stoom.coupon_domain.domain.model.Coupon;
import br.com.stoom.coupon_domain.domain.port.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BuscarTodosCuponsUseCaseImpl implements BuscarTodosCuponsUseCase {

    private final CouponRepository couponRepository;

    public BuscarTodosCuponsUseCaseImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Coupon> execute() {
        return couponRepository.findAll();
    }
}
