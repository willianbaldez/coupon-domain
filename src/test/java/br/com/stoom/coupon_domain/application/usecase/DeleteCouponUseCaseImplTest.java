package br.com.stoom.coupon_domain.application.usecase;

import br.com.stoom.coupon_domain.domain.exception.CouponAlreadyDeletedException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteCouponUseCase")
class DeleteCouponUseCaseImplTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private DeleteCouponUseCaseImpl deleteCouponUseCase;

    private Coupon createActiveCoupon(UUID id) {
        return Coupon.reconstitute(
                id,
                CouponCode.reconstitute("ABC123"),
                "Cupom ativo",
                DiscountValue.reconstitute(new BigDecimal("10.00")),
                ExpirationDate.reconstitute(LocalDate.now().plusDays(30)),
                true, false, null, LocalDateTime.now().minusDays(5)
        );
    }

    @Nested
    @DisplayName("Cenários de sucesso")
    class SuccessScenarios {

        @Test
        @DisplayName("deve realizar soft delete do cupom")
        void shouldSoftDeleteCoupon() {
            UUID couponId = UUID.randomUUID();
            Coupon coupon = createActiveCoupon(couponId);
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));
            when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

            deleteCouponUseCase.execute(couponId);

            assertTrue(coupon.isDeleted());
            assertNotNull(coupon.getDeletedAt());
            verify(couponRepository).findById(couponId);
            verify(couponRepository).save(coupon);
        }
    }

    @Nested
    @DisplayName("Cenários de erro")
    class ErrorScenarios {

        @Test
        @DisplayName("deve lançar exceção quando cupom não é encontrado")
        void shouldThrowWhenCouponNotFound() {
            UUID couponId = UUID.randomUUID();
            when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

            CouponNotFoundException ex = assertThrows(
                    CouponNotFoundException.class,
                    () -> deleteCouponUseCase.execute(couponId)
            );

            assertTrue(ex.getMessage().contains(couponId.toString()));
            verify(couponRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve propagar exceção ao tentar excluir cupom já excluído")
        void shouldPropagateAlreadyDeletedException() {
            UUID couponId = UUID.randomUUID();
            Coupon deletedCoupon = Coupon.reconstitute(
                    couponId,
                    CouponCode.reconstitute("DEL001"),
                    "Já excluído",
                    DiscountValue.reconstitute(new BigDecimal("10.00")),
                    ExpirationDate.reconstitute(LocalDate.now().plusDays(30)),
                    true, true, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(5)
            );
            when(couponRepository.findById(couponId)).thenReturn(Optional.of(deletedCoupon));

            assertThrows(CouponAlreadyDeletedException.class,
                    () -> deleteCouponUseCase.execute(couponId));

            verify(couponRepository, never()).save(any());
        }
    }
}
