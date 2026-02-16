package br.com.stoom.coupon_domain.application.port.in;

import br.com.stoom.coupon_domain.domain.model.Coupon;

public interface BuscarCupomPorCodigoUseCase {

    Coupon execute(String codigo);
}
