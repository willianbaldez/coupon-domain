package br.com.stoom.coupon_domain.domain.model;

import br.com.stoom.coupon_domain.domain.exception.InvalidDiscountValueException;

import java.math.BigDecimal;
import java.util.Objects;

public final class DiscountValue {

    private static final BigDecimal MIN_VALUE = new BigDecimal("0.5");

    private final BigDecimal value;

    private DiscountValue(BigDecimal value) {
        this.value = value;
    }

    public static DiscountValue of(BigDecimal value) {
        if (value == null) {
            throw new InvalidDiscountValueException("O valor de desconto é obrigatório");
        }
        if (value.compareTo(MIN_VALUE) < 0) {
            throw new InvalidDiscountValueException(
                    "O valor de desconto deve ser no mínimo " + MIN_VALUE + ", mas foi informado " + value
            );
        }
        return new DiscountValue(value);
    }

    public static DiscountValue reconstitute(BigDecimal value) {
        return new DiscountValue(value);
    }

    public BigDecimal value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscountValue that)) return false;
        return value.compareTo(that.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}
