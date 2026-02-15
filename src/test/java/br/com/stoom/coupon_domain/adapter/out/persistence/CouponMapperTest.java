package br.com.stoom.coupon_domain.adapter.out.persistence;

import br.com.stoom.coupon_domain.domain.model.Coupon;
import br.com.stoom.coupon_domain.domain.model.CouponCode;
import br.com.stoom.coupon_domain.domain.model.DiscountValue;
import br.com.stoom.coupon_domain.domain.model.ExpirationDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CouponMapper")
class CouponMapperTest {

    private final UUID id = UUID.randomUUID();
    private final String code = "ABC123";
    private final String description = "Cupom de teste";
    private final BigDecimal discountValue = new BigDecimal("15.00");
    private final LocalDate expirationDate = LocalDate.now().plusDays(30);
    private final LocalDateTime createdAt = LocalDateTime.now().minusDays(5);
    private final LocalDateTime deletedAt = LocalDateTime.now().minusDays(1);

    @Nested
    @DisplayName("Conversão de domínio para entidade JPA")
    class ToJpaEntity {

        @Test
        @DisplayName("deve converter cupom ativo para entidade JPA")
        void shouldConvertActiveCouponToJpaEntity() {
            Coupon coupon = Coupon.reconstitute(
                    id, CouponCode.reconstitute(code), description,
                    DiscountValue.reconstitute(discountValue),
                    ExpirationDate.reconstitute(expirationDate),
                    true, false, null, createdAt
            );

            CouponEntity entity = CouponMapper.toJpaEntity(coupon);

            assertEquals(id, entity.getId());
            assertEquals(code, entity.getCode());
            assertEquals(description, entity.getDescription());
            assertEquals(discountValue, entity.getDiscountValue());
            assertEquals(expirationDate, entity.getExpirationDate());
            assertTrue(entity.isPublished());
            assertFalse(entity.isDeleted());
            assertNull(entity.getDeletedAt());
            assertEquals(createdAt, entity.getCreatedAt());
        }

        @Test
        @DisplayName("deve converter cupom excluído para entidade JPA")
        void shouldConvertDeletedCouponToJpaEntity() {
            Coupon coupon = Coupon.reconstitute(
                    id, CouponCode.reconstitute(code), description,
                    DiscountValue.reconstitute(discountValue),
                    ExpirationDate.reconstitute(expirationDate),
                    false, true, deletedAt, createdAt
            );

            CouponEntity entity = CouponMapper.toJpaEntity(coupon);

            assertTrue(entity.isDeleted());
            assertEquals(deletedAt, entity.getDeletedAt());
            assertFalse(entity.isPublished());
        }
    }

    @Nested
    @DisplayName("Conversão de entidade JPA para domínio")
    class ToDomain {

        @Test
        @DisplayName("deve converter entidade JPA ativa para domínio")
        void shouldConvertActiveEntityToDomain() {
            CouponEntity entity = new CouponEntity(
                    id, code, description, discountValue,
                    expirationDate, true, false, null, createdAt
            );

            Coupon coupon = CouponMapper.toDomain(entity);

            assertEquals(id, coupon.getId());
            assertEquals(code, coupon.getCode().value());
            assertEquals(description, coupon.getDescription());
            assertEquals(discountValue, coupon.getDiscountValue().value());
            assertEquals(expirationDate, coupon.getExpirationDate().value());
            assertTrue(coupon.isPublished());
            assertFalse(coupon.isDeleted());
            assertNull(coupon.getDeletedAt());
            assertEquals(createdAt, coupon.getCreatedAt());
        }

        @Test
        @DisplayName("deve converter entidade JPA excluída para domínio")
        void shouldConvertDeletedEntityToDomain() {
            CouponEntity entity = new CouponEntity(
                    id, code, description, discountValue,
                    expirationDate, false, true, deletedAt, createdAt
            );

            Coupon coupon = CouponMapper.toDomain(entity);

            assertTrue(coupon.isDeleted());
            assertEquals(deletedAt, coupon.getDeletedAt());
            assertFalse(coupon.isPublished());
        }
    }

    @Nested
    @DisplayName("Conversão bidirecional")
    class RoundTrip {

        @Test
        @DisplayName("deve manter todos os campos após conversão domínio -> JPA -> domínio")
        void shouldPreserveFieldsAfterRoundTrip() {
            Coupon original = Coupon.reconstitute(
                    id, CouponCode.reconstitute(code), description,
                    DiscountValue.reconstitute(discountValue),
                    ExpirationDate.reconstitute(expirationDate),
                    true, false, null, createdAt
            );

            CouponEntity entity = CouponMapper.toJpaEntity(original);
            Coupon reconstituted = CouponMapper.toDomain(entity);

            assertEquals(original.getId(), reconstituted.getId());
            assertEquals(original.getCode().value(), reconstituted.getCode().value());
            assertEquals(original.getDescription(), reconstituted.getDescription());
            assertEquals(original.getDiscountValue().value(), reconstituted.getDiscountValue().value());
            assertEquals(original.getExpirationDate().value(), reconstituted.getExpirationDate().value());
            assertEquals(original.isPublished(), reconstituted.isPublished());
            assertEquals(original.isDeleted(), reconstituted.isDeleted());
            assertEquals(original.getDeletedAt(), reconstituted.getDeletedAt());
            assertEquals(original.getCreatedAt(), reconstituted.getCreatedAt());
        }
    }
}
