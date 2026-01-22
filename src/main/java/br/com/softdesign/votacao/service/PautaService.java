package br.com.softdesign.votacao.service;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.dto.CriarPautaRequest;
import br.com.softdesign.votacao.dto.PautaResponse;
import br.com.softdesign.votacao.exception.PautaInvalidaException;
import br.com.softdesign.votacao.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PautaService {

   private final PautaRepository pautaRepository;

    public Pauta criar(CriarPautaRequest pautaRequest) {

        if (pautaRequest == null) {
            throw new PautaInvalidaException("Os dados não podem ser nulos");
        }

        Pauta pauta = new Pauta(pautaRequest.getTitulo(), pautaRequest.getDescricao());
        return pautaRepository.save(pauta);
    }

    public List<PautaResponse> getAllPautas() {
        return pautaRepository.findAll()
                .stream()
                .map(pauta -> new PautaResponse(
                        pauta.getId(),
                        pauta.getTitulo(),
                        pauta.getDescricao(),
                        pauta.getDataCriacao()
                ))
                .toList();
    }
}
