package br.com.stoom.coupon_domain.adapter.in.web;

import br.com.stoom.coupon_domain.adapter.in.web.dto.CouponQueryResponse;
import br.com.stoom.coupon_domain.adapter.in.web.dto.CouponResponse;
import br.com.stoom.coupon_domain.adapter.in.web.dto.CreateCouponRequest;
import br.com.stoom.coupon_domain.adapter.in.web.dto.ErrorResponse;
import br.com.stoom.coupon_domain.application.port.in.BuscarCupomPorCodigoUseCase;
import br.com.stoom.coupon_domain.application.port.in.BuscarTodosCuponsUseCase;
import br.com.stoom.coupon_domain.application.port.in.CreateCouponUseCase;
import br.com.stoom.coupon_domain.application.port.in.CreateCouponUseCase.CreateCouponCommand;
import br.com.stoom.coupon_domain.application.port.in.DeleteCouponUseCase;
import br.com.stoom.coupon_domain.domain.model.Coupon;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cupons")
@Tag(name = "Cupons", description = "Operações de gerenciamento de cupons")
public class CouponController {

    private final CreateCouponUseCase createCouponUseCase;
    private final DeleteCouponUseCase deleteCouponUseCase;
    private final BuscarCupomPorCodigoUseCase buscarCupomPorCodigoUseCase;
    private final BuscarTodosCuponsUseCase buscarTodosCuponsUseCase;

    public CouponController(CreateCouponUseCase createCouponUseCase,
                            DeleteCouponUseCase deleteCouponUseCase,
                            BuscarCupomPorCodigoUseCase buscarCupomPorCodigoUseCase,
                            BuscarTodosCuponsUseCase buscarTodosCuponsUseCase) {
        this.createCouponUseCase = createCouponUseCase;
        this.deleteCouponUseCase = deleteCouponUseCase;
        this.buscarCupomPorCodigoUseCase = buscarCupomPorCodigoUseCase;
        this.buscarTodosCuponsUseCase = buscarTodosCuponsUseCase;
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

    @GetMapping("/{codigo}")
    @Operation(
            summary = "Buscar cupom por código",
            description = "Retorna os dados de um cupom pelo código"
    )
    @ApiResponse(responseCode = "200", description = "Cupom encontrado",
            content = @Content(schema = @Schema(implementation = CouponQueryResponse.class)))
    @ApiResponse(responseCode = "404", description = "Cupom não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<CouponQueryResponse> findByCode(@PathVariable String codigo) {
        Coupon coupon = buscarCupomPorCodigoUseCase.execute(codigo);
        return ResponseEntity.ok(CouponQueryResponse.from(coupon));
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os cupons",
            description = "Retorna a lista de todos os cupons cadastrados"
    )
    @ApiResponse(responseCode = "200", description = "Lista de cupons",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CouponQueryResponse.class))))
    public ResponseEntity<List<CouponQueryResponse>> findAll() {
        List<CouponQueryResponse> response = buscarTodosCuponsUseCase.execute().stream()
                .map(CouponQueryResponse::from)
                .toList();

        return ResponseEntity.ok(response);
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
