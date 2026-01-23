package br.com.softdesign.votacao.unit;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.dto.CriarSessaoVotacaoRequest;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoVotacaoServiceUnitTest {

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

        CriarSessaoVotacaoRequest request = new CriarSessaoVotacaoRequest();
        request.setPautaId(1L);
        request.setDuracao(30);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaIdAndDataFimAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(false);
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SessaoVotacao sessaoSalva = sessaoVotacaoService.criar(request);

        verify(sessaoVotacaoRepository).save(any(SessaoVotacao.class));
        assertThat(sessaoSalva).isNotNull();
        assertThat(sessaoSalva.getPauta().getTitulo()).isEqualTo("Pauta 1");
        assertThat(sessaoSalva.isSessaoAberta()).isTrue();
        assertThat(sessaoSalva.getDuracao()).isEqualTo(30);
    }

    @Test
    void abrirSessao_semDuracaoInformada_ePautaValida_deveCriarSessaoAbertaComDuracaoPadraoDeUmMinuto() {
        Pauta pauta = new Pauta("Pauta 1", "Descricao 1");
        pauta.setId(1L);

        CriarSessaoVotacaoRequest request = new CriarSessaoVotacaoRequest();
        request.setPautaId(1L);
        request.setDuracao(0);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaIdAndDataFimAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(false);
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SessaoVotacao sessaoSalva = sessaoVotacaoService.criar(request);

        verify(sessaoVotacaoRepository).save(any(SessaoVotacao.class));
        assertThat(sessaoSalva.getDuracao()).isEqualTo(1); // padrão
        assertThat(sessaoSalva.isSessaoAberta()).isTrue();
    }

    @Test
    void abrirSessao_comPautaInexistente_deveLancarExcecao() {
        CriarSessaoVotacaoRequest request = new CriarSessaoVotacaoRequest();
        request.setPautaId(1L);
        request.setDuracao(10);

        when(pautaRepository.findById(anyLong())).thenReturn(Optional.empty());

        SessaoVotacaoInvaalidaException exception =
                assertThrows(SessaoVotacaoInvaalidaException.class,
                        () -> sessaoVotacaoService.criar(request));

        assertEquals("A sessão de votação não pode ser criada para uma pauta inexistente",
                exception.getMessage());
    }

    @Test
    void abrirSessao_emPautaComSessaoAtiva_deveLancarExcecao() {
        Pauta pauta = new Pauta("Pauta 1", "Descricao 1");
        pauta.setId(1L);

        CriarSessaoVotacaoRequest request = new CriarSessaoVotacaoRequest();
        request.setPautaId(1L);
        request.setDuracao(10);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaIdAndDataFimAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(true);

        SessaoVotacaoInvaalidaException exception =
                assertThrows(SessaoVotacaoInvaalidaException.class,
                        () -> sessaoVotacaoService.criar(request));

        assertEquals("Não é possível criar uma sessão de votação em pauta com sessão já ativa",
                exception.getMessage());
    }

    @Test
    void abrirSessao_emPautaComSessaoAnteriorEncerrada_deveCriarNovaSessaoVotacao() {
        Pauta pauta = new Pauta("Pauta 1", "Descricao 1");
        pauta.setId(1L);

        CriarSessaoVotacaoRequest request = new CriarSessaoVotacaoRequest();
        request.setPautaId(1L);
        request.setDuracao(3);

        when(pautaRepository.findById(1L)).thenReturn(Optional.of(pauta));
        when(sessaoVotacaoRepository.existsByPautaIdAndDataFimAfter(eq(1L), any(LocalDateTime.class)))
                .thenReturn(false);
        when(sessaoVotacaoRepository.save(any(SessaoVotacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SessaoVotacao sessaoSalva = sessaoVotacaoService.criar(request);

        verify(sessaoVotacaoRepository).save(any(SessaoVotacao.class));
        assertThat(sessaoSalva).isNotNull();
        assertThat(sessaoSalva.getPauta().getTitulo()).isEqualTo("Pauta 1");
        assertThat(sessaoSalva.isSessaoAberta()).isTrue();
        assertThat(sessaoSalva.getDuracao()).isEqualTo(3);
    }
}
