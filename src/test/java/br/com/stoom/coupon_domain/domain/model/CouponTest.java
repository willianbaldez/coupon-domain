package br.com.stoom.coupon_domain.domain.model;

import br.com.stoom.coupon_domain.domain.exception.CouponAlreadyDeletedException;
import br.com.stoom.coupon_domain.domain.exception.InvalidCouponCodeException;
import br.com.stoom.coupon_domain.domain.exception.InvalidDescriptionException;
import br.com.stoom.coupon_domain.domain.exception.InvalidDiscountValueException;
import br.com.stoom.coupon_domain.domain.exception.InvalidExpirationDateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Coupon - Aggregate Root")
class CouponTest {

    private static final String VALID_CODE = "ABC123";
    private static final String VALID_DESCRIPTION = "10% off on all items";
    private static final BigDecimal VALID_DISCOUNT = new BigDecimal("10.00");
    private static final LocalDate VALID_EXPIRATION = LocalDate.now().plusDays(30);

    private Coupon createValidCoupon() {
        return Coupon.create(VALID_CODE, VALID_DESCRIPTION, VALID_DISCOUNT, VALID_EXPIRATION, false);
    }

    @Nested
    @DisplayName("Factory method create()")
    class FactoryCreate {

        @Test
        @DisplayName("deve criar um cupom válido com todos os campos preenchidos")
        void shouldCreateValidCoupon() {
            Coupon coupon = createValidCoupon();

            assertNotNull(coupon.getId());
            assertEquals("ABC123", coupon.getCode().value());
            assertEquals("10% off on all items", coupon.getDescription());
            assertEquals(new BigDecimal("10.00"), coupon.getDiscountValue().value());
            assertEquals(VALID_EXPIRATION, coupon.getExpirationDate().value());
            assertFalse(coupon.isPublished());
            assertFalse(coupon.isDeleted());
            assertNull(coupon.getDeletedAt());
            assertNotNull(coupon.getCreatedAt());
        }

        @Test
        @DisplayName("deve criar um cupom já publicado")
        void shouldCreatePublishedCoupon() {
            Coupon coupon = Coupon.create(VALID_CODE, VALID_DESCRIPTION, VALID_DISCOUNT, VALID_EXPIRATION, true);

            assertTrue(coupon.isPublished());
        }

        @Test
        @DisplayName("deve gerar um ID único para cada cupom")
        void shouldGenerateUniqueId() {
            Coupon coupon1 = createValidCoupon();
            Coupon coupon2 = createValidCoupon();

            assertNotEquals(coupon1.getId(), coupon2.getId());
        }

        @Test
        @DisplayName("deve remover espaços em branco da descrição")
        void shouldTrimDescription() {
            Coupon coupon = Coupon.create(VALID_CODE, "  spaced  ", VALID_DISCOUNT, VALID_EXPIRATION, false);

            assertEquals("spaced", coupon.getDescription());
        }

        @Test
        @DisplayName("deve sanitizar o código do cupom (remover caracteres especiais)")
        void shouldSanitizeCode() {
            Coupon coupon = Coupon.create("A-B.C!1@2#3", VALID_DESCRIPTION, VALID_DISCOUNT, VALID_EXPIRATION, false);

            assertEquals("ABC123", coupon.getCode().value());
        }

        @Test
        @DisplayName("deve rejeitar descrição nula")
        void shouldRejectNullDescription() {
            assertThrows(InvalidDescriptionException.class,
                    () -> Coupon.create(VALID_CODE, null, VALID_DISCOUNT, VALID_EXPIRATION, false));
        }

        @Test
        @DisplayName("deve rejeitar descrição em branco")
        void shouldRejectBlankDescription() {
            assertThrows(InvalidDescriptionException.class,
                    () -> Coupon.create(VALID_CODE, "   ", VALID_DISCOUNT, VALID_EXPIRATION, false));
        }

        @Test
        @DisplayName("deve rejeitar código de cupom inválido")
        void shouldRejectInvalidCode() {
            assertThrows(InvalidCouponCodeException.class,
                    () -> Coupon.create("AB", VALID_DESCRIPTION, VALID_DISCOUNT, VALID_EXPIRATION, false));
        }

        @Test
        @DisplayName("deve rejeitar desconto abaixo do mínimo")
        void shouldRejectLowDiscount() {
            assertThrows(InvalidDiscountValueException.class,
                    () -> Coupon.create(VALID_CODE, VALID_DESCRIPTION, new BigDecimal("0.1"), VALID_EXPIRATION, false));
        }

        @Test
        @DisplayName("deve rejeitar data de expiração no passado")
        void shouldRejectPastExpiration() {
            LocalDate yesterday = LocalDate.now().minusDays(1);

            assertThrows(InvalidExpirationDateException.class,
                    () -> Coupon.create(VALID_CODE, VALID_DESCRIPTION, VALID_DISCOUNT, yesterday, false));
        }
    }

    @Nested
    @DisplayName("Reconstituição")
    class Reconstitution {

        @Test
        @DisplayName("deve reconstituir cupom a partir de dados persistidos")
        void shouldReconstituteCoupon() {
            UUID id = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.now().minusDays(10);
            CouponCode code = CouponCode.reconstitute("XYZ789");
            DiscountValue discount = DiscountValue.reconstitute(new BigDecimal("5.00"));
            ExpirationDate expDate = ExpirationDate.reconstitute(LocalDate.now().minusDays(5));

            Coupon coupon = Coupon.reconstitute(id, code, "Old coupon", discount, expDate,
                    true, false, null, createdAt);

            assertEquals(id, coupon.getId());
            assertEquals("XYZ789", coupon.getCode().value());
            assertEquals("Old coupon", coupon.getDescription());
            assertTrue(coupon.isPublished());
            assertFalse(coupon.isDeleted());
            assertEquals(createdAt, coupon.getCreatedAt());
        }

        @Test
        @DisplayName("deve reconstituir um cupom excluído")
        void shouldReconstituteDeletedCoupon() {
            UUID id = UUID.randomUUID();
            LocalDateTime deletedAt = LocalDateTime.now().minusDays(1);

            Coupon coupon = Coupon.reconstitute(id, CouponCode.reconstitute("DEL001"),
                    "Deleted coupon", DiscountValue.reconstitute(new BigDecimal("2.00")),
                    ExpirationDate.reconstitute(LocalDate.now().plusDays(10)),
                    false, true, deletedAt, LocalDateTime.now().minusDays(5));

            assertTrue(coupon.isDeleted());
            assertEquals(deletedAt, coupon.getDeletedAt());
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("deve realizar soft delete do cupom")
        void shouldSoftDelete() {
            Coupon coupon = createValidCoupon();

            coupon.delete();

            assertTrue(coupon.isDeleted());
            assertNotNull(coupon.getDeletedAt());
        }

        @Test
        @DisplayName("deve lançar exceção ao tentar excluir cupom já excluído")
        void shouldRejectDoubleDelete() {
            Coupon coupon = createValidCoupon();
            coupon.delete();

            CouponAlreadyDeletedException ex = assertThrows(
                    CouponAlreadyDeletedException.class,
                    coupon::delete
            );
            assertTrue(ex.getMessage().contains("já foi excluído"));
        }
    }

    @Nested
    @DisplayName("isExpired()")
    class IsExpired {

        @Test
        @DisplayName("deve retornar falso quando a data de expiração está no futuro")
        void shouldNotBeExpired() {
            Coupon coupon = createValidCoupon();

            assertFalse(coupon.isExpired());
        }

        @Test
        @DisplayName("deve retornar verdadeiro quando a data de expiração está no passado (reconstituído)")
        void shouldBeExpiredForPastDate() {
            Coupon coupon = Coupon.reconstitute(UUID.randomUUID(),
                    CouponCode.reconstitute("EXP001"), "Expired coupon",
                    DiscountValue.reconstitute(new BigDecimal("5.00")),
                    ExpirationDate.reconstitute(LocalDate.now().minusDays(1)),
                    true, false, null, LocalDateTime.now().minusDays(30));

            assertTrue(coupon.isExpired());
        }
    }

    @Nested
    @DisplayName("isActive()")
    class IsActive {

        @Test
        @DisplayName("deve estar ativo quando não excluído e não expirado")
        void shouldBeActive() {
            Coupon coupon = createValidCoupon();

            assertTrue(coupon.isActive());
        }

        @Test
        @DisplayName("não deve estar ativo quando excluído")
        void shouldNotBeActiveWhenDeleted() {
            Coupon coupon = createValidCoupon();
            coupon.delete();

            assertFalse(coupon.isActive());
        }

        @Test
        @DisplayName("não deve estar ativo quando expirado")
        void shouldNotBeActiveWhenExpired() {
            Coupon coupon = Coupon.reconstitute(UUID.randomUUID(),
                    CouponCode.reconstitute("EXP001"), "Expired",
                    DiscountValue.reconstitute(new BigDecimal("5.00")),
                    ExpirationDate.reconstitute(LocalDate.now().minusDays(1)),
                    true, false, null, LocalDateTime.now().minusDays(30));

            assertFalse(coupon.isActive());
        }

        @Test
        @DisplayName("não deve estar ativo quando excluído e expirado")
        void shouldNotBeActiveWhenDeletedAndExpired() {
            Coupon coupon = Coupon.reconstitute(UUID.randomUUID(),
                    CouponCode.reconstitute("EXP002"), "Expired and deleted",
                    DiscountValue.reconstitute(new BigDecimal("5.00")),
                    ExpirationDate.reconstitute(LocalDate.now().minusDays(1)),
                    true, true, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(30));

            assertFalse(coupon.isActive());
        }
    }

    @Nested
    @DisplayName("Igualdade baseada em identidade")
    class EqualityTests {

        @Test
        @DisplayName("deve ser igual quando os IDs são iguais")
        void shouldBeEqualBySameId() {
            UUID id = UUID.randomUUID();
            Coupon c1 = Coupon.reconstitute(id, CouponCode.reconstitute("ABC123"),
                    "Desc 1", DiscountValue.reconstitute(new BigDecimal("5.00")),
                    ExpirationDate.reconstitute(LocalDate.now().plusDays(10)),
                    false, false, null, LocalDateTime.now());
            Coupon c2 = Coupon.reconstitute(id, CouponCode.reconstitute("XYZ789"),
                    "Desc 2", DiscountValue.reconstitute(new BigDecimal("10.00")),
                    ExpirationDate.reconstitute(LocalDate.now().plusDays(20)),
                    true, false, null, LocalDateTime.now());

            assertEquals(c1, c2);
            assertEquals(c1.hashCode(), c2.hashCode());
        }

        @Test
        @DisplayName("não deve ser igual quando os IDs são diferentes")
        void shouldNotBeEqualForDifferentIds() {
            Coupon c1 = createValidCoupon();
            Coupon c2 = createValidCoupon();

            assertNotEquals(c1, c2);
        }
    }
}
