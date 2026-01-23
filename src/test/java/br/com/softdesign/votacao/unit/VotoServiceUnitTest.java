package br.com.softdesign.votacao.unit;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.domain.Voto;
import br.com.softdesign.votacao.domain.VotoOpcao;
import br.com.softdesign.votacao.dto.CriarVotoRequest;
import br.com.softdesign.votacao.dto.VotoResponse;
import br.com.softdesign.votacao.exception.SessaoVotacaoInvaalidaException;
import br.com.softdesign.votacao.exception.VotoInvalidoException;
import br.com.softdesign.votacao.repository.SessaoVotacaoRepository;
import br.com.softdesign.votacao.repository.VotoRepository;
import br.com.softdesign.votacao.service.VotoService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class VotoServiceUnitTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @InjectMocks
    private VotoService votoService;

    private Pauta pauta;

    private SessaoVotacao sessaoVotacao;

    @BeforeEach
    void setup() {
        pauta = new Pauta("Pauta 1", "Descrição 1");
        sessaoVotacao = new SessaoVotacao(pauta, 3);
        sessaoVotacao.setId(1L);
        votoRepository.deleteAll();
    }

    @Test
    void salvarVoto_quandoDadosValidos_deveSalvarComSucesso() {

        CriarVotoRequest criarVotoRequest = new CriarVotoRequest(1L,"12345678901", VotoOpcao.SIM);
        Voto votoParaSalvar = new Voto(sessaoVotacao, "12345678901", VotoOpcao.SIM);

        when(sessaoVotacaoRepository.findById(1L))
                .thenReturn(Optional.of(sessaoVotacao));

        when(votoRepository.save(any(Voto.class))).thenAnswer(invocation -> {
            Voto voto = invocation.getArgument(0);
            voto.setId(1L);
            return voto;
        });

        Voto votoSalvo = votoService.registrarVoto(criarVotoRequest);

        assertNotNull(votoSalvo.getId());
        assertEquals("12345678901", votoSalvo.getCpf());
        assertEquals(1L, votoSalvo.getSessaoVotacao().getId());
        assertEquals(VotoOpcao.SIM, votoSalvo.getVoto());
    }

    @Test
    void registrarVoto_quandoMesmoAssociadoVotaNaMesmaPauta_deveLancarExcecao(){
        CriarVotoRequest criarVotoRequest = new CriarVotoRequest(1L, "12345678901", VotoOpcao.SIM);

        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessaoVotacao));
        when(votoRepository.existsBySessaoVotacaoAndCpf(sessaoVotacao, "12345678901")).thenReturn(true);

        VotoInvalidoException votoInvalidoException = assertThrows(VotoInvalidoException.class, () -> {
            votoService.registrarVoto(criarVotoRequest);
        });

        assertEquals("O associado já votou nesta sessão.", votoInvalidoException.getMessage());
   }

    @Test
    void registrarVoto_quandoSessaoNaoExistir_deveLancarExcecao(){
        CriarVotoRequest criarVotoRequest = new CriarVotoRequest(999L, "12345678901", VotoOpcao.SIM);

        when(sessaoVotacaoRepository.findById(999L)).thenReturn(Optional.empty());

        SessaoVotacaoInvaalidaException sessaoVotacaoException = assertThrows(
                SessaoVotacaoInvaalidaException.class,
                () -> votoService.registrarVoto(criarVotoRequest)
        );

        assertEquals("Sessão de votação com id 999 não encontrada", sessaoVotacaoException.getMessage());
    }

    @Test
    void registrarVoto_quandoSessaoFechada_deveLancarExcecao(){
        CriarVotoRequest criarVotoRequest = new CriarVotoRequest(1L, "12345678901", VotoOpcao.SIM);

        sessaoVotacao.setDataFim(LocalDateTime.now().minusMinutes(1));
        when(sessaoVotacaoRepository.findById(1L)).thenReturn(Optional.of(sessaoVotacao));

        SessaoVotacaoInvaalidaException sessaoVotacaoInvaalidaException = assertThrows(
                SessaoVotacaoInvaalidaException.class,
                () -> votoService.registrarVoto(criarVotoRequest)
        );

        assertEquals("A sessão de votação está encerrada.", sessaoVotacaoInvaalidaException.getMessage());
    }
}
