package br.com.stoom.coupon_domain.adapter.in.web.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ErrorResponse")
class ErrorResponseTest {

    @Test
    @DisplayName("deve criar resposta de erro sem lista de erros")
    void shouldCreateErrorResponseWithoutErrorList() {
        ErrorResponse response = ErrorResponse.of(404, "Cupom não encontrado");

        assertEquals(404, response.status());
        assertEquals("Cupom não encontrado", response.mensagem());
        assertNotNull(response.timestamp());
        assertNull(response.erros());
    }

    @Test
    @DisplayName("deve criar resposta de erro com lista de erros")
    void shouldCreateErrorResponseWithErrorList() {
        List<String> erros = List.of("code: O código é obrigatório", "description: A descrição é obrigatória");

        ErrorResponse response = ErrorResponse.of(400, "Erro de validação", erros);

        assertEquals(400, response.status());
        assertEquals("Erro de validação", response.mensagem());
        assertNotNull(response.timestamp());
        assertEquals(2, response.erros().size());
        assertTrue(response.erros().contains("code: O código é obrigatório"));
    }
}
