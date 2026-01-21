package br.com.softdesign.votacao.controller;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.service.PautaService;
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
    public ResponseEntity<Pauta> create(@RequestBody Pauta pauta){

        Pauta pautaCriada = pautaService.criar(pauta);
        return ResponseEntity.status(HttpStatus.CREATED).body(pautaCriada);
    }

    @GetMapping
    public ResponseEntity<List<Pauta>> listar() {
        return ResponseEntity.ok(pautaService.getAllPautas());
    }
}
