package br.com.softdesign.votacao.controller;


import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.dto.CriarSessaoVotacaoRequest;
import br.com.softdesign.votacao.dto.SessaoVotacaoResponse;
import br.com.softdesign.votacao.service.SessaoVotacaoService;
import jakarta.validation.Valid;
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
    public ResponseEntity<SessaoVotacaoResponse> create(@RequestBody @Valid CriarSessaoVotacaoRequest sessaoVotacao){
        SessaoVotacao sessaoVotacaoCriada = sessaoVotacaoService.criar(sessaoVotacao);

        SessaoVotacaoResponse sessaoVotacaoResponse = new SessaoVotacaoResponse(sessaoVotacaoCriada.getId(), sessaoVotacao.getPautaId(),
              sessaoVotacaoCriada.getDataInicio(), sessaoVotacaoCriada.getDataFim(), sessaoVotacaoCriada.isSessaoAberta(), sessaoVotacaoCriada.getDuracao());

        return ResponseEntity.status(HttpStatus.CREATED).body(sessaoVotacaoResponse);
    }
}
