package br.com.stoom.coupon_domain.application.usecase;

import br.com.stoom.coupon_domain.domain.exception.CouponNotFoundException;
import br.com.stoom.coupon_domain.domain.model.Coupon;
import br.com.stoom.coupon_domain.domain.model.CouponCode;
import br.com.stoom.coupon_domain.domain.model.DiscountValue;
import br.com.stoom.coupon_domain.domain.model.ExpirationDate;
import br.com.stoom.coupon_domain.domain.port.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarCupomPorCodigoUseCase")
class BuscarCupomPorCodigoUseCaseImplTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private BuscarCupomPorCodigoUseCaseImpl buscarCupomPorCodigoUseCase;

    private Coupon createCoupon(String code) {
        return Coupon.reconstitute(
                UUID.randomUUID(),
                CouponCode.reconstitute(code),
                "Cupom teste",
                DiscountValue.reconstitute(new BigDecimal("10.00")),
                ExpirationDate.reconstitute(LocalDate.now().plusDays(30)),
                true, false, null, LocalDateTime.now().minusDays(5)
        );
    }

    @Nested
    @DisplayName("Cenários de sucesso")
    class SuccessScenarios {

        @Test
        @DisplayName("deve retornar cupom quando encontrado pelo código")
        void shouldReturnCouponWhenFoundByCode() {
            Coupon coupon = createCoupon("ABC123");
            when(couponRepository.findByCode("ABC123")).thenReturn(Optional.of(coupon));

            Coupon result = buscarCupomPorCodigoUseCase.execute("ABC123");

            assertEquals("ABC123", result.getCode().value());
            verify(couponRepository).findByCode("ABC123");
        }

        @Test
        @DisplayName("deve normalizar código para uppercase antes de buscar")
        void shouldNormalizeCodeToUppercase() {
            Coupon coupon = createCoupon("ABC123");
            when(couponRepository.findByCode("ABC123")).thenReturn(Optional.of(coupon));

            Coupon result = buscarCupomPorCodigoUseCase.execute("abc123");

            assertEquals("ABC123", result.getCode().value());
            verify(couponRepository).findByCode("ABC123");
        }
    }

    @Nested
    @DisplayName("Cenários de erro")
    class ErrorScenarios {

        @Test
        @DisplayName("deve lançar exceção quando cupom não é encontrado")
        void shouldThrowWhenCouponNotFound() {
            when(couponRepository.findByCode("XYZ999")).thenReturn(Optional.empty());

            CouponNotFoundException ex = assertThrows(
                    CouponNotFoundException.class,
                    () -> buscarCupomPorCodigoUseCase.execute("XYZ999")
            );

            assertTrue(ex.getMessage().contains("XYZ999"));
        }
    }
}
