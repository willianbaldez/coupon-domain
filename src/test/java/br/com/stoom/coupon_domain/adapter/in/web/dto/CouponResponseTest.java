package br.com.stoom.coupon_domain.adapter.in.web.dto;

import br.com.stoom.coupon_domain.domain.model.Coupon;
import br.com.stoom.coupon_domain.domain.model.CouponCode;
import br.com.stoom.coupon_domain.domain.model.DiscountValue;
import br.com.stoom.coupon_domain.domain.model.ExpirationDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CouponResponse")
class CouponResponseTest {

    @Test
    @DisplayName("deve converter cupom ativo para response com todos os campos")
    void shouldConvertActiveCouponToResponse() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(5);
        LocalDate expirationDate = LocalDate.now().plusDays(30);

        Coupon coupon = Coupon.reconstitute(
                id, CouponCode.reconstitute("ABC123"), "Cupom teste",
                DiscountValue.reconstitute(new BigDecimal("25.00")),
                ExpirationDate.reconstitute(expirationDate),
                true, false, null, createdAt
        );

        CouponResponse response = CouponResponse.from(coupon);

        assertEquals(id, response.id());
        assertEquals("ABC123", response.code());
        assertEquals("Cupom teste", response.description());
        assertEquals(new BigDecimal("25.00"), response.discountValue());
        assertEquals(expirationDate, response.expirationDate());
        assertTrue(response.published());
        assertFalse(response.deleted());
        assertNull(response.deletedAt());
        assertEquals(createdAt, response.createdAt());
        assertFalse(response.expired());
        assertTrue(response.active());
    }

    @Test
    @DisplayName("deve converter cupom excluído para response")
    void shouldConvertDeletedCouponToResponse() {
        LocalDateTime deletedAt = LocalDateTime.now().minusDays(1);

        Coupon coupon = Coupon.reconstitute(
                UUID.randomUUID(), CouponCode.reconstitute("DEL001"), "Excluído",
                DiscountValue.reconstitute(new BigDecimal("10.00")),
                ExpirationDate.reconstitute(LocalDate.now().plusDays(10)),
                false, true, deletedAt, LocalDateTime.now().minusDays(5)
        );

        CouponResponse response = CouponResponse.from(coupon);

        assertTrue(response.deleted());
        assertEquals(deletedAt, response.deletedAt());
        assertFalse(response.active());
    }

    @Test
    @DisplayName("deve converter cupom expirado para response")
    void shouldConvertExpiredCouponToResponse() {
        Coupon coupon = Coupon.reconstitute(
                UUID.randomUUID(), CouponCode.reconstitute("EXP001"), "Expirado",
                DiscountValue.reconstitute(new BigDecimal("5.00")),
                ExpirationDate.reconstitute(LocalDate.now().minusDays(1)),
                true, false, null, LocalDateTime.now().minusDays(30)
        );

        CouponResponse response = CouponResponse.from(coupon);

        assertTrue(response.expired());
        assertFalse(response.active());
        assertFalse(response.deleted());
    }
}
