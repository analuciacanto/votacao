package br.com.softdesign.votacao.unit;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.dto.CriarPautaRequest;
import br.com.softdesign.votacao.dto.PautaResponse;
import br.com.softdesign.votacao.exception.PautaInvalidaException;
import br.com.softdesign.votacao.repository.PautaRepository;
import br.com.softdesign.votacao.service.PautaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PautaServiceUnitTest {

    @InjectMocks
    private PautaService pautaService;

    @Mock
    private PautaRepository pautaRepository;

    @Test
    void criarPautaComDadosCorretos_deveCriarPautaComSucesso() {
        CriarPautaRequest request =
                new CriarPautaRequest("Pauta teste", "descricao");

        when(pautaRepository.save(any(Pauta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Pauta> captor =
                ArgumentCaptor.forClass(Pauta.class);

        Pauta resultado = pautaService.criar(request);

        verify(pautaRepository).save(captor.capture());

        Pauta pautaSalva = captor.getValue();

        assertThat(pautaSalva.getTitulo()).isEqualTo("Pauta teste");
        assertThat(pautaSalva.getDescricao()).isEqualTo("descricao");
        assertThat(resultado).isNotNull();
    }

    @Test
    void criarPauta_requestNulo_deveLancarExcecao() {

        PautaInvalidaException exception =
                assertThrows(PautaInvalidaException.class,
                        () -> pautaService.criar(null));

        assertThat(exception.getMessage())
                .isEqualTo("Os dados não podem ser nulos");

        verifyNoInteractions(pautaRepository);
    }

    @Test
    void getAllPautas_deveRetornarLista() {

        Pauta pauta = new Pauta("Pauta", "Desc");
        pauta.setId(1L);
        when(pautaRepository.findAll()).thenReturn(List.of(pauta));

        List<PautaResponse> pautasResponse = pautaService.getAllPautas();

        assertThat(pautasResponse).hasSize(1);

        PautaResponse primeira = pautasResponse.get(0);
        assertThat(primeira.getId()).isEqualTo(1L);
        assertThat(primeira.getTitulo()).isEqualTo("Pauta");
        assertThat(primeira.getDescricao()).isEqualTo("Desc");

        verify(pautaRepository).findAll();
    }
}