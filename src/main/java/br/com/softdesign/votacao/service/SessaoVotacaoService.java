package br.com.softdesign.votacao.service;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.dto.CriarSessaoVotacaoRequest;
import br.com.softdesign.votacao.exception.SessaoVotacaoInvaalidaException;
import br.com.softdesign.votacao.repository.PautaRepository;
import br.com.softdesign.votacao.repository.SessaoVotacaoRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessaoVotacaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaRepository pautaRepository;
    private static final Logger log = LoggerFactory.getLogger(SessaoVotacaoService.class);

    @Timed(value = "sessaoVotacao.criar.tempo", description = "Tempo gasto para criar uma sessão de votação")
    public SessaoVotacao criar(CriarSessaoVotacaoRequest request) {

        log.info("Iniciando criação de sessão de votação | pautaId={} | duracaoRecebida={}",
                request.pautaId(), request.duracao());

        // Valida existência da pauta
        Pauta pauta = validarPautaExiste(request.pautaId());

        // Valida se já existe sessão ativa
        validarSessaoAtiva(pauta);

        // Cria a sessão de votação
        SessaoVotacao sessaoVotacao = new SessaoVotacao();
        sessaoVotacao.setPauta(pauta);
        setDuracao(sessaoVotacao, request.duracao());

        SessaoVotacao sessaoSalva =  sessaoVotacaoRepository.save(sessaoVotacao);

        log.info("Sessão de votação criada com sucesso | sessaoId={} | pautaId={} | inicio={} | fim={}",
                sessaoSalva.getId(),
                pauta.getId(),
                sessaoSalva.getDataInicio(),
                sessaoSalva.getDataFim());

        return sessaoSalva;
    }

    private Pauta validarPautaExiste(Long pautaId) {
        log.debug("Validando existência da pauta | pautaId={}", pautaId);
        return pautaRepository.findById(pautaId)
                .orElseThrow(() -> {
                    log.warn("Falha ao criar sessão: pauta inexistente | pautaId={}", pautaId);
                    return new SessaoVotacaoInvaalidaException(
                        "A sessão de votação não pode ser criada para uma pauta inexistente");
                });
    }

    private void validarSessaoAtiva(Pauta pauta) {

        log.debug("Verificando existência de sessão ativa | pautaId={}", pauta.getId());

        if (sessaoVotacaoRepository.existsByPautaIdAndDataFimAfter(pauta.getId(), LocalDateTime.now())) {

            log.warn("Tentativa de criar sessão em pauta com sessão ativa | pautaId={}",
                    pauta.getId());

            throw new SessaoVotacaoInvaalidaException(
                    "Não é possível criar uma sessão de votação em pauta com sessão já ativa");
        }
    }

    private void setDuracao(SessaoVotacao sessao, int duracao) {

        log.debug("Verificando duração da sessão de votação");

        sessao.setDataInicio(LocalDateTime.now());
        int duracaoValida = duracao > 0 ? duracao : 1; // padrão 1 minuto
        sessao.setDuracao(duracaoValida);
        sessao.setDataFim(sessao.getDataInicio().plusMinutes(duracaoValida));

        log.info("Duração da sessão definida| duracaoRecebida={} | duracaoAplicada={}",
                duracao, duracaoValida);
    }
}
