package br.com.softdesign.votacao.service;

import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.domain.Voto;
import br.com.softdesign.votacao.dto.CriarVotoRequest;
import br.com.softdesign.votacao.exception.SessaoVotacaoInvaalidaException;
import br.com.softdesign.votacao.exception.VotoInvalidoException;
import br.com.softdesign.votacao.repository.SessaoVotacaoRepository;
import br.com.softdesign.votacao.repository.VotoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VotoService {

    private final VotoRepository votoRepository;
    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private static final Logger log = LoggerFactory.getLogger(VotoService.class);

    @Transactional
    public Voto registrarVoto(CriarVotoRequest votoRequest) {

        log.info("Recebida solicitação de voto | sessaoId={} | cpf={}",
                votoRequest.getSessaoVotacaoId(),
                votoRequest.getCpf());

        SessaoVotacao sessaoVotacao = buscarSessaoOuLancar(votoRequest.getSessaoVotacaoId());
        validarSessaoAberta(sessaoVotacao);
        validarVotoDuplicado(sessaoVotacao, votoRequest.getCpf());

        Voto voto = new Voto(sessaoVotacao, votoRequest.getCpf(), votoRequest.getVoto());
        sessaoVotacao.adicionarVoto(voto);

        Voto votoSalvo = votoRepository.save(voto);

        log.info("Voto registrado com sucesso | votoId={} | sessaoId={} | voto={}",
                votoSalvo.getId(),
                sessaoVotacao.getId(),
                votoSalvo.getVoto());

        return votoSalvo;
    }

    private SessaoVotacao buscarSessaoOuLancar(Long sessaoId) {
        log.debug("Buscando sessão de votação | sessaoId={}", sessaoId);

        return sessaoVotacaoRepository.findById(sessaoId)
                .orElseThrow(() -> {
                    log.warn("Sessão de votação não encontrada | sessaoId={}", sessaoId);
                    return new SessaoVotacaoInvaalidaException(
                        "Sessão de votação com id " + sessaoId + " não encontrada");
                });
    }

    private void validarSessaoAberta(SessaoVotacao sessaoVotacao) {

        log.debug("Validando se sessão está aberta | sessaoId={}", sessaoVotacao.getId());

        if (sessaoVotacao.getDataFim() != null && sessaoVotacao.getDataFim().isBefore(LocalDateTime.now())) {
            log.warn("Tentativa de voto em sessão encerrada | sessaoId={}",
                    sessaoVotacao.getId());
            throw new SessaoVotacaoInvaalidaException("A sessão de votação está encerrada.");
        }
    }

    private void validarVotoDuplicado(SessaoVotacao sessaoVotacao, String cpf) {

        log.debug("Verificando voto duplicado | sessaoId={} | cpf={}",
                sessaoVotacao.getId(), cpf);

        if (votoRepository.existsBySessaoVotacaoAndCpf(sessaoVotacao, cpf)) {
            log.warn("Voto duplicado detectado | sessaoId={} | cpf={}",
                    sessaoVotacao.getId(), cpf);

            throw new VotoInvalidoException("O associado já votou nesta sessão.");
        }
    }
}
