package com.ak.leilao.servico;

import java.util.Calendar;
import java.util.List;

import com.ak.leilao.dominio.Leilao;
import com.ak.leilao.dominio.Pagamento;
import com.ak.leilao.infra.dao.RepositorioDeLeiloes;
import com.ak.leilao.infra.dao.RepositorioDePagamentos;
import com.ak.leilao.infra.relogio.Relogio;
import com.ak.leilao.infra.relogio.RelogioDoSistema;

public class GeradorDePagamento {
	
	private final RepositorioDeLeiloes leiloes;
	private final Avaliador avaliador;
	private final RepositorioDePagamentos pagamentos;
	private final Relogio relogio;

	// Construtor para testes
	public GeradorDePagamento(RepositorioDeLeiloes leiloes, RepositorioDePagamentos pagamentos, Avaliador avaliador, Relogio relogio) {
		this.leiloes = leiloes;
		this.pagamentos = pagamentos;
		this.avaliador = avaliador;
		this.relogio = relogio;
	}
	
	// Construtor principal
	public GeradorDePagamento(RepositorioDeLeiloes leiloes, RepositorioDePagamentos pagamentos, Avaliador avaliador) {
		this(leiloes, pagamentos, avaliador, new RelogioDoSistema());
	}

	public void gera() {
		List<Leilao> leiloesEncerrados = this.leiloes.encerrados();
		
		for (Leilao leilao : leiloesEncerrados) {
			this.avaliador.avalia(leilao);
			
			Pagamento novoPagamento = new Pagamento(avaliador.getMaiorLance(), primeiroDiaUtil());
			this.pagamentos.salva(novoPagamento);
		}
	}

	private Calendar primeiroDiaUtil() {
		Calendar data = relogio.hoje();
		int diaDaSemana = data.get(Calendar.DAY_OF_WEEK);
		
		if (diaDaSemana == Calendar.SATURDAY) {
			data.add(Calendar.DAY_OF_MONTH, 2);
		} else if (diaDaSemana == Calendar.SUNDAY) {
			data.add(Calendar.DAY_OF_MONTH, 1);			
		}
		return data;
	}
}
