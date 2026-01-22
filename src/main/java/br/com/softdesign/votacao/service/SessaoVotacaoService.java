package br.com.softdesign.votacao.service;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.dto.CriarSessaoVotacaoRequest;
import br.com.softdesign.votacao.exception.SessaoVotacaoInvaalidaException;
import br.com.softdesign.votacao.repository.PautaRepository;
import br.com.softdesign.votacao.repository.SessaoVotacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SessaoVotacaoService {

    private final SessaoVotacaoRepository sessaoVotacaoRepository;
    private final PautaRepository pautaRepository;

    public SessaoVotacao criar(CriarSessaoVotacaoRequest request) {

        // Valida existência da pauta
        Pauta pauta = validarPautaExiste(request.getPautaId());

        // Valida se já existe sessão ativa
        validarSessaoAtiva(pauta);

        // Cria a sessão de votação
        SessaoVotacao sessaoVotacao = new SessaoVotacao();
        sessaoVotacao.setPauta(pauta);
        setDuracao(sessaoVotacao, request.getDuracao());

        return sessaoVotacaoRepository.save(sessaoVotacao);
    }

    private Pauta validarPautaExiste(Long pautaId) {
        return pautaRepository.findById(pautaId)
                .orElseThrow(() -> new SessaoVotacaoInvaalidaException(
                        "A sessão de votação não pode ser criada para uma pauta inexistente"));
    }

    private void validarSessaoAtiva(Pauta pauta) {
        if (sessaoVotacaoRepository.existsByPautaIdAndDataFimAfter(pauta.getId(), LocalDateTime.now())) {
            throw new SessaoVotacaoInvaalidaException(
                    "Não é possível criar uma sessão de votação em pauta com sessão já ativa");
        }
    }

    private void setDuracao(SessaoVotacao sessao, int duracao) {
        sessao.setDataInicio(LocalDateTime.now());
        int duracaoValida = duracao > 0 ? duracao : 1; // padrão 1 minuto
        sessao.setDuracao(duracaoValida);
        sessao.setDataFim(sessao.getDataInicio().plusMinutes(duracaoValida));
    }
}
