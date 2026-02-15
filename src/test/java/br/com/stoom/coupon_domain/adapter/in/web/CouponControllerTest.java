package br.com.stoom.coupon_domain.adapter.in.web;

import br.com.stoom.coupon_domain.application.port.in.CreateCouponUseCase;
import br.com.stoom.coupon_domain.application.port.in.CreateCouponUseCase.CreateCouponCommand;
import br.com.stoom.coupon_domain.application.port.in.DeleteCouponUseCase;
import br.com.stoom.coupon_domain.domain.exception.CouponAlreadyDeletedException;
import br.com.stoom.coupon_domain.domain.exception.CouponCodeAlreadyExistsException;
import br.com.stoom.coupon_domain.domain.exception.CouponNotFoundException;
import br.com.stoom.coupon_domain.domain.exception.InvalidCouponCodeException;
import br.com.stoom.coupon_domain.domain.exception.InvalidDiscountValueException;
import br.com.stoom.coupon_domain.domain.exception.InvalidExpirationDateException;
import br.com.stoom.coupon_domain.domain.model.Coupon;
import br.com.stoom.coupon_domain.domain.model.CouponCode;
import br.com.stoom.coupon_domain.domain.model.DiscountValue;
import br.com.stoom.coupon_domain.domain.model.ExpirationDate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
@DisplayName("CouponController")
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCouponUseCase createCouponUseCase;

    @MockitoBean
    private DeleteCouponUseCase deleteCouponUseCase;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private Coupon createSampleCoupon() {
        return Coupon.reconstitute(
                UUID.randomUUID(),
                CouponCode.reconstitute("ABC123"),
                "Cupom de desconto",
                DiscountValue.reconstitute(new BigDecimal("10.00")),
                ExpirationDate.reconstitute(LocalDate.now().plusDays(30)),
                false, false, null, LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("POST /cupons")
    class CreateCoupon {

        @Test
        @DisplayName("deve criar cupom e retornar 201")
        void shouldCreateCouponAndReturn201() throws Exception {
            Coupon coupon = createSampleCoupon();
            when(createCouponUseCase.execute(any(CreateCouponCommand.class))).thenReturn(coupon);

            String requestBody = """
                    {
                        "code": "ABC123",
                        "description": "Cupom de desconto",
                        "discountValue": 10.00,
                        "expirationDate": "%s",
                        "published": false
                    }
                    """.formatted(LocalDate.now().plusDays(30));

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value("ABC123"))
                    .andExpect(jsonPath("$.description").value("Cupom de desconto"))
                    .andExpect(jsonPath("$.discountValue").value(10.00))
                    .andExpect(jsonPath("$.published").value(false))
                    .andExpect(jsonPath("$.deleted").value(false))
                    .andExpect(jsonPath("$.id").exists());

            verify(createCouponUseCase).execute(any(CreateCouponCommand.class));
        }

        @Test
        @DisplayName("deve retornar 400 quando código está em branco")
        void shouldReturn400WhenCodeIsBlank() throws Exception {
            String requestBody = """
                    {
                        "code": "",
                        "description": "Descrição",
                        "discountValue": 10.00,
                        "expirationDate": "%s",
                        "published": false
                    }
                    """.formatted(LocalDate.now().plusDays(30));

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.erros").isArray());

            verify(createCouponUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("deve retornar 400 quando descrição está em branco")
        void shouldReturn400WhenDescriptionIsBlank() throws Exception {
            String requestBody = """
                    {
                        "code": "ABC123",
                        "description": "",
                        "discountValue": 10.00,
                        "expirationDate": "%s",
                        "published": false
                    }
                    """.formatted(LocalDate.now().plusDays(30));

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());

            verify(createCouponUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("deve retornar 400 quando valor de desconto é nulo")
        void shouldReturn400WhenDiscountValueIsNull() throws Exception {
            String requestBody = """
                    {
                        "code": "ABC123",
                        "description": "Descrição",
                        "expirationDate": "%s",
                        "published": false
                    }
                    """.formatted(LocalDate.now().plusDays(30));

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());

            verify(createCouponUseCase, never()).execute(any());
        }

        @Test
        @DisplayName("deve retornar 409 quando código já existe")
        void shouldReturn409WhenCodeAlreadyExists() throws Exception {
            when(createCouponUseCase.execute(any(CreateCouponCommand.class)))
                    .thenThrow(new CouponCodeAlreadyExistsException("Já existe um cupom com o código 'ABC123'"));

            String requestBody = """
                    {
                        "code": "ABC123",
                        "description": "Descrição",
                        "discountValue": 10.00,
                        "expirationDate": "%s",
                        "published": false
                    }
                    """.formatted(LocalDate.now().plusDays(30));

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.mensagem").value("Já existe um cupom com o código 'ABC123'"));
        }

        @Test
        @DisplayName("deve retornar 400 quando código de cupom é inválido")
        void shouldReturn400WhenCouponCodeIsInvalid() throws Exception {
            when(createCouponUseCase.execute(any(CreateCouponCommand.class)))
                    .thenThrow(new InvalidCouponCodeException("O código do cupom deve conter exatamente 6 caracteres alfanuméricos"));

            String requestBody = """
                    {
                        "code": "AB",
                        "description": "Descrição",
                        "discountValue": 10.00,
                        "expirationDate": "%s",
                        "published": false
                    }
                    """.formatted(LocalDate.now().plusDays(30));

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("deve retornar 400 quando valor de desconto é inválido")
        void shouldReturn400WhenDiscountValueIsInvalid() throws Exception {
            when(createCouponUseCase.execute(any(CreateCouponCommand.class)))
                    .thenThrow(new InvalidDiscountValueException("O valor de desconto deve ser no mínimo R$ 0,50"));

            String requestBody = """
                    {
                        "code": "ABC123",
                        "description": "Descrição",
                        "discountValue": 0.10,
                        "expirationDate": "%s",
                        "published": false
                    }
                    """.formatted(LocalDate.now().plusDays(30));

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 400 quando data de expiração é inválida")
        void shouldReturn400WhenExpirationDateIsInvalid() throws Exception {
            when(createCouponUseCase.execute(any(CreateCouponCommand.class)))
                    .thenThrow(new InvalidExpirationDateException("A data de expiração não pode estar no passado"));

            String requestBody = """
                    {
                        "code": "ABC123",
                        "description": "Descrição",
                        "discountValue": 10.00,
                        "expirationDate": "%s",
                        "published": false
                    }
                    """.formatted(LocalDate.now().plusDays(30));

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /cupons/{codigo}")
    class DeleteCoupon {

        @Test
        @DisplayName("deve excluir cupom e retornar 204")
        void shouldDeleteCouponAndReturn204() throws Exception {
            doNothing().when(deleteCouponUseCase).execute("ABC123");

            mockMvc.perform(delete("/cupons/ABC123"))
                    .andExpect(status().isNoContent());

            verify(deleteCouponUseCase).execute("ABC123");
        }

        @Test
        @DisplayName("deve retornar 404 quando cupom não é encontrado")
        void shouldReturn404WhenCouponNotFound() throws Exception {
            doThrow(new CouponNotFoundException("Cupom não encontrado com o código 'XYZ999'"))
                    .when(deleteCouponUseCase).execute("XYZ999");

            mockMvc.perform(delete("/cupons/XYZ999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.mensagem").value("Cupom não encontrado com o código 'XYZ999'"));
        }

        @Test
        @DisplayName("deve retornar 422 quando cupom já foi excluído")
        void shouldReturn422WhenCouponAlreadyDeleted() throws Exception {
            doThrow(new CouponAlreadyDeletedException("O cupom 'DEL001' já foi excluído"))
                    .when(deleteCouponUseCase).execute("DEL001");

            mockMvc.perform(delete("/cupons/DEL001"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.status").value(422))
                    .andExpect(jsonPath("$.mensagem").value("O cupom 'DEL001' já foi excluído"));
        }
    }
}
