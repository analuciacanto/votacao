package br.com.softdesign.votacao.service;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.dto.CriarPautaRequest;
import br.com.softdesign.votacao.dto.PautaResponse;
import br.com.softdesign.votacao.exception.PautaInvalidaException;
import br.com.softdesign.votacao.repository.PautaRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PautaService {

   private final PautaRepository pautaRepository;
   private static final Logger log = LoggerFactory.getLogger(PautaService.class);

    @Timed(value = "pautas.criar.tempo", description = "Tempo gasto para criar uma pauta")
    public Pauta criar(CriarPautaRequest pautaRequest) {

        log.info("Iniciando criação de pauta");

        if (pautaRequest == null) {
            log.warn("Falha ao criar pauta: request nulo");
            throw new PautaInvalidaException("Os dados não podem ser nulos");
        }

        log.debug("Dados recebidos para criação da pauta | titulo={} | descricao={}",
                pautaRequest.titulo(),
                pautaRequest.descricao());

        Pauta pauta = new Pauta(pautaRequest.titulo(), pautaRequest.descricao());
        Pauta pautaSalva = pautaRepository.save(pauta);

        log.info("Pauta criada com sucesso | pautaId={} | titulo={}",
                pautaSalva.getId(),
                pautaSalva.getTitulo());

        return pautaSalva;
    }

    @Timed(value = "pautas.listar.tempo", description = "Tempo gasto para listar todas as pautas")
    public List<PautaResponse> getAllPautas() {
        log.info("Buscando todas as pautas cadastradas");

        List<PautaResponse> pautas = pautaRepository.findAll()
                .stream()
                .map(pauta -> new PautaResponse(
                        pauta.getId(),
                        pauta.getTitulo(),
                        pauta.getDescricao(),
                        pauta.getDataCriacao()
                ))
                .toList();

        log.info("Consulta de pautas finalizada | totalEncontrado={}", pautas.size());

        return pautas;
    }
}
