package br.com.stoom.coupon_domain.adapter.out.persistence;

import br.com.stoom.coupon_domain.domain.model.Coupon;
import br.com.stoom.coupon_domain.domain.model.CouponCode;
import br.com.stoom.coupon_domain.domain.model.DiscountValue;
import br.com.stoom.coupon_domain.domain.model.ExpirationDate;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponAdapter")
class CouponAdapterTest {

    @Mock
    private CouponJpaRepository couponJpaRepository;

    @InjectMocks
    private CouponAdapter couponAdapter;

    private CouponEntity createEntity(UUID id, String code) {
        return new CouponEntity(
                id, code, "Descrição", new BigDecimal("10.00"),
                LocalDate.now().plusDays(30), true, false, null,
                LocalDateTime.now().minusDays(5)
        );
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("deve salvar cupom e retornar domínio reconstituído")
        void shouldSaveCouponAndReturnDomain() {
            UUID id = UUID.randomUUID();
            CouponEntity entity = createEntity(id, "ABC123");
            when(couponJpaRepository.save(any(CouponEntity.class))).thenReturn(entity);

            Coupon coupon = Coupon.reconstitute(
                    id,
                    CouponCode.reconstitute("ABC123"),
                    "Descrição",
                    DiscountValue.reconstitute(new BigDecimal("10.00")),
                    ExpirationDate.reconstitute(LocalDate.now().plusDays(30)),
                    true, false, null, LocalDateTime.now().minusDays(5)
            );

            Coupon result = couponAdapter.save(coupon);

            assertNotNull(result);
            assertEquals(id, result.getId());
            assertEquals("ABC123", result.getCode().value());
            verify(couponJpaRepository).save(any(CouponEntity.class));
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("deve retornar cupom quando encontrado por ID")
        void shouldReturnCouponWhenFoundById() {
            UUID id = UUID.randomUUID();
            CouponEntity entity = createEntity(id, "FND001");
            when(couponJpaRepository.findById(id)).thenReturn(Optional.of(entity));

            Optional<Coupon> result = couponAdapter.findById(id);

            assertTrue(result.isPresent());
            assertEquals(id, result.get().getId());
            assertEquals("FND001", result.get().getCode().value());
        }

        @Test
        @DisplayName("deve retornar vazio quando cupom não é encontrado por ID")
        void shouldReturnEmptyWhenNotFoundById() {
            UUID id = UUID.randomUUID();
            when(couponJpaRepository.findById(id)).thenReturn(Optional.empty());

            Optional<Coupon> result = couponAdapter.findById(id);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("findByCode")
    class FindByCode {

        @Test
        @DisplayName("deve retornar cupom quando encontrado por código")
        void shouldReturnCouponWhenFoundByCode() {
            UUID id = UUID.randomUUID();
            CouponEntity entity = createEntity(id, "BYC001");
            when(couponJpaRepository.findByCode("BYC001")).thenReturn(Optional.of(entity));

            Optional<Coupon> result = couponAdapter.findByCode("BYC001");

            assertTrue(result.isPresent());
            assertEquals("BYC001", result.get().getCode().value());
        }

        @Test
        @DisplayName("deve retornar vazio quando cupom não é encontrado por código")
        void shouldReturnEmptyWhenNotFoundByCode() {
            when(couponJpaRepository.findByCode("XXX999")).thenReturn(Optional.empty());

            Optional<Coupon> result = couponAdapter.findByCode("XXX999");

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("deve retornar lista de cupons convertidos para domínio")
        void shouldReturnListOfDomainCoupons() {
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();
            List<CouponEntity> entities = List.of(
                    createEntity(id1, "LST001"),
                    createEntity(id2, "LST002")
            );
            when(couponJpaRepository.findAll()).thenReturn(entities);

            List<Coupon> result = couponAdapter.findAll();

            assertEquals(2, result.size());
            assertEquals("LST001", result.get(0).getCode().value());
            assertEquals("LST002", result.get(1).getCode().value());
            verify(couponJpaRepository).findAll();
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há cupons")
        void shouldReturnEmptyListWhenNoCoupons() {
            when(couponJpaRepository.findAll()).thenReturn(Collections.emptyList());

            List<Coupon> result = couponAdapter.findAll();

            assertTrue(result.isEmpty());
            verify(couponJpaRepository).findAll();
        }
    }

    @Nested
    @DisplayName("existsByCode")
    class ExistsByCode {

        @Test
        @DisplayName("deve retornar true quando código existe")
        void shouldReturnTrueWhenCodeExists() {
            when(couponJpaRepository.existsByCode("ABC123")).thenReturn(true);

            assertTrue(couponAdapter.existsByCode("ABC123"));
        }

        @Test
        @DisplayName("deve retornar false quando código não existe")
        void shouldReturnFalseWhenCodeDoesNotExist() {
            when(couponJpaRepository.existsByCode("ZZZ999")).thenReturn(false);

            assertFalse(couponAdapter.existsByCode("ZZZ999"));
        }
    }
}
