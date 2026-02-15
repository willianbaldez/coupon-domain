package br.com.stoom.coupon_domain.adapter.in.web;

import br.com.stoom.coupon_domain.adapter.in.web.dto.CouponResponse;
import br.com.stoom.coupon_domain.adapter.in.web.dto.CreateCouponRequest;
import br.com.stoom.coupon_domain.adapter.in.web.dto.ErrorResponse;
import br.com.stoom.coupon_domain.application.port.in.CreateCouponUseCase;
import br.com.stoom.coupon_domain.application.port.in.CreateCouponUseCase.CreateCouponCommand;
import br.com.stoom.coupon_domain.application.port.in.DeleteCouponUseCase;
import br.com.stoom.coupon_domain.domain.model.Coupon;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cupons")
@Tag(name = "Cupons", description = "Operações de gerenciamento de cupons")
public class CouponController {

    private final CreateCouponUseCase createCouponUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;

    public CouponController(CreateCouponUseCase createCouponUseCase,
                            DeleteCouponUseCase deleteCouponUseCase) {
        this.createCouponUseCase = createCouponUseCase;
        this.deleteCouponUseCase = deleteCouponUseCase;
    }

    @PostMapping
    @Operation(
            summary = "Criar cupom",
            description = "Cria um novo cupom de desconto"
    )
    @ApiResponse(responseCode = "201", description = "Cupom criado com sucesso",
            content = @Content(schema = @Schema(implementation = CouponResponse.class)))
    @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Código de cupom já existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<CouponResponse> create(@Valid @RequestBody CreateCouponRequest request) {
        CreateCouponCommand command = new CreateCouponCommand(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate(),
                request.published()
        );

        Coupon coupon = createCouponUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(CouponResponse.from(coupon));
    }

    @DeleteMapping("/{codigo}")
    @Operation(
            summary = "Excluir cupom",
            description = "Realiza soft delete de um cupom pelo código"
    )
    @ApiResponse(responseCode = "204", description = "Cupom excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Cupom não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "422", description = "Cupom já foi excluído",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<Void> delete(@PathVariable String codigo) {
        deleteCouponUseCase.execute(codigo);
        return ResponseEntity.noContent().build();
    }
}
