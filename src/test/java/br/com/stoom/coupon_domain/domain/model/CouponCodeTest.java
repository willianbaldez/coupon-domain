package br.com.stoom.coupon_domain.domain.model;

import br.com.stoom.coupon_domain.domain.exception.InvalidCouponCodeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CouponCode Value Object")
class CouponCodeTest {

    @Nested
    @DisplayName("Creation via of()")
    class Creation {

        @Test
        @DisplayName("should create a valid coupon code with 6 alphanumeric characters")
        void shouldCreateValidCode() {
            CouponCode code = CouponCode.of("ABC123");

            assertEquals("ABC123", code.value());
        }

        @Test
        @DisplayName("should normalize code to uppercase")
        void shouldNormalizeToUppercase() {
            CouponCode code = CouponCode.of("abc123");

            assertEquals("ABC123", code.value());
        }

        @Test
        @DisplayName("should strip special characters before validation")
        void shouldStripSpecialCharacters() {
            CouponCode code = CouponCode.of("AB-C1.23");

            assertEquals("ABC123", code.value());
        }

        @Test
        @DisplayName("should strip spaces before validation")
        void shouldStripSpaces() {
            CouponCode code = CouponCode.of("AB C1 23");

            assertEquals("ABC123", code.value());
        }

        @Test
        @DisplayName("should reject null code")
        void shouldRejectNull() {
            InvalidCouponCodeException ex = assertThrows(
                    InvalidCouponCodeException.class,
                    () -> CouponCode.of(null)
            );
            assertTrue(ex.getMessage().contains("obrigatório"));
        }

        @Test
        @DisplayName("should reject blank code")
        void shouldRejectBlank() {
            assertThrows(InvalidCouponCodeException.class, () -> CouponCode.of("   "));
        }

        @Test
        @DisplayName("should reject empty code")
        void shouldRejectEmpty() {
            assertThrows(InvalidCouponCodeException.class, () -> CouponCode.of(""));
        }

        @Test
        @DisplayName("should reject code with fewer than 6 alphanumeric characters")
        void shouldRejectTooShort() {
            InvalidCouponCodeException ex = assertThrows(
                    InvalidCouponCodeException.class,
                    () -> CouponCode.of("AB12")
            );
            assertTrue(ex.getMessage().contains("exatamente 6"));
        }

        @Test
        @DisplayName("should reject code with more than 6 alphanumeric characters")
        void shouldRejectTooLong() {
            assertThrows(InvalidCouponCodeException.class, () -> CouponCode.of("ABCDEFG"));
        }

        @Test
        @DisplayName("should reject code that is only special characters")
        void shouldRejectOnlySpecialChars() {
            assertThrows(InvalidCouponCodeException.class, () -> CouponCode.of("!@#$%^"));
        }
    }

    @Nested
    @DisplayName("Reconstitution")
    class Reconstitution {

        @Test
        @DisplayName("should reconstitute from persisted value without validation")
        void shouldReconstitute() {
            CouponCode code = CouponCode.reconstitute("ABC123");

            assertEquals("ABC123", code.value());
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("should be equal when values are the same")
        void shouldBeEqualForSameValue() {
            CouponCode code1 = CouponCode.of("ABC123");
            CouponCode code2 = CouponCode.of("abc123");

            assertEquals(code1, code2);
            assertEquals(code1.hashCode(), code2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when values differ")
        void shouldNotBeEqualForDifferentValues() {
            CouponCode code1 = CouponCode.of("ABC123");
            CouponCode code2 = CouponCode.of("XYZ789");

            assertNotEquals(code1, code2);
        }

        @Test
        @DisplayName("should not be equal to null")
        void shouldNotBeEqualToNull() {
            CouponCode code = CouponCode.of("ABC123");

            assertNotEquals(null, code);
        }
    }

    @Test
    @DisplayName("toString should return the code value")
    void toStringShouldReturnValue() {
        CouponCode code = CouponCode.of("abc123");

        assertEquals("ABC123", code.toString());
    }
}
