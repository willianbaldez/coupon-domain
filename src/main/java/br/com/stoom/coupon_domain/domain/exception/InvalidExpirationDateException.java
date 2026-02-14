package br.com.stoom.coupon_domain.domain.exception;

public class InvalidExpirationDateException extends DomainException {

    public InvalidExpirationDateException(String message) {
        super(message);
    }
}
