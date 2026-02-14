package br.com.stoom.coupon_domain.domain.model;

import br.com.stoom.coupon_domain.domain.exception.InvalidExpirationDateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ExpirationDate Value Object")
class ExpirationDateTest {

    @Nested
    @DisplayName("Creation via of()")
    class Creation {

        @Test
        @DisplayName("should create with a future date")
        void shouldCreateWithFutureDate() {
            LocalDate future = LocalDate.now().plusDays(30);
            ExpirationDate date = ExpirationDate.of(future);

            assertEquals(future, date.value());
        }

        @Test
        @DisplayName("should create with today's date (today is not in the past)")
        void shouldCreateWithToday() {
            LocalDate today = LocalDate.now();
            ExpirationDate date = ExpirationDate.of(today);

            assertEquals(today, date.value());
        }

        @Test
        @DisplayName("should reject null date")
        void shouldRejectNull() {
            InvalidExpirationDateException ex = assertThrows(
                    InvalidExpirationDateException.class,
                    () -> ExpirationDate.of(null)
            );
            assertTrue(ex.getMessage().contains("obrigatória"));
        }

        @Test
        @DisplayName("should reject a date in the past")
        void shouldRejectPastDate() {
            LocalDate yesterday = LocalDate.now().minusDays(1);

            InvalidExpirationDateException ex = assertThrows(
                    InvalidExpirationDateException.class,
                    () -> ExpirationDate.of(yesterday)
            );
            assertTrue(ex.getMessage().contains("passado"));
        }
    }

    @Nested
    @DisplayName("Reconstitution")
    class Reconstitution {

        @Test
        @DisplayName("should reconstitute past date without validation (from persistence)")
        void shouldReconstitutePastDate() {
            LocalDate pastDate = LocalDate.now().minusDays(30);
            ExpirationDate date = ExpirationDate.reconstitute(pastDate);

            assertEquals(pastDate, date.value());
        }
    }

    @Nested
    @DisplayName("isExpired()")
    class IsExpired {

        @Test
        @DisplayName("should return false for a future date")
        void shouldNotBeExpiredForFutureDate() {
            ExpirationDate date = ExpirationDate.of(LocalDate.now().plusDays(10));

            assertFalse(date.isExpired());
        }

        @Test
        @DisplayName("should return false for today")
        void shouldNotBeExpiredForToday() {
            ExpirationDate date = ExpirationDate.of(LocalDate.now());

            assertFalse(date.isExpired());
        }

        @Test
        @DisplayName("should return true for a past date (reconstituted)")
        void shouldBeExpiredForPastDate() {
            ExpirationDate date = ExpirationDate.reconstitute(LocalDate.now().minusDays(1));

            assertTrue(date.isExpired());
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("should be equal when dates are the same")
        void shouldBeEqualForSameDate() {
            LocalDate date = LocalDate.now().plusDays(5);
            ExpirationDate d1 = ExpirationDate.of(date);
            ExpirationDate d2 = ExpirationDate.of(date);

            assertEquals(d1, d2);
            assertEquals(d1.hashCode(), d2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when dates differ")
        void shouldNotBeEqualForDifferentDates() {
            ExpirationDate d1 = ExpirationDate.of(LocalDate.now().plusDays(1));
            ExpirationDate d2 = ExpirationDate.of(LocalDate.now().plusDays(2));

            assertNotEquals(d1, d2);
        }
    }

    @Test
    @DisplayName("toString should return ISO date string")
    void toStringShouldReturnIsoDate() {
        LocalDate date = LocalDate.of(2026, 12, 31);
        ExpirationDate expDate = ExpirationDate.of(date);

        assertEquals("2026-12-31", expDate.toString());
    }
}
