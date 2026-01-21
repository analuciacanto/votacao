package br.com.softdesign.votacao.unit;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.exception.PautaInvalidaException;
import br.com.softdesign.votacao.repository.PautaRepository;
import br.com.softdesign.votacao.service.PautaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PautaServiceUnitTest {

    @InjectMocks
    private PautaService pautaService;

    @Mock
    private PautaRepository pautaRepository;

    @Test
    void criarPautaComDadosCorretos_deveCriarPautaComSucesso(){

        Pauta pauta = new Pauta("Pauta teste", "");

        when(pautaRepository.save(pauta)).thenReturn(pauta);

        Pauta pautaSalva = pautaService.criar(pauta);

        verify(pautaRepository).save(pauta);
        assertThat(pautaSalva).isNotNull();
        assertThat(pautaSalva.getTitulo()).isEqualTo("Pauta teste");
    }

    @Test
    void criarPautaNula_deveLancarExcecao(){
        Pauta pauta = null;

        PautaInvalidaException pautaInvalidaException =
                assertThrows(PautaInvalidaException.class,
                        () -> pautaService.criar(pauta));
        assertEquals("Pauta não pode ser nula", pautaInvalidaException.getMessage());
    }

    @Test
    void criarPautaComTituloVazio_deveLancarExcecao() {
        Pauta pauta = new Pauta("", "");
        PautaInvalidaException pautaInvalidaException =
                assertThrows(PautaInvalidaException.class,
                        () -> pautaService.criar(pauta));
        assertEquals("O título da pauta é obrigatório", pautaInvalidaException.getMessage());
    }

    @Test
    void criarPautaComTituloEmBranco_deveLancarExcecao(){
        Pauta pauta = new Pauta(" ", "");
        PautaInvalidaException pautaInvalidaException =
                assertThrows(PautaInvalidaException.class,
                        () -> pautaService.criar(pauta));
        assertEquals("O título da pauta é obrigatório", pautaInvalidaException.getMessage());
    }

    @Test
    void retornarPautasCadastradasSemParametros_deveRetornarTodasPautasCadastradas(){
        Pauta pauta1 = new Pauta("Pauta 1", "Descrição 1");
        Pauta pauta2 = new Pauta("Pauta 2", "Descrição 2");

        List<Pauta> pautas = List.of(pauta1,pauta2);
        when(pautaRepository.findAll()).thenReturn(pautas);

        List<Pauta> resultado = pautaService.getAllPautas();

        assertEquals(2, resultado.size());
        assertEquals("Pauta 1", resultado.get(0).getTitulo());
    }
}
