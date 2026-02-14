package br.com.stoom.coupon_domain.domain.model;

import br.com.stoom.coupon_domain.domain.exception.CouponAlreadyDeletedException;
import br.com.stoom.coupon_domain.domain.exception.InvalidCouponCodeException;
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

@DisplayName("Coupon Aggregate Root")
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
        @DisplayName("should create a valid coupon with all fields populated")
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
        @DisplayName("should create a published coupon")
        void shouldCreatePublishedCoupon() {
            Coupon coupon = Coupon.create(VALID_CODE, VALID_DESCRIPTION, VALID_DISCOUNT, VALID_EXPIRATION, true);

            assertTrue(coupon.isPublished());
        }

        @Test
        @DisplayName("should generate a unique ID for each coupon")
        void shouldGenerateUniqueId() {
            Coupon coupon1 = createValidCoupon();
            Coupon coupon2 = createValidCoupon();

            assertNotEquals(coupon1.getId(), coupon2.getId());
        }

        @Test
        @DisplayName("should trim description whitespace")
        void shouldTrimDescription() {
            Coupon coupon = Coupon.create(VALID_CODE, "  spaced  ", VALID_DISCOUNT, VALID_EXPIRATION, false);

            assertEquals("spaced", coupon.getDescription());
        }

        @Test
        @DisplayName("should sanitize coupon code (remove special characters)")
        void shouldSanitizeCode() {
            Coupon coupon = Coupon.create("A-B.C!1@2#3", VALID_DESCRIPTION, VALID_DISCOUNT, VALID_EXPIRATION, false);

            assertEquals("ABC123", coupon.getCode().value());
        }

        @Test
        @DisplayName("should reject null description")
        void shouldRejectNullDescription() {
            assertThrows(IllegalArgumentException.class,
                    () -> Coupon.create(VALID_CODE, null, VALID_DISCOUNT, VALID_EXPIRATION, false));
        }

        @Test
        @DisplayName("should reject blank description")
        void shouldRejectBlankDescription() {
            assertThrows(IllegalArgumentException.class,
                    () -> Coupon.create(VALID_CODE, "   ", VALID_DISCOUNT, VALID_EXPIRATION, false));
        }

        @Test
        @DisplayName("should reject invalid coupon code")
        void shouldRejectInvalidCode() {
            assertThrows(InvalidCouponCodeException.class,
                    () -> Coupon.create("AB", VALID_DESCRIPTION, VALID_DISCOUNT, VALID_EXPIRATION, false));
        }

        @Test
        @DisplayName("should reject discount below minimum")
        void shouldRejectLowDiscount() {
            assertThrows(InvalidDiscountValueException.class,
                    () -> Coupon.create(VALID_CODE, VALID_DESCRIPTION, new BigDecimal("0.1"), VALID_EXPIRATION, false));
        }

        @Test
        @DisplayName("should reject past expiration date")
        void shouldRejectPastExpiration() {
            LocalDate yesterday = LocalDate.now().minusDays(1);

            assertThrows(InvalidExpirationDateException.class,
                    () -> Coupon.create(VALID_CODE, VALID_DESCRIPTION, VALID_DISCOUNT, yesterday, false));
        }
    }

    @Nested
    @DisplayName("Reconstitution")
    class Reconstitution {

        @Test
        @DisplayName("should reconstitute coupon from persisted data")
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
        @DisplayName("should reconstitute a deleted coupon")
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
        @DisplayName("should soft-delete a coupon")
        void shouldSoftDelete() {
            Coupon coupon = createValidCoupon();

            coupon.delete();

            assertTrue(coupon.isDeleted());
            assertNotNull(coupon.getDeletedAt());
        }

        @Test
        @DisplayName("should throw when deleting an already deleted coupon")
        void shouldRejectDoubleDelete() {
            Coupon coupon = createValidCoupon();
            coupon.delete();

            CouponAlreadyDeletedException ex = assertThrows(
                    CouponAlreadyDeletedException.class,
                    coupon::delete
            );
            assertTrue(ex.getMessage().contains("already been deleted"));
        }
    }

    @Nested
    @DisplayName("isExpired()")
    class IsExpired {

        @Test
        @DisplayName("should return false when expiration date is in the future")
        void shouldNotBeExpired() {
            Coupon coupon = createValidCoupon();

            assertFalse(coupon.isExpired());
        }

        @Test
        @DisplayName("should return true when expiration date is in the past (reconstituted)")
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
        @DisplayName("should be active when not deleted and not expired")
        void shouldBeActive() {
            Coupon coupon = createValidCoupon();

            assertTrue(coupon.isActive());
        }

        @Test
        @DisplayName("should not be active when deleted")
        void shouldNotBeActiveWhenDeleted() {
            Coupon coupon = createValidCoupon();
            coupon.delete();

            assertFalse(coupon.isActive());
        }

        @Test
        @DisplayName("should not be active when expired")
        void shouldNotBeActiveWhenExpired() {
            Coupon coupon = Coupon.reconstitute(UUID.randomUUID(),
                    CouponCode.reconstitute("EXP001"), "Expired",
                    DiscountValue.reconstitute(new BigDecimal("5.00")),
                    ExpirationDate.reconstitute(LocalDate.now().minusDays(1)),
                    true, false, null, LocalDateTime.now().minusDays(30));

            assertFalse(coupon.isActive());
        }

        @Test
        @DisplayName("should not be active when both deleted and expired")
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
    @DisplayName("Identity-based equality")
    class EqualityTests {

        @Test
        @DisplayName("should be equal when IDs match")
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
        @DisplayName("should not be equal when IDs differ")
        void shouldNotBeEqualForDifferentIds() {
            Coupon c1 = createValidCoupon();
            Coupon c2 = createValidCoupon();

            assertNotEquals(c1, c2);
        }
    }
}
