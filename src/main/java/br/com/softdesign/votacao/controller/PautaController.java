package br.com.softdesign.votacao.controller;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.dto.CriarPautaRequest;
import br.com.softdesign.votacao.dto.PautaResponse;
import br.com.softdesign.votacao.service.PautaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pautas")
@RequiredArgsConstructor
public class PautaController {

    private final PautaService pautaService;

    @PostMapping
    public ResponseEntity<PautaResponse> create(@RequestBody @Valid CriarPautaRequest pautaRequest){

        Pauta pautaCriada = pautaService.criar(pautaRequest);

        PautaResponse pautaResponse = new PautaResponse(pautaCriada.getId(), pautaCriada.getTitulo(), pautaCriada.getDescricao());
        return ResponseEntity.status(HttpStatus.CREATED).body(pautaResponse);    }

    @GetMapping
    public ResponseEntity<List<Pauta>> listar() {
        return ResponseEntity.ok(pautaService.getAllPautas());
    }
}
