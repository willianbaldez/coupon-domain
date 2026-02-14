package br.com.stoom.coupon_domain.domain.model;

import br.com.stoom.coupon_domain.domain.exception.InvalidExpirationDateException;

import java.time.LocalDate;
import java.util.Objects;

public final class ExpirationDate {

    private final LocalDate value;

    private ExpirationDate(LocalDate value) {
        this.value = value;
    }

    public static ExpirationDate of(LocalDate value) {
        if (value == null) {
            throw new InvalidExpirationDateException("A data de expiração é obrigatória");
        }
        if (value.isBefore(LocalDate.now())) {
            throw new InvalidExpirationDateException("A data de expiração não pode estar no passado");
        }
        return new ExpirationDate(value);
    }

    public static ExpirationDate reconstitute(LocalDate value) {
        return new ExpirationDate(value);
    }

    public LocalDate value() {
        return value;
    }

    public boolean isExpired() {
        return value.isBefore(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpirationDate that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
