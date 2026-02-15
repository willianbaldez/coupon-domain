package br.com.stoom.coupon_domain.adapter.in.web.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        int status,
        String mensagem,
        LocalDateTime timestamp,
        List<String> erros
) {

    public static ErrorResponse of(int status, String mensagem) {
        return new ErrorResponse(status, mensagem, LocalDateTime.now(), null);
    }

    public static ErrorResponse of(int status, String mensagem, List<String> erros) {
        return new ErrorResponse(status, mensagem, LocalDateTime.now(), erros);
    }
}
