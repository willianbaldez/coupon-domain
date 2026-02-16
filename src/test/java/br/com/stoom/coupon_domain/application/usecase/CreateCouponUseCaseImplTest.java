package br.com.stoom.coupon_domain.application.usecase;

import br.com.stoom.coupon_domain.application.port.in.CreateCouponUseCase.CreateCouponCommand;
import br.com.stoom.coupon_domain.domain.exception.CouponCodeAlreadyExistsException;
import br.com.stoom.coupon_domain.domain.exception.InvalidCouponCodeException;
import br.com.stoom.coupon_domain.domain.exception.InvalidDescriptionException;
import br.com.stoom.coupon_domain.domain.exception.InvalidDiscountValueException;
import br.com.stoom.coupon_domain.domain.exception.InvalidExpirationDateException;
import br.com.stoom.coupon_domain.domain.model.Coupon;
import br.com.stoom.coupon_domain.domain.port.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateCouponUseCase")
class CreateCouponUseCaseImplTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CreateCouponUseCaseImpl createCouponUseCase;

    private CreateCouponCommand validCommand;

    @BeforeEach
    void setUp() {
        validCommand = new CreateCouponCommand(
                "ABC123",
                "Cupom de desconto",
                new BigDecimal("10.00"),
                LocalDate.now().plusDays(30),
                false
        );
    }

    @Nested
    @DisplayName("Cenários de sucesso")
    class SuccessScenarios {

        @Test
        @DisplayName("deve criar e salvar um cupom válido")
        void shouldCreateAndSaveValidCoupon() {
            when(couponRepository.existsByCode(anyString())).thenReturn(false);
            when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Coupon result = createCouponUseCase.execute(validCommand);

            assertNotNull(result);
            assertEquals("ABC123", result.getCode().value());
            assertEquals("Cupom de desconto", result.getDescription());
            assertFalse(result.isPublished());
            verify(couponRepository).existsByCode("ABC123");
            verify(couponRepository).save(any(Coupon.class));
        }

        @Test
        @DisplayName("deve criar um cupom publicado")
        void shouldCreatePublishedCoupon() {
            CreateCouponCommand publishedCommand = new CreateCouponCommand(
                    "PUB001", "Publicado", new BigDecimal("5.00"),
                    LocalDate.now().plusDays(10), true
            );
            when(couponRepository.existsByCode(anyString())).thenReturn(false);
            when(couponRepository.save(any(Coupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Coupon result = createCouponUseCase.execute(publishedCommand);

            assertTrue(result.isPublished());
        }
    }

    @Nested
    @DisplayName("Cenários de erro - código duplicado")
    class DuplicateCodeScenarios {

        @Test
        @DisplayName("deve lançar exceção quando código já existe")
        void shouldThrowWhenCodeAlreadyExists() {
            when(couponRepository.existsByCode("ABC123")).thenReturn(true);

            CouponCodeAlreadyExistsException ex = assertThrows(
                    CouponCodeAlreadyExistsException.class,
                    () -> createCouponUseCase.execute(validCommand)
            );

            assertTrue(ex.getMessage().contains("ABC123"));
            verify(couponRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Cenários de erro - validação do domínio")
    class DomainValidationScenarios {

        @Test
        @DisplayName("deve propagar exceção de código inválido")
        void shouldPropagateInvalidCodeException() {
            CreateCouponCommand invalidCode = new CreateCouponCommand(
                    "AB", "Descrição", new BigDecimal("10.00"),
                    LocalDate.now().plusDays(30), false
            );

            assertThrows(InvalidCouponCodeException.class,
                    () -> createCouponUseCase.execute(invalidCode));

            verify(couponRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve propagar exceção de desconto inválido")
        void shouldPropagateInvalidDiscountException() {
            CreateCouponCommand invalidDiscount = new CreateCouponCommand(
                    "ABC123", "Descrição", new BigDecimal("0.1"),
                    LocalDate.now().plusDays(30), false
            );

            assertThrows(InvalidDiscountValueException.class,
                    () -> createCouponUseCase.execute(invalidDiscount));

            verify(couponRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve propagar exceção de data de expiração no passado")
        void shouldPropagateInvalidExpirationDateException() {
            CreateCouponCommand pastDate = new CreateCouponCommand(
                    "ABC123", "Descrição", new BigDecimal("10.00"),
                    LocalDate.now().minusDays(1), false
            );

            assertThrows(InvalidExpirationDateException.class,
                    () -> createCouponUseCase.execute(pastDate));

            verify(couponRepository, never()).save(any());
        }

        @Test
        @DisplayName("deve propagar exceção de descrição em branco")
        void shouldPropagateBlankDescriptionException() {
            CreateCouponCommand blankDesc = new CreateCouponCommand(
                    "ABC123", "   ", new BigDecimal("10.00"),
                    LocalDate.now().plusDays(30), false
            );

            assertThrows(InvalidDescriptionException.class,
                    () -> createCouponUseCase.execute(blankDesc));

            verify(couponRepository, never()).save(any());
        }
    }
}
