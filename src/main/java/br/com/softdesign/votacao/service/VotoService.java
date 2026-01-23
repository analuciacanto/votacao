package br.com.softdesign.votacao.service;

import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.domain.Voto;
import br.com.softdesign.votacao.dto.CriarVotoRequest;
import br.com.softdesign.votacao.exception.SessaoVotacaoInvaalidaException;
import br.com.softdesign.votacao.exception.VotoInvalidoException;
import br.com.softdesign.votacao.repository.SessaoVotacaoRepository;
import br.com.softdesign.votacao.repository.VotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VotoService {

    private final VotoRepository votoRepository;

    private final SessaoVotacaoRepository sessaoVotacaoRepository;

    public Voto registrarVoto(CriarVotoRequest votoRequest) {
        SessaoVotacao sessaoVotacao = buscarSessaoOuLancar(votoRequest.getSessaoVotacaoId());
        validarSessaoAberta(sessaoVotacao);
        validarVotoDuplicado(sessaoVotacao, votoRequest.getCpf());

        Voto voto = new Voto(sessaoVotacao, votoRequest.getCpf(), votoRequest.getVoto());
        return votoRepository.save(voto);
    }

    private SessaoVotacao buscarSessaoOuLancar(Long sessaoId) {
        return sessaoVotacaoRepository.findById(sessaoId)
                .orElseThrow(() -> new SessaoVotacaoInvaalidaException(
                        "Sessão de votação com id " + sessaoId + " não encontrada"));
    }

    private void validarSessaoAberta(SessaoVotacao sessaoVotacao) {
        if (sessaoVotacao.getDataFim() != null && sessaoVotacao.getDataFim().isBefore(LocalDateTime.now())) {
            throw new SessaoVotacaoInvaalidaException("A sessão de votação está encerrada.");
        }
    }

    private void validarVotoDuplicado(SessaoVotacao sessaoVotacao, String cpf) {
        if (votoRepository.existsBySessaoVotacaoAndCpf(sessaoVotacao, cpf)) {
            throw new VotoInvalidoException("O associado já votou nesta sessão.");
        }
    }
}
