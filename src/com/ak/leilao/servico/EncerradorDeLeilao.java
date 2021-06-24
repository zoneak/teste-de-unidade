package com.ak.leilao.servico;

import java.util.Calendar;
import java.util.List;

import com.ak.leilao.dominio.Leilao;
import com.ak.leilao.infra.dao.RepositorioDeLeiloes;
import com.ak.leilao.infra.email.Carteiro;

public class EncerradorDeLeilao {

	private int total = 0;
	private final RepositorioDeLeiloes dao;
	private final Carteiro carteiro;

	public EncerradorDeLeilao(RepositorioDeLeiloes dao, Carteiro carteiro) {
		this.dao = dao;
		this.carteiro = carteiro;
	}
	
	public void encerra() {
		List<Leilao> todosLeiloesCorrentes = dao.correntes();

		for (Leilao leilao : todosLeiloesCorrentes) {
			try {
				if (comecouSemanaPassada(leilao)) {
					leilao.encerra();
					total++;
					dao.atualiza(leilao);
					// envia email
					carteiro.envia(leilao);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
		}
	}

	private boolean comecouSemanaPassada(Leilao leilao) {
		return diasEntre(leilao.getData(), Calendar.getInstance()) >= 7;
	}

	private int diasEntre(Calendar inicio, Calendar fim) {
		Calendar data = (Calendar) inicio.clone();
		int diasNoIntervalo = 0;
		while (data.before(fim)) {
			data.add(Calendar.DAY_OF_MONTH, 1);
			diasNoIntervalo++;
		}

		return diasNoIntervalo;
	}

	public int getTotalEncerrados() {
		return total;
	}
}
