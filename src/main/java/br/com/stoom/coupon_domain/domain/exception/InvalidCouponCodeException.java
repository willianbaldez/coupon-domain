package br.com.stoom.coupon_domain.domain.exception;

public class InvalidCouponCodeException extends DomainException {

    public InvalidCouponCodeException(String message) {
        super(message);
    }
}
