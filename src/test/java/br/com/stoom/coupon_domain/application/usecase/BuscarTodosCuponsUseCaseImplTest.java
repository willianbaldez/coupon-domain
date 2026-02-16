package br.com.stoom.coupon_domain.application.usecase;

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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarTodosCuponsUseCase")
class BuscarTodosCuponsUseCaseImplTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private BuscarTodosCuponsUseCaseImpl buscarTodosCuponsUseCase;

    private Coupon createCoupon(String code) {
        return Coupon.reconstitute(
                UUID.randomUUID(),
                CouponCode.reconstitute(code),
                "Cupom " + code,
                DiscountValue.reconstitute(new BigDecimal("10.00")),
                ExpirationDate.reconstitute(LocalDate.now().plusDays(30)),
                true, false, null, LocalDateTime.now().minusDays(5)
        );
    }

    @Nested
    @DisplayName("Cenários de sucesso")
    class SuccessScenarios {

        @Test
        @DisplayName("deve retornar lista de cupons")
        void shouldReturnCouponList() {
            List<Coupon> coupons = List.of(createCoupon("CUP001"), createCoupon("CUP002"));
            when(couponRepository.findAll()).thenReturn(coupons);

            List<Coupon> result = buscarTodosCuponsUseCase.execute();

            assertEquals(2, result.size());
            verify(couponRepository).findAll();
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há cupons")
        void shouldReturnEmptyListWhenNoCoupons() {
            when(couponRepository.findAll()).thenReturn(Collections.emptyList());

            List<Coupon> result = buscarTodosCuponsUseCase.execute();

            assertTrue(result.isEmpty());
            verify(couponRepository).findAll();
        }
    }
}
