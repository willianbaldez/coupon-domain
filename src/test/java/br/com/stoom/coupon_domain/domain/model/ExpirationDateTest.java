package br.com.stoom.coupon_domain.domain.model;

import br.com.stoom.coupon_domain.domain.exception.InvalidExpirationDateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ExpirationDate - Value Object")
class ExpirationDateTest {

    @Nested
    @DisplayName("Criação via of()")
    class Creation {

        @Test
        @DisplayName("deve criar com uma data futura")
        void shouldCreateWithFutureDate() {
            LocalDate future = LocalDate.now().plusDays(30);
            ExpirationDate date = ExpirationDate.of(future);

            assertEquals(future, date.value());
        }

        @Test
        @DisplayName("deve criar com a data de hoje (hoje não está no passado)")
        void shouldCreateWithToday() {
            LocalDate today = LocalDate.now();
            ExpirationDate date = ExpirationDate.of(today);

            assertEquals(today, date.value());
        }

        @Test
        @DisplayName("deve rejeitar data nula")
        void shouldRejectNull() {
            InvalidExpirationDateException ex = assertThrows(
                    InvalidExpirationDateException.class,
                    () -> ExpirationDate.of(null)
            );
            assertTrue(ex.getMessage().contains("obrigatória"));
        }

        @Test
        @DisplayName("deve rejeitar data no passado")
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
    @DisplayName("Reconstituição")
    class Reconstitution {

        @Test
        @DisplayName("deve reconstituir data passada sem validação (a partir da persistência)")
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
        @DisplayName("deve retornar falso para data futura")
        void shouldNotBeExpiredForFutureDate() {
            ExpirationDate date = ExpirationDate.of(LocalDate.now().plusDays(10));

            assertFalse(date.isExpired());
        }

        @Test
        @DisplayName("deve retornar falso para a data de hoje")
        void shouldNotBeExpiredForToday() {
            ExpirationDate date = ExpirationDate.of(LocalDate.now());

            assertFalse(date.isExpired());
        }

        @Test
        @DisplayName("deve retornar verdadeiro para data passada (reconstituída)")
        void shouldBeExpiredForPastDate() {
            ExpirationDate date = ExpirationDate.reconstitute(LocalDate.now().minusDays(1));

            assertTrue(date.isExpired());
        }
    }

    @Nested
    @DisplayName("Igualdade")
    class Equality {

        @Test
        @DisplayName("deve ser igual quando as datas são iguais")
        void shouldBeEqualForSameDate() {
            LocalDate date = LocalDate.now().plusDays(5);
            ExpirationDate d1 = ExpirationDate.of(date);
            ExpirationDate d2 = ExpirationDate.of(date);

            assertEquals(d1, d2);
            assertEquals(d1.hashCode(), d2.hashCode());
        }

        @Test
        @DisplayName("não deve ser igual quando as datas são diferentes")
        void shouldNotBeEqualForDifferentDates() {
            ExpirationDate d1 = ExpirationDate.of(LocalDate.now().plusDays(1));
            ExpirationDate d2 = ExpirationDate.of(LocalDate.now().plusDays(2));

            assertNotEquals(d1, d2);
        }
    }

    @Test
    @DisplayName("toString deve retornar a data no formato ISO")
    void toStringShouldReturnIsoDate() {
        LocalDate date = LocalDate.of(2026, 12, 31);
        ExpirationDate expDate = ExpirationDate.of(date);

        assertEquals("2026-12-31", expDate.toString());
    }
}
