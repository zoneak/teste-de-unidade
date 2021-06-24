package com.ak.leilao.infra.dao;

import java.util.List;

import com.ak.leilao.dominio.Leilao;

public interface RepositorioDeLeiloes {
	void salva(Leilao leilao);
    List<Leilao> encerrados();
    List<Leilao> correntes();
    void atualiza(Leilao leilao);
}
