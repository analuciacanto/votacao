package br.com.softdesign.votacao.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PautaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void criarPauta_comDadosValidos_deveRetornar201() throws Exception{
        String pauta = """
        {
          "titulo": "Pauta de Teste",
          "descricao": "Descrição da pauta"
        }
        """;

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pauta))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Pauta de Teste"))
                .andExpect(jsonPath("$.descricao").value("Descrição da pauta"));
    }

    @Test
    void criarPauta_semTitulo_deveRetornar400() throws Exception{

        String json = """
        {
          "descricao": "Descrição da pauta"
        }
        """;

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagens[0]")
                        .value("O título da pauta é obrigatório"));
    }

    @Test
    void criarPauta_comTituloVazio_deveRetornar400() throws Exception {

        String json = """
        {
          "titulo": "",
          "descricao": "Descrição da pauta"
        }
        """;

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagens[0]")
                .value("O título da pauta é obrigatório"));;
    }

    @Test
    void criarPauta_comTituloApenasEspacos_deveRetornar400() throws Exception {
        String json = """
        {
          "titulo": "   ",
          "descricao": "Descrição da pauta"
        }
        """;

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagens[0]")
                        .value("O título da pauta é obrigatório"));;
    }

    @Test
    void criarPauta_comContentTypeInvalido_deveRetornar415() throws Exception {

        String json = """
        {
          "titulo": "Pauta Teste"
        }
        """;

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(json))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void criarPauta_comDescricaoNula_deveRetornar201() throws Exception {
        String json = """
        {
          "titulo": "Pauta sem descrição"
        }
        """;

        mockMvc.perform(post("/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.descricao").doesNotExist());
    }
}
