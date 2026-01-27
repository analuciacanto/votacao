package br.com.softdesign.votacao.controller;


import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.dto.CriarSessaoVotacaoRequest;
import br.com.softdesign.votacao.dto.SessaoVotacaoResponse;
import br.com.softdesign.votacao.service.SessaoVotacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessao-votacao")
@RequiredArgsConstructor
public class SessaoVotacaoController {

    private final SessaoVotacaoService sessaoVotacaoService;
    private static final Logger log = LoggerFactory.getLogger(SessaoVotacaoController.class);

    @PostMapping
    public ResponseEntity<SessaoVotacaoResponse> create(@RequestBody @Valid CriarSessaoVotacaoRequest sessaoVotacao){

        log.info("POST /sessao-votacao | Criar sessão | pautaId={} | duracao={}",
                sessaoVotacao.pautaId(),
                sessaoVotacao.duracao());

        SessaoVotacao sessaoVotacaoCriada = sessaoVotacaoService.criar(sessaoVotacao);

        log.info("Sessão criada com sucesso | sessaoId={} | pautaId={} | aberta={}",
                sessaoVotacaoCriada.getId(),
                sessaoVotacaoCriada.getPauta().getId(),
                sessaoVotacaoCriada.isSessaoAberta());

        SessaoVotacaoResponse sessaoVotacaoResponse = new SessaoVotacaoResponse(sessaoVotacaoCriada.getId(), sessaoVotacao.pautaId(),
              sessaoVotacaoCriada.getDataInicio(), sessaoVotacaoCriada.getDataFim(), sessaoVotacaoCriada.isSessaoAberta(), sessaoVotacaoCriada.getDuracao());

        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoVotacaoResponse);
    }
}
