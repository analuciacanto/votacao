package br.com.softdesign.votacao.controller;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.dto.CriarPautaRequest;
import br.com.softdesign.votacao.dto.PautaResponse;
import br.com.softdesign.votacao.service.PautaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pautas")
@RequiredArgsConstructor
public class PautaController {

    private static final Logger log = LoggerFactory.getLogger(PautaController.class);
    private final PautaService pautaService;

    @PostMapping
    public ResponseEntity<PautaResponse> create(@RequestBody @Valid CriarPautaRequest pautaRequest){

        log.info("POST /pautas | Criar pauta | titulo={}",
                pautaRequest.titulo());

        Pauta pautaCriada = pautaService.criar(pautaRequest);

        log.info("Pauta criada com sucesso | pautaId={} | titulo={}",
                pautaCriada.getId(),
                pautaCriada.getTitulo());

        PautaResponse pautaResponse = new PautaResponse(pautaCriada.getId(), pautaCriada.getTitulo(), pautaCriada.getDescricao(),
                pautaCriada.getDataCriacao());

        return ResponseEntity.status(HttpStatus.CREATED).body(pautaResponse);    }

    @GetMapping
    public ResponseEntity<List<PautaResponse>> listar() {

        log.info("GET /pautas | Listar pautas");

        List<PautaResponse> pautas = pautaService.getAllPautas();

        log.info("Lista de pautas retornada | total={}", pautas.size());

        return ResponseEntity.ok(pautas);
    }
}
