package br.com.stoom.coupon_domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Testes de integração - Fluxo completo")
class CouponIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String createCouponRequestBody(String code, String description, double discount,
                                            LocalDate expirationDate, boolean published) {
        return String.format(Locale.US, """
                {
                    "code": "%s",
                    "description": "%s",
                    "discountValue": %.2f,
                    "expirationDate": "%s",
                    "published": %s
                }
                """, code, description, discount, expirationDate, published);
    }

    @Nested
    @DisplayName("Fluxo de criação de cupom")
    class CreateFlow {

        @Test
        @DisplayName("deve criar cupom e persistir no banco")
        void shouldCreateCouponAndPersist() throws Exception {
            String body = createCouponRequestBody(
                    "INT001", "Cupom integração", 10.00,
                    LocalDate.now().plusDays(30), true
            );

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.code").value("INT001"))
                    .andExpect(jsonPath("$.description").value("Cupom integração"))
                    .andExpect(jsonPath("$.discountValue").value(10.00))
                    .andExpect(jsonPath("$.published").value(true))
                    .andExpect(jsonPath("$.deleted").value(false))
                    .andExpect(jsonPath("$.active").value(true));
        }

        @Test
        @DisplayName("deve rejeitar cupom com código duplicado")
        void shouldRejectDuplicateCode() throws Exception {
            String body = createCouponRequestBody(
                    "DUP001", "Primeiro", 5.00,
                    LocalDate.now().plusDays(30), false
            );

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.mensagem").value("Já existe um cupom com o código 'DUP001'"));
        }

        @Test
        @DisplayName("deve sanitizar código removendo caracteres especiais")
        void shouldSanitizeCodeAndPersist() throws Exception {
            String body = createCouponRequestBody(
                    "AB-C1.2!3", "Com caracteres especiais", 10.00,
                    LocalDate.now().plusDays(30), false
            );

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value("ABC123"));
        }

        @Test
        @DisplayName("deve rejeitar dados inválidos com erros de validação")
        void shouldRejectInvalidDataWithValidationErrors() throws Exception {
            String body = """
                    {
                        "code": "",
                        "description": "",
                        "discountValue": 0.1,
                        "expirationDate": "2020-01-01",
                        "published": false
                    }
                    """;

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.erros").isArray());
        }
    }

    @Nested
    @DisplayName("Fluxo de exclusão de cupom")
    class DeleteFlow {

        @Test
        @DisplayName("deve criar e excluir cupom com sucesso")
        void shouldCreateAndDeleteCoupon() throws Exception {
            String body = createCouponRequestBody(
                    "DEL001", "Para excluir", 10.00,
                    LocalDate.now().plusDays(30), false
            );

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());

            mockMvc.perform(delete("/cupons/DEL001"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("deve encontrar cupom para exclusão mesmo com código em lowercase")
        void shouldFindCouponForDeletionWithLowercaseCode() throws Exception {
            String body = createCouponRequestBody(
                    "LOW001", "Teste lowercase", 10.00,
                    LocalDate.now().plusDays(30), false
            );

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());

            mockMvc.perform(delete("/cupons/low001"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("deve retornar 404 ao excluir cupom inexistente")
        void shouldReturn404WhenDeletingNonExistentCoupon() throws Exception {
            mockMvc.perform(delete("/cupons/XXX999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("deve retornar 422 ao tentar excluir cupom já excluído")
        void shouldReturn422WhenDeletingAlreadyDeletedCoupon() throws Exception {
            String body = createCouponRequestBody(
                    "DBL001", "Double delete", 10.00,
                    LocalDate.now().plusDays(30), false
            );

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());

            mockMvc.perform(delete("/cupons/DBL001"))
                    .andExpect(status().isNoContent());

            mockMvc.perform(delete("/cupons/DBL001"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.status").value(422));
        }
    }

    @Nested
    @DisplayName("Fluxo de consulta de cupons")
    class QueryFlow {

        @Test
        @DisplayName("deve criar e consultar cupom por código")
        void shouldCreateAndFindCouponByCode() throws Exception {
            String body = createCouponRequestBody(
                    "QRY001", "Cupom consulta", 15.00,
                    LocalDate.now().plusDays(30), true
            );

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());

            mockMvc.perform(get("/cupons/QRY001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.codigo").value("QRY001"))
                    .andExpect(jsonPath("$.valorDesconto").value(15.00))
                    .andExpect(jsonPath("$.dataExpiracao").exists())
                    .andExpect(jsonPath("$.removido").value(false));
        }

        @Test
        @DisplayName("deve consultar cupom por código em lowercase")
        void shouldFindCouponByLowercaseCode() throws Exception {
            String body = createCouponRequestBody(
                    "QRY002", "Cupom lowercase", 10.00,
                    LocalDate.now().plusDays(30), false
            );

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());

            mockMvc.perform(get("/cupons/qry002"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.codigo").value("QRY002"));
        }

        @Test
        @DisplayName("deve retornar 404 ao consultar cupom inexistente")
        void shouldReturn404WhenCouponNotFound() throws Exception {
            mockMvc.perform(get("/cupons/XXX000"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("deve listar todos os cupons cadastrados")
        void shouldListAllCoupons() throws Exception {
            String body1 = createCouponRequestBody(
                    "LST001", "Primeiro", 10.00,
                    LocalDate.now().plusDays(30), true
            );
            String body2 = createCouponRequestBody(
                    "LST002", "Segundo", 20.00,
                    LocalDate.now().plusDays(60), false
            );

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body1))
                    .andExpect(status().isCreated());

            mockMvc.perform(post("/cupons")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body2))
                    .andExpect(status().isCreated());

            mockMvc.perform(get("/cupons"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].codigo").exists())
                    .andExpect(jsonPath("$[1].codigo").exists());
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há cupons")
        void shouldReturnEmptyListWhenNoCoupons() throws Exception {
            mockMvc.perform(get("/cupons"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }
}
