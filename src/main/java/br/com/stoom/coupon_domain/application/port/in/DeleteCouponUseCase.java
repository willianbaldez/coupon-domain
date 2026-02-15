package br.com.stoom.coupon_domain.application.port.in;

import java.util.UUID;

public interface DeleteCouponUseCase {

    void execute(UUID couponId);
}
