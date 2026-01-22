package br.com.softdesign.votacao.unit;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.exception.SessaoVotacaoInvaalidaException;
import br.com.softdesign.votacao.repository.PautaRepository;
import br.com.softdesign.votacao.repository.SessaoVotacaoRepository;
import br.com.softdesign.votacao.service.SessaoVotacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoUnitTest {

    @InjectMocks
    private SessaoVotacaoService sessaoVotacaoService;

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @Test
    void abrirSessao_comDuracaoInformada_ePautaValida_deveCriarSessaoAbertaComDuracaoInformada() {
        Pauta pauta = new Pauta("Pauta 1", "Descricao 1");
        pauta.setId(1L);
        SessaoVotacao sessaoVotacao = new SessaoVotacao(pauta, 30);

        when(pautaRepository.findById(pauta.getId())).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.save(sessaoVotacao)).thenReturn(sessaoVotacao);

        SessaoVotacao sessaoVotacaoSalva = sessaoVotacaoService.criar(sessaoVotacao);

        verify(sessaoVotacaoRepository).save(sessaoVotacao);
        assertThat(sessaoVotacaoSalva).isNotNull();
        assertThat(sessaoVotacaoSalva.getPauta().getTitulo()).isEqualTo("Pauta 1");
        assertThat(sessaoVotacaoSalva.isSessaoAberta()).isTrue();
        assertThat(sessaoVotacaoSalva.getDuracao()).isEqualTo(30);
    }

    @Test
    void abrirSessao_semDuracaoInformada_ePautaValida_deveCriarSessaoAbertaComDuracaoPadraoDeUmMinuto() {
        Pauta pauta = new Pauta("Pauta 1", "Descricao 1");
        pauta.setId(1L);
        SessaoVotacao sessaoVotacao = new SessaoVotacao(pauta, 0);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.save(sessaoVotacao)).thenReturn(sessaoVotacao);

        SessaoVotacao sessaoVotacaoSalva = sessaoVotacaoService.criar(sessaoVotacao);

        verify(sessaoVotacaoRepository).save(sessaoVotacao);
        assertThat(sessaoVotacaoSalva).isNotNull();
        assertThat(sessaoVotacaoSalva.getPauta().getTitulo()).isEqualTo("Pauta 1");
        assertThat(sessaoVotacaoSalva.isSessaoAberta()).isTrue();
        assertThat(sessaoVotacaoSalva.getDuracao()).isEqualTo(1);
    }

    @Test
    void abrirSessao_comPautaNula_deveLancarExcecao() {
        Pauta pauta = null;
        SessaoVotacao sessaoVotacao = new SessaoVotacao(pauta, 0);

        SessaoVotacaoInvaalidaException sessaoVotacaoInvaalidaException =
                assertThrows(SessaoVotacaoInvaalidaException.class,
                        () -> sessaoVotacaoService.criar(sessaoVotacao));
        assertEquals("A Pauta é obrigatória para abrir uma sessão", sessaoVotacaoInvaalidaException.getMessage());
    }

    @Test
    void abrirSessao_comPautaInexistente_deveLancarExcecao() {
        Pauta pauta = new Pauta("Pauta 1", "Descricao 1");
        pauta.setId(1L);
        SessaoVotacao sessaoVotacao = new SessaoVotacao(pauta, 0);

        when(pautaRepository.findById(anyLong())).thenReturn(Optional.empty());

        SessaoVotacaoInvaalidaException sessaoVotacaoInvaalidaException =
                assertThrows(SessaoVotacaoInvaalidaException.class,
                        () -> sessaoVotacaoService.criar(sessaoVotacao));
        assertEquals("A sessão de votação não pode ser criada para uma pauta inexistente", sessaoVotacaoInvaalidaException.getMessage());
    }

    @Test
    void abrirSessao_emPautaComSessaoAtiva_deveLancarExcecao() {
        Pauta pauta = new Pauta("Pauta 1", "Descricao 1");
        pauta.setId(1L);

        SessaoVotacao sessaoVotacao = new SessaoVotacao(pauta, 1);
        SessaoVotacao sessaoVotacaoEmPautaComSessaoAtiva = new SessaoVotacao(pauta, 3);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.findByPautaIdAndDataFimAfter(
                eq(pauta.getId()), any(LocalDateTime.class)))
                .thenReturn(List.of(sessaoVotacao));

        SessaoVotacaoInvaalidaException sessaoVotacaoInvaalidaException =
                assertThrows(SessaoVotacaoInvaalidaException.class,
                        () -> sessaoVotacaoService.criar(sessaoVotacaoEmPautaComSessaoAtiva));
        assertEquals("Não é possível criar uma sessão de votação em pauta com sessão já ativa", sessaoVotacaoInvaalidaException.getMessage());
    }

    @Test
    void abrirSessao_emPautaComSessaoAnteriorEncerrada_deveCriarNovaSessaoVotacao() {
        Pauta pauta = new Pauta("Pauta 1", "Descricao 1");
        pauta.setId(1L);

        SessaoVotacao sessaoVotacaoEncerrada = new SessaoVotacao(pauta, 5);
        sessaoVotacaoEncerrada.setDataInicio(LocalDateTime.now().minusMinutes(10));
        sessaoVotacaoEncerrada.setDataFim(LocalDateTime.now().minusMinutes(5));

        SessaoVotacao novaSessaoVotacao = new SessaoVotacao(pauta, 3);

        when(pautaRepository.findById(pauta.getId())).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.save(novaSessaoVotacao)).thenReturn(novaSessaoVotacao);

        SessaoVotacao sessaoVotacaoSalva = sessaoVotacaoService.criar(novaSessaoVotacao);

        verify(sessaoVotacaoRepository).save(sessaoVotacaoSalva);
        assertThat(sessaoVotacaoSalva).isNotNull();
        assertThat(sessaoVotacaoSalva.getPauta().getTitulo()).isEqualTo("Pauta 1");
        assertThat(sessaoVotacaoSalva.isSessaoAberta()).isTrue();
        assertThat(sessaoVotacaoSalva.getDuracao()).isEqualTo(3);
    }
}
