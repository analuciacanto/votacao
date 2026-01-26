package br.com.softdesign.votacao.integration;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.dto.CriarSessaoVotacaoRequest;
import br.com.softdesign.votacao.repository.PautaRepository;
import br.com.softdesign.votacao.repository.SessaoVotacaoRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class SessaoVotacaoIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private SessaoVotacaoRepository sessaoVotacaoRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private Pauta pauta;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        sessaoVotacaoRepository.deleteAll();
        pautaRepository.deleteAll();

        pauta = new Pauta("Pauta Teste", "Descrição Teste");
        pautaRepository.save(pauta);
    }

    @Test
    void criarSessao_comDuracaoInformada_deveRetornar201() throws Exception {
        CriarSessaoVotacaoRequest request = new CriarSessaoVotacaoRequest();
        request.setPautaId(pauta.getId());
        request.setDuracao(15);

        mockMvc.perform(post("/sessao-votacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pautaId").value(pauta.getId()))
                .andExpect(jsonPath("$.duracao").value(15));
    }

    @Test
    void criarSessao_semDuracaoInformada_deveUsarDuracaoPadrao() throws Exception {
        CriarSessaoVotacaoRequest request = new CriarSessaoVotacaoRequest();
        request.setPautaId(pauta.getId());
        request.setDuracao(0);

        mockMvc.perform(post("/sessao-votacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.duracao").value(1));
    }

    @Test
    void criarSessao_comPautaInexistente_deveRetornar400_eMensagemCorreta() throws Exception {
        CriarSessaoVotacaoRequest request = new CriarSessaoVotacaoRequest();
        request.setPautaId(999L);
        request.setDuracao(10);

        mockMvc.perform(post("/sessao-votacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem")
                        .value("A sessão de votação não pode ser criada para uma pauta inexistente"));
    }

    @Test
    void criarSessao_emPautaComSessaoAtiva_deveRetornar400_eMensagemCorreta() throws Exception {
        // Cria uma sessão ativa
        SessaoVotacao sessaoAtiva = new SessaoVotacao();
        sessaoAtiva.setPauta(pauta);
        sessaoAtiva.setDuracao(10);
        sessaoAtiva.setDataInicio(LocalDateTime.now());
        sessaoAtiva.setDataFim(LocalDateTime.now().plusMinutes(10));
        sessaoVotacaoRepository.save(sessaoAtiva);

        CriarSessaoVotacaoRequest request = new CriarSessaoVotacaoRequest();
        request.setPautaId(pauta.getId());
        request.setDuracao(5);

        mockMvc.perform(post("/sessao-votacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem")
                        .value("Não é possível criar uma sessão de votação em pauta com sessão já ativa"));
    }
}
