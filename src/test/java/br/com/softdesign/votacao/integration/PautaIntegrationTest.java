package br.com.softdesign.votacao.integration;

import br.com.softdesign.votacao.dto.CriarPautaRequest;
import br.com.softdesign.votacao.repository.PautaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PautaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PautaRepository pautaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setup() {
        pautaRepository.deleteAll();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private String buildJson(CriarPautaRequest request) throws Exception {
        return objectMapper.writeValueAsString(request);
    }

    @Test
    void criarPauta_comDadosValidos_deveRetornar201() throws Exception {
        CriarPautaRequest request = new CriarPautaRequest("Pauta de Teste", "Descrição da pauta");

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Pauta de Teste"))
                .andExpect(jsonPath("$.descricao").value("Descrição da pauta"));
    }

    @Test
    void criarPauta_semTitulo_deveRetornar400() throws Exception {
        CriarPautaRequest request = new CriarPautaRequest();
        request.setDescricao("Descrição da pauta");

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagens[0]").value("O título da pauta é obrigatório"));
    }

    @Test
    void criarPauta_comTituloVazio_deveRetornar400() throws Exception {
        CriarPautaRequest request = new CriarPautaRequest("", "Descrição da pauta");

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagens[0]").value("O título da pauta é obrigatório"));
    }

    @Test
    void criarPauta_comTituloApenasEspacos_deveRetornar400() throws Exception {
        CriarPautaRequest request = new CriarPautaRequest("   ", "Descrição da pauta");

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagens[0]").value("O título da pauta é obrigatório"));
    }

    @Test
    void criarPauta_comContentTypeInvalido_deveRetornar415() throws Exception {
        CriarPautaRequest request = new CriarPautaRequest("Pauta Teste", null);

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(buildJson(request)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void criarPauta_comDescricaoNula_deveRetornar201() throws Exception {
        CriarPautaRequest request = new CriarPautaRequest("Pauta sem descrição", null);

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.descricao").doesNotExist());
    }

    @Test
    void listarPautas_quandoExistirPauta_deveRetornar200() throws Exception {
        CriarPautaRequest request = new CriarPautaRequest("Pauta de Teste", "Descrição");

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildJson(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/pautas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].titulo").value("Pauta de Teste"))
                .andExpect(jsonPath("$[0].descricao").value("Descrição"));
    }

    @Test
    void listarPautas_quandoNaoExistirPauta_deveRetornarListaVazia() throws Exception {
        mockMvc.perform(get("/pautas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
