package br.com.stoom.coupon_domain.domain.model;

import br.com.stoom.coupon_domain.domain.exception.InvalidCouponCodeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CouponCode - Value Object")
class CouponCodeTest {

    @Nested
    @DisplayName("Criação via of()")
    class Creation {

        @Test
        @DisplayName("deve criar um código válido com 6 caracteres alfanuméricos")
        void shouldCreateValidCode() {
            CouponCode code = CouponCode.of("ABC123");

            assertEquals("ABC123", code.value());
        }

        @Test
        @DisplayName("deve normalizar o código para letras maiúsculas")
        void shouldNormalizeToUppercase() {
            CouponCode code = CouponCode.of("abc123");

            assertEquals("ABC123", code.value());
        }

        @Test
        @DisplayName("deve remover caracteres especiais antes da validação")
        void shouldStripSpecialCharacters() {
            CouponCode code = CouponCode.of("AB-C1.23");

            assertEquals("ABC123", code.value());
        }

        @Test
        @DisplayName("deve remover espaços antes da validação")
        void shouldStripSpaces() {
            CouponCode code = CouponCode.of("AB C1 23");

            assertEquals("ABC123", code.value());
        }

        @Test
        @DisplayName("deve rejeitar código nulo")
        void shouldRejectNull() {
            InvalidCouponCodeException ex = assertThrows(
                    InvalidCouponCodeException.class,
                    () -> CouponCode.of(null)
            );
            assertTrue(ex.getMessage().contains("obrigatório"));
        }

        @Test
        @DisplayName("deve rejeitar código em branco")
        void shouldRejectBlank() {
            assertThrows(InvalidCouponCodeException.class, () -> CouponCode.of("   "));
        }

        @Test
        @DisplayName("deve rejeitar código vazio")
        void shouldRejectEmpty() {
            assertThrows(InvalidCouponCodeException.class, () -> CouponCode.of(""));
        }

        @Test
        @DisplayName("deve rejeitar código com menos de 6 caracteres alfanuméricos")
        void shouldRejectTooShort() {
            InvalidCouponCodeException ex = assertThrows(
                    InvalidCouponCodeException.class,
                    () -> CouponCode.of("AB12")
            );
            assertTrue(ex.getMessage().contains("exatamente 6"));
        }

        @Test
        @DisplayName("deve rejeitar código com mais de 6 caracteres alfanuméricos")
        void shouldRejectTooLong() {
            assertThrows(InvalidCouponCodeException.class, () -> CouponCode.of("ABCDEFG"));
        }

        @Test
        @DisplayName("deve rejeitar código composto apenas por caracteres especiais")
        void shouldRejectOnlySpecialChars() {
            assertThrows(InvalidCouponCodeException.class, () -> CouponCode.of("!@#$%^"));
        }
    }

    @Nested
    @DisplayName("Reconstituição")
    class Reconstitution {

        @Test
        @DisplayName("deve reconstituir a partir de valor persistido sem validação")
        void shouldReconstitute() {
            CouponCode code = CouponCode.reconstitute("ABC123");

            assertEquals("ABC123", code.value());
        }
    }

    @Nested
    @DisplayName("Igualdade")
    class Equality {

        @Test
        @DisplayName("deve ser igual quando os valores são iguais")
        void shouldBeEqualForSameValue() {
            CouponCode code1 = CouponCode.of("ABC123");
            CouponCode code2 = CouponCode.of("abc123");

            assertEquals(code1, code2);
            assertEquals(code1.hashCode(), code2.hashCode());
        }

        @Test
        @DisplayName("não deve ser igual quando os valores são diferentes")
        void shouldNotBeEqualForDifferentValues() {
            CouponCode code1 = CouponCode.of("ABC123");
            CouponCode code2 = CouponCode.of("XYZ789");

            assertNotEquals(code1, code2);
        }

        @Test
        @DisplayName("não deve ser igual a nulo")
        void shouldNotBeEqualToNull() {
            CouponCode code = CouponCode.of("ABC123");

            assertNotEquals(null, code);
        }
    }

    @Test
    @DisplayName("toString deve retornar o valor do código")
    void toStringShouldReturnValue() {
        CouponCode code = CouponCode.of("abc123");

        assertEquals("ABC123", code.toString());
    }
}
