package com.ak.leilao.dominio;

import java.util.Calendar;

public class Pagamento {

	private final double valor;
	private final Calendar data;

	public Pagamento(double valor, Calendar data) {
		this.valor = valor;
		this.data = data;
	}

	public double getValor() {
		return valor;
	}

	public Calendar getData() {
		return data;
	}
}
