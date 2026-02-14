package br.com.stoom.coupon_domain.domain.model;

import br.com.stoom.coupon_domain.domain.exception.InvalidDiscountValueException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DiscountValue - Value Object")
class DiscountValueTest {

    @Nested
    @DisplayName("Criação via of()")
    class Creation {

        @Test
        @DisplayName("deve criar com o valor mínimo exato (0.5)")
        void shouldCreateWithMinimumValue() {
            DiscountValue discount = DiscountValue.of(new BigDecimal("0.5"));

            assertEquals(new BigDecimal("0.5"), discount.value());
        }

        @Test
        @DisplayName("deve criar com valor acima do mínimo")
        void shouldCreateWithValueAboveMinimum() {
            DiscountValue discount = DiscountValue.of(new BigDecimal("10.00"));

            assertEquals(new BigDecimal("10.00"), discount.value());
        }

        @Test
        @DisplayName("deve criar com valor de desconto alto")
        void shouldCreateWithLargeValue() {
            DiscountValue discount = DiscountValue.of(new BigDecimal("999.99"));

            assertEquals(new BigDecimal("999.99"), discount.value());
        }

        @Test
        @DisplayName("deve rejeitar valor nulo")
        void shouldRejectNull() {
            InvalidDiscountValueException ex = assertThrows(
                    InvalidDiscountValueException.class,
                    () -> DiscountValue.of(null)
            );
            assertTrue(ex.getMessage().contains("obrigatório"));
        }

        @Test
        @DisplayName("deve rejeitar valor abaixo do mínimo (0.49)")
        void shouldRejectBelowMinimum() {
            InvalidDiscountValueException ex = assertThrows(
                    InvalidDiscountValueException.class,
                    () -> DiscountValue.of(new BigDecimal("0.49"))
            );
            assertTrue(ex.getMessage().contains("no mínimo"));
        }

        @Test
        @DisplayName("deve rejeitar valor zero")
        void shouldRejectZero() {
            assertThrows(
                    InvalidDiscountValueException.class,
                    () -> DiscountValue.of(BigDecimal.ZERO)
            );
        }

        @Test
        @DisplayName("deve rejeitar valor negativo")
        void shouldRejectNegative() {
            assertThrows(
                    InvalidDiscountValueException.class,
                    () -> DiscountValue.of(new BigDecimal("-1.0"))
            );
        }
    }

    @Nested
    @DisplayName("Reconstituição")
    class Reconstitution {

        @Test
        @DisplayName("deve reconstituir a partir de valor persistido sem validação")
        void shouldReconstitute() {
            DiscountValue discount = DiscountValue.reconstitute(new BigDecimal("25.00"));

            assertEquals(new BigDecimal("25.00"), discount.value());
        }
    }

    @Nested
    @DisplayName("Igualdade")
    class Equality {

        @Test
        @DisplayName("deve ser igual quando os valores numéricos são iguais (ignorando escala)")
        void shouldBeEqualIgnoringScale() {
            DiscountValue d1 = DiscountValue.of(new BigDecimal("10.0"));
            DiscountValue d2 = DiscountValue.of(new BigDecimal("10.00"));

            assertEquals(d1, d2);
            assertEquals(d1.hashCode(), d2.hashCode());
        }

        @Test
        @DisplayName("não deve ser igual quando os valores são diferentes")
        void shouldNotBeEqualForDifferentValues() {
            DiscountValue d1 = DiscountValue.of(new BigDecimal("5.00"));
            DiscountValue d2 = DiscountValue.of(new BigDecimal("10.00"));

            assertNotEquals(d1, d2);
        }

        @Test
        @DisplayName("não deve ser igual a nulo")
        void shouldNotBeEqualToNull() {
            DiscountValue discount = DiscountValue.of(new BigDecimal("5.00"));

            assertNotEquals(null, discount);
        }
    }

    @Test
    @DisplayName("toString deve retornar o valor numérico sem notação científica")
    void toStringShouldReturnPlainValue() {
        DiscountValue discount = DiscountValue.of(new BigDecimal("15.50"));

        assertEquals("15.50", discount.toString());
    }
}
