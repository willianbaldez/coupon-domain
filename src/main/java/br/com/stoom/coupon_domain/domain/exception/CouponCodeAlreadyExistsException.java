package br.com.stoom.coupon_domain.domain.exception;

public class CouponCodeAlreadyExistsException extends DomainException {

    public CouponCodeAlreadyExistsException(String message) {
        super(message);
    }
}
