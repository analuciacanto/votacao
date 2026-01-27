package br.com.softdesign.votacao.controller;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.domain.Voto;
import br.com.softdesign.votacao.dto.CriarPautaRequest;
import br.com.softdesign.votacao.dto.CriarVotoRequest;
import br.com.softdesign.votacao.dto.PautaResponse;
import br.com.softdesign.votacao.dto.VotoResponse;
import br.com.softdesign.votacao.service.VotoService;
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
@RequestMapping("/votos")
@RequiredArgsConstructor

public class VotoController {

    private final VotoService votoService;
    private static final Logger log = LoggerFactory.getLogger(VotoController.class);

    @PostMapping
    public ResponseEntity<VotoResponse> create(@RequestBody @Valid CriarVotoRequest votoRequest){

        log.info("POST /votos | Registrar voto | sessaoId={}",
                votoRequest.sessaoVotacaoId());

        Voto voto = votoService.registrarVoto(votoRequest);

        log.info("Voto registrado com sucesso | sessaoId={} | voto={}",
                voto.getSessaoVotacao().getId(),
                voto.getVoto());

        VotoResponse votoResponse = new VotoResponse(voto.getCpf(), voto.getVoto());
        return ResponseEntity.status(HttpStatus.CREATED).body(votoResponse);
    }

}
