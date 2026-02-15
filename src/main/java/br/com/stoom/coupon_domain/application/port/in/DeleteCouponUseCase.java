package br.com.stoom.coupon_domain.application.port.in;

public interface DeleteCouponUseCase {

    void execute(String couponCode);
}
