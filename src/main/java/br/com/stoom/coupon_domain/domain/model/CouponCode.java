package br.com.stoom.coupon_domain.domain.model;

import br.com.stoom.coupon_domain.domain.exception.InvalidCouponCodeException;

import java.util.Objects;

public final class CouponCode {

    private static final int REQUIRED_LENGTH = 6;
    private static final String ALPHANUMERIC_PATTERN = "[^a-zA-Z0-9]";

    private final String value;

    private CouponCode(String value) {
        this.value = value;
    }

    public static CouponCode of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidCouponCodeException("O código do cupom é obrigatório");
        }

        String sanitized = raw.replaceAll(ALPHANUMERIC_PATTERN, "").toUpperCase();

        if (sanitized.length() != REQUIRED_LENGTH) {
            throw new InvalidCouponCodeException(
                    "O código do cupom deve ter exatamente " + REQUIRED_LENGTH
                            + " caracteres alfanuméricos, mas possui " + sanitized.length()
            );
        }

        return new CouponCode(sanitized);
    }

    public static CouponCode reconstitute(String value) {
        return new CouponCode(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CouponCode that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
