package br.com.softdesign.votacao.controller;


import br.com.softdesign.votacao.dto.ResultadoVotacaoResponse;
import br.com.softdesign.votacao.service.ResultadoVotacaoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resultados")
@RequiredArgsConstructor
public class ResultadoVotacaoController
{
    private final ResultadoVotacaoService resultadoVotacaoService;
    private static final Logger log = LoggerFactory.getLogger(ResultadoVotacaoController.class);

    @GetMapping("/{pautaId}")
    public ResponseEntity<ResultadoVotacaoResponse> obterResultado(@PathVariable Long pautaId) {

        log.info("GET /resultados/{} | Solicitação de apuração", pautaId);

        ResultadoVotacaoResponse resultado = resultadoVotacaoService.contarVotos(pautaId);

        log.info("Resultado retornado | pautaId={} | resultado={}",
                pautaId,
                resultado.getResultado());

        return ResponseEntity.ok(resultado);
    }

}
