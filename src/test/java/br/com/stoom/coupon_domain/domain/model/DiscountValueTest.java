package br.com.stoom.coupon_domain.domain.model;

import br.com.stoom.coupon_domain.domain.exception.InvalidDiscountValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DiscountValue Value Object")
class DiscountValueTest {

    @Nested
    @DisplayName("Creation via of()")
    class Creation {

        @Test
        @DisplayName("should create with exact minimum value (0.5)")
        void shouldCreateWithMinimumValue() {
            DiscountValue discount = DiscountValue.of(new BigDecimal("0.5"));

            assertEquals(new BigDecimal("0.5"), discount.value());
        }

        @Test
        @DisplayName("should create with value above minimum")
        void shouldCreateWithValueAboveMinimum() {
            DiscountValue discount = DiscountValue.of(new BigDecimal("10.00"));

            assertEquals(new BigDecimal("10.00"), discount.value());
        }

        @Test
        @DisplayName("should create with large discount value")
        void shouldCreateWithLargeValue() {
            DiscountValue discount = DiscountValue.of(new BigDecimal("999.99"));

            assertEquals(new BigDecimal("999.99"), discount.value());
        }

        @Test
        @DisplayName("should reject null value")
        void shouldRejectNull() {
            InvalidDiscountValueException ex = assertThrows(
                    InvalidDiscountValueException.class,
                    () -> DiscountValue.of(null)
            );
            assertTrue(ex.getMessage().contains("required"));
        }

        @Test
        @DisplayName("should reject value below minimum (0.49)")
        void shouldRejectBelowMinimum() {
            InvalidDiscountValueException ex = assertThrows(
                    InvalidDiscountValueException.class,
                    () -> DiscountValue.of(new BigDecimal("0.49"))
            );
            assertTrue(ex.getMessage().contains("at least"));
        }

        @Test
        @DisplayName("should reject zero")
        void shouldRejectZero() {
            assertThrows(
                    InvalidDiscountValueException.class,
                    () -> DiscountValue.of(BigDecimal.ZERO)
            );
        }

        @Test
        @DisplayName("should reject negative value")
        void shouldRejectNegative() {
            assertThrows(
                    InvalidDiscountValueException.class,
                    () -> DiscountValue.of(new BigDecimal("-1.0"))
            );
        }
    }

    @Nested
    @DisplayName("Reconstitution")
    class Reconstitution {

        @Test
        @DisplayName("should reconstitute from persisted value without validation")
        void shouldReconstitute() {
            DiscountValue discount = DiscountValue.reconstitute(new BigDecimal("25.00"));

            assertEquals(new BigDecimal("25.00"), discount.value());
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("should be equal when numeric values are the same (ignoring scale)")
        void shouldBeEqualIgnoringScale() {
            DiscountValue d1 = DiscountValue.of(new BigDecimal("10.0"));
            DiscountValue d2 = DiscountValue.of(new BigDecimal("10.00"));

            assertEquals(d1, d2);
            assertEquals(d1.hashCode(), d2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when values differ")
        void shouldNotBeEqualForDifferentValues() {
            DiscountValue d1 = DiscountValue.of(new BigDecimal("5.00"));
            DiscountValue d2 = DiscountValue.of(new BigDecimal("10.00"));

            assertNotEquals(d1, d2);
        }

        @Test
        @DisplayName("should not be equal to null")
        void shouldNotBeEqualToNull() {
            DiscountValue discount = DiscountValue.of(new BigDecimal("5.00"));

            assertNotEquals(null, discount);
        }
    }

    @Test
    @DisplayName("toString should return plain numeric string")
    void toStringShouldReturnPlainValue() {
        DiscountValue discount = DiscountValue.of(new BigDecimal("15.50"));

        assertEquals("15.50", discount.toString());
    }
}
