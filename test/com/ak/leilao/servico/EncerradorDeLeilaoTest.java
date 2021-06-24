package com.ak.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.ak.leilao.builder.CriadorDeLeilao;
import com.ak.leilao.dominio.Leilao;
import com.ak.leilao.infra.dao.LeilaoDao;
import com.ak.leilao.infra.dao.RepositorioDeLeiloes;
import com.ak.leilao.infra.email.Carteiro;

public class EncerradorDeLeilaoTest {
	
	@Test
	public void deveEncerrarLeiloesQueComecaramUmaSemanaAntes() {
		// criamos lista ficticia
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);
		
		// criamos dao ficticio com o mockito
		LeilaoDao daoFalso = mock(LeilaoDao.class);
		
		// setamos um retorno quando chamar um metodo do dao falso (como se tivesse simulando uma consulta no banco de dados)
		when(daoFalso.correntes()).thenReturn(leiloesAntigos);
		
		Carteiro carteiroFalso = mock(Carteiro.class);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		
		assertEquals(2, encerrador.getTotalEncerrados());
		assertTrue(leilao1.isEncerrado());
		assertTrue(leilao2.isEncerrado());
	}
	
	@Test
	public void deveAtualizarLeiloesEnccerrados() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
		
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1));
		
		Carteiro carteiroFalso = mock(Carteiro.class);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		
		// garantir que o método .atualiza foi invocado apenas 1x de fato. se nao for invocado vai falhar o teste
		verify(daoFalso, times(1)).atualiza(leilao1);
	}
	
	@Test
	public void deveContinuarExecucaoMesmoQuandoDaoFalha() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(antiga).constroi();
		
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		Carteiro carteiroFalso = mock(Carteiro.class);
		
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
		
		// simular um throw de exception. precisa ser chamado antes do método em si
		doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		
		// não é pra enviar email quando não atualizar o leilao 1
		verify(carteiroFalso, times(0)).envia(leilao1);
		
		// é para continuar mesmo o leilao 1 dando pau
		verify(daoFalso).atualiza(leilao2);
		verify(carteiroFalso).envia(leilao2);
		
	}

}
