package br.com.softdesign.votacao.service;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.exception.PautaInvalidaException;
import br.com.softdesign.votacao.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PautaService {

   private final PautaRepository pautaRepository;

    public Pauta criar(Pauta pauta) {
       if (pauta == null){
           throw new PautaInvalidaException("Pauta não pode ser nula");
       }

       if (pauta.getTitulo() == null || pauta.getTitulo().isBlank()){
           throw new PautaInvalidaException("O título da pauta é obrigatório");
       }

       return pautaRepository.save(pauta);
    }

    public List<Pauta> getAllPautas() {
        return pautaRepository.findAll();
    }
}
