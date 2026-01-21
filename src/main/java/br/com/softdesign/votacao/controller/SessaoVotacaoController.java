package br.com.softdesign.votacao.controller;


import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.service.SessaoVotacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessaoVotacao")
@RequiredArgsConstructor
public class SessaoVotacaoController {

    private final SessaoVotacaoService sessaoVotacaoService;

    @PostMapping
    public ResponseEntity<SessaoVotacao> create(@RequestBody SessaoVotacao sessaoVotacao){
        SessaoVotacao sessaoVotacaoCriada = sessaoVotacaoService.criar(sessaoVotacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoVotacaoCriada);
    }
}
