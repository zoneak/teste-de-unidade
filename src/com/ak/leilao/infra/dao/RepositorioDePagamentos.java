package com.ak.leilao.infra.dao;

import com.ak.leilao.dominio.Pagamento;

public interface RepositorioDePagamentos {

	void salva(Pagamento pagamento);
}
