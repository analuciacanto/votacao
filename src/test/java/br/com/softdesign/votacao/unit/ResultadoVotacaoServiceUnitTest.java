package br.com.softdesign.votacao.unit;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.domain.Voto;
import br.com.softdesign.votacao.domain.VotoOpcao;
import br.com.softdesign.votacao.dto.ResultadoVotacaoResponse;
import br.com.softdesign.votacao.exception.PautaInvalidaException;
import br.com.softdesign.votacao.repository.PautaRepository;
import br.com.softdesign.votacao.service.ResultadoVotacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResultadoVotacaoServiceUnitTest {

    @InjectMocks
    private ResultadoVotacaoService resultadoVotacaoService;

    @Mock
    private PautaRepository pautaRepository;

    private Pauta pauta;
    private SessaoVotacao sessaoVotacao;
    private Voto voto1;
    private Voto voto2;
    private Voto voto3;

    @BeforeEach
    void setup() {
        pauta = new Pauta("Pauta 1", "Descrição 1");
        pauta.setId(1L);

        sessaoVotacao = new SessaoVotacao(pauta, 3);
        sessaoVotacao.setId(1L);

        voto1 = new Voto(sessaoVotacao, "11111111111", VotoOpcao.SIM);
        voto2 = new Voto(sessaoVotacao, "22222222222", VotoOpcao.NAO);
    }

    @Test
    void calcularResultado_quandoMaioriaSim_deveRetornarAprovada(){
        voto3 = new Voto(sessaoVotacao, "33333333333", VotoOpcao.SIM);
        sessaoVotacao.setVotos(List.of(voto1, voto2, voto3));
        pauta.setSessoes(Set.of(sessaoVotacao));

        when(pautaRepository.findByIdWithSessoesAndVotos(1L)).thenReturn(Optional.of(pauta));

        ResultadoVotacaoResponse resultado = resultadoVotacaoService.contarVotos(1L);

        assertThat(resultado.getPautaId()).isEqualTo(1L);
        assertThat(resultado.getTituloPauta()).isEqualTo("Pauta 1");
        assertThat(resultado.getTotalSim()).isEqualTo(2);
        assertThat(resultado.getTotalNao()).isEqualTo(1);
        assertThat(resultado.getResultado()).isEqualTo("APROVADA");
    }

    @Test
    void calcularResultado_quandoMaioriaNao_deveRetornarReprovada(){
        voto3 = new Voto(sessaoVotacao, "33333333333", VotoOpcao.NAO);
        sessaoVotacao.setVotos(List.of(voto1, voto2, voto3));
        pauta.setSessoes(Set.of(sessaoVotacao));

        when(pautaRepository.findByIdWithSessoesAndVotos(1L)).thenReturn(Optional.of(pauta));

        ResultadoVotacaoResponse resultado = resultadoVotacaoService.contarVotos(1L);

        assertThat(resultado.getTotalSim()).isEqualTo(1);
        assertThat(resultado.getTotalNao()).isEqualTo(2);
        assertThat(resultado.getResultado()).isEqualTo("REPROVADA");
    }

    @Test
    void calcularResultado_quandoEmpate_deveRetornarEmpate(){
        sessaoVotacao.setVotos(List.of(voto1, voto2));
        pauta.setSessoes(Set.of(sessaoVotacao));

        when(pautaRepository.findByIdWithSessoesAndVotos(1L)).thenReturn(Optional.of(pauta));

        ResultadoVotacaoResponse resultado = resultadoVotacaoService.contarVotos(1L);

        assertThat(resultado.getTotalSim()).isEqualTo(1);
        assertThat(resultado.getTotalNao()).isEqualTo(1);
        assertThat(resultado.getResultado()).isEqualTo("EMPATE");
    }

    @Test
    void calcularResultado_quandoNaoExistiremVotos_deveRetornarSemVotos(){
        sessaoVotacao.setVotos(List.of());
        pauta.setSessoes(Set.of(sessaoVotacao));

        when(pautaRepository.findByIdWithSessoesAndVotos(1L)).thenReturn(Optional.of(pauta));

        ResultadoVotacaoResponse resultado = resultadoVotacaoService.contarVotos(1L);

        assertThat(resultado.getTotalSim()).isEqualTo(0);
        assertThat(resultado.getTotalNao()).isEqualTo(0);
        assertThat(resultado.getResultado()).isEqualTo("SEM VOTOS");
    }

    @Test
    void calcularResultado_quandoPautaNaoExistir_deveLancarExcecao(){
        when(pautaRepository.findByIdWithSessoesAndVotos(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultadoVotacaoService.contarVotos(99L))
                .isInstanceOf(PautaInvalidaException.class)
                .hasMessage("Pauta não encontrada");
    }

    @Test
    void calcularResultado_quandoMultiplasSessoes_deveSomarTodosVotosCorretamente(){
        SessaoVotacao sessao2 = new SessaoVotacao(pauta, 3);
        sessao2.setId(2L);

        Voto voto3 = new Voto(sessao2, "33333333333", VotoOpcao.SIM);
        Voto voto4 = new Voto(sessao2, "44444444444", VotoOpcao.SIM);
        sessao2.setVotos(List.of(voto3, voto4));

        sessaoVotacao.setVotos(List.of(voto1, voto2));

        pauta.setSessoes(Set.of(sessaoVotacao, sessao2));

        when(pautaRepository.findByIdWithSessoesAndVotos(1L)).thenReturn(Optional.of(pauta));

        ResultadoVotacaoResponse resultado = resultadoVotacaoService.contarVotos(1L);

        assertThat(resultado.getTotalSim()).isEqualTo(3);
        assertThat(resultado.getTotalNao()).isEqualTo(1);
        assertThat(resultado.getResultado()).isEqualTo("APROVADA");
    }

    @Test
    void calcularResultado_quandoNaoExistiremSessoes_deveRetornarSemVotos(){
        Pauta pautaSemSessoes = new Pauta("Pauta 2", "Descrição sem sessões");
        pautaSemSessoes.setId(2L);
        pauta.setSessoes(Set.of(sessaoVotacao));

        when(pautaRepository.findByIdWithSessoesAndVotos(2L)).thenReturn(Optional.of(pautaSemSessoes));

        ResultadoVotacaoResponse resultado = resultadoVotacaoService.contarVotos(2L);

        assertThat(resultado.getTotalSim()).isEqualTo(0);
        assertThat(resultado.getTotalNao()).isEqualTo(0);
        assertThat(resultado.getResultado()).isEqualTo("SEM VOTOS");
    }
}
