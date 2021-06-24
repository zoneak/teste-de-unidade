package com.ak.leilao.infra.email;

import com.ak.leilao.dominio.Leilao;

public interface Carteiro {

	void envia(Leilao leilao);
}
