package br.com.stoom.coupon_domain.domain.exception;

public class CouponNotFoundException extends DomainException {

    public CouponNotFoundException(String message) {
        super(message);
    }
}
