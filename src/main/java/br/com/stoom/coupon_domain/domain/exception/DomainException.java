package br.com.stoom.coupon_domain.domain.exception;

public class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
