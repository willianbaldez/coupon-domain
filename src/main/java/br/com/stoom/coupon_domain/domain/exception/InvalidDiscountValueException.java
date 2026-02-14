package br.com.stoom.coupon_domain.domain.exception;

public class InvalidDiscountValueException extends DomainException {

    public InvalidDiscountValueException(String message) {
        super(message);
    }
}
