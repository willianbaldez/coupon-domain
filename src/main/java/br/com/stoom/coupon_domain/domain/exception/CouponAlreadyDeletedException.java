package br.com.stoom.coupon_domain.domain.exception;

public class CouponAlreadyDeletedException extends DomainException {

    public CouponAlreadyDeletedException(String message) {
        super(message);
    }
}
