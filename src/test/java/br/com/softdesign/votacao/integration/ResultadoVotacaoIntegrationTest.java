package br.com.softdesign.votacao.integration;

import br.com.softdesign.votacao.domain.*;
import br.com.softdesign.votacao.dto.ResultadoVotacaoResponse;
import br.com.softdesign.votacao.repository.PautaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class ResultadoVotacaoIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PautaRepository pautaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private Pauta pauta;
    private SessaoVotacao sessao;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        pautaRepository.deleteAll();

        pauta = new Pauta("Pauta 1", "Descrição");
        sessao = new SessaoVotacao(pauta, 3);
        pauta.adicionarSessao(sessao);
        pautaRepository.save(pauta);

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void calcularResultado_quandoMaioriaSim_deveRetornarAprovada() throws Exception {
        adicionarVotos(sessao,
                voto(sessao, "111", VotoOpcao.SIM),
                voto(sessao, "222", VotoOpcao.NAO),
                voto(sessao, "333", VotoOpcao.SIM)
        );

        pautaRepository.save(pauta);

        ResultadoVotacaoResponse r = chamarEndpoint();

        assertThat(r.getResultado()).isEqualTo("APROVADA");
        assertThat(r.getTotalSim()).isEqualTo(2);
        assertThat(r.getTotalNao()).isEqualTo(1);
    }

    @Test
    void calcularResultado_quandoMaioriaNao_deveRetornarReprovada() throws Exception {
        adicionarVotos(sessao,
                voto(sessao, "111", VotoOpcao.NAO),
                voto(sessao, "222", VotoOpcao.NAO),
                voto(sessao, "333", VotoOpcao.SIM)
        );

        pautaRepository.save(pauta);

        ResultadoVotacaoResponse r = chamarEndpoint();

        assertThat(r.getResultado()).isEqualTo("REPROVADA");
        assertThat(r.getTotalSim()).isEqualTo(1);
        assertThat(r.getTotalNao()).isEqualTo(2);
    }

    @Test
    void calcularResultado_quandoEmpate_deveRetornarEmpate() throws Exception {
        adicionarVotos(sessao,
                voto(sessao, "111", VotoOpcao.SIM),
                voto(sessao, "222", VotoOpcao.NAO)
        );

        pautaRepository.save(pauta);

        ResultadoVotacaoResponse r = chamarEndpoint();

        assertThat(r.getResultado()).isEqualTo("EMPATE");
        assertThat(r.getTotalSim()).isEqualTo(1);
        assertThat(r.getTotalNao()).isEqualTo(1);
    }

    @Test
    void calcularResultado_quandoNaoExistiremVotos_deveRetornarSemVotos() throws Exception {
        pautaRepository.save(pauta);

        ResultadoVotacaoResponse r = chamarEndpoint();

        assertThat(r.getResultado()).isEqualTo("SEM VOTOS");
        assertThat(r.getTotalSim()).isZero();
        assertThat(r.getTotalNao()).isZero();
    }

    @Test
    void calcularResultado_quandoNaoExistiremSessoes_deveRetornarSemVotos() throws Exception {
        Pauta p = new Pauta("Pauta sem sessão", "desc");
        pautaRepository.save(p);

        String json = mockMvc.perform(get("/resultados/{id}", p.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ResultadoVotacaoResponse r = objectMapper.readValue(json, ResultadoVotacaoResponse.class);

        assertThat(r.getResultado()).isEqualTo("SEM VOTOS");
        assertThat(r.getTotalSim()).isZero();
        assertThat(r.getTotalNao()).isZero();
    }

    // ================= helpers =================

    private Voto voto(SessaoVotacao sessao, String cpf, VotoOpcao opcao) {
        return new Voto(sessao, cpf, opcao);
    }

    private void adicionarVotos(SessaoVotacao sessao, Voto... votos) {
        for (Voto v : votos) {
            sessao.adicionarVoto(v);
        }
    }

    private ResultadoVotacaoResponse chamarEndpoint() throws Exception {
        String json = mockMvc.perform(get("/resultados/{id}", pauta.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(json, ResultadoVotacaoResponse.class);
    }
}
