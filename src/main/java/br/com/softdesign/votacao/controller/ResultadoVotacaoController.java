package br.com.softdesign.votacao.controller;


import br.com.softdesign.votacao.dto.ResultadoVotacaoResponse;
import br.com.softdesign.votacao.service.ResultadoVotacaoService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/{pautaId}")
    public ResponseEntity<ResultadoVotacaoResponse> obterResultado(@PathVariable Long pautaId) {
        ResultadoVotacaoResponse resultado = resultadoVotacaoService.contarVotos(pautaId);
        return ResponseEntity.ok(resultado);
    }

}
