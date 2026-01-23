package br.com.softdesign.votacao.integration;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.domain.VotoOpcao;
import br.com.softdesign.votacao.dto.CriarVotoRequest;
import br.com.softdesign.votacao.repository.PautaRepository;
import br.com.softdesign.votacao.repository.SessaoVotacaoRepository;
import br.com.softdesign.votacao.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VotoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Pauta pauta;
    private SessaoVotacao sessaoVotacao;

    @BeforeEach
    void setup() {
        votoRepository.deleteAll();
        sessaoVotacaoRepository.deleteAll();
        pautaRepository.deleteAll();

        pauta = new Pauta("Pauta Teste", "Descrição Teste");
        pautaRepository.save(pauta);

        sessaoVotacao = new SessaoVotacao(pauta, 5);
        sessaoVotacaoRepository.save(sessaoVotacao);
    }

    @Test
    void registrarVoto_quandoDadosValidos_deveRetornar201() throws Exception {
        CriarVotoRequest request = new CriarVotoRequest(sessaoVotacao.getId(), "12345678901", VotoOpcao.SIM);

        mockMvc.perform(post("/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.voto").value("SIM"));
    }

    @Test
    void registrarVoto_quandoMesmoAssociadoVotaNaMesmaPauta_deveRetornar400() throws Exception {
        CriarVotoRequest request = new CriarVotoRequest(
                sessaoVotacao.getId(),
                "12345678901",
                VotoOpcao.SIM
        );

        // Primeiro voto válido
        mockMvc.perform(post("/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Segundo voto duplicado — espera 400
        mockMvc.perform(post("/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("O associado já votou nesta sessão."));
    }

    @Test
    void registrarVoto_quandoSessaoNaoExistir_deveRetornar400() throws Exception {
        CriarVotoRequest request = new CriarVotoRequest(999L, "12345678901", br.com.softdesign.votacao.domain.VotoOpcao.SIM);

        mockMvc.perform(post("/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Sessão de votação com id 999 não encontrada"));
    }

    @Test
    void registrarVoto_quandoSessaoFechada_deveRetornar400() throws Exception {
        // fecha a sessão
        sessaoVotacao.setDataFim(LocalDateTime.now().minusMinutes(1));
        sessaoVotacaoRepository.save(sessaoVotacao);

        CriarVotoRequest request = new CriarVotoRequest(sessaoVotacao.getId(), "12345678901", br.com.softdesign.votacao.domain.VotoOpcao.SIM);

        mockMvc.perform(post("/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("A sessão de votação está encerrada."));
    }
}
