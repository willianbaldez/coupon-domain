package br.com.stoom.coupon_domain.application.port.in;

import br.com.stoom.coupon_domain.domain.model.Coupon;

import java.util.List;

public interface BuscarTodosCuponsUseCase {

    List<Coupon> execute();
}
