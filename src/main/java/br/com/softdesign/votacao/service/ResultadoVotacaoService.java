package br.com.softdesign.votacao.service;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.domain.Voto;
import br.com.softdesign.votacao.domain.VotoOpcao;
import br.com.softdesign.votacao.dto.ResultadoVotacaoResponse;
import br.com.softdesign.votacao.exception.PautaInvalidaException;
import br.com.softdesign.votacao.repository.PautaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ResultadoVotacaoService {

    private final PautaRepository pautaRepository;

    public ResultadoVotacaoResponse contarVotos(Long pautaId) {
        Pauta pauta = buscarPauta(pautaId);
        Set<SessaoVotacao> sessoes = pauta.getSessoes();

        long totalSim = contarVotosPorOpcao(sessoes, VotoOpcao.SIM);
        long totalNao = contarVotosPorOpcao(sessoes, VotoOpcao.NAO);
        String resultado = calcularResultado(totalSim, totalNao);

        return new ResultadoVotacaoResponse(pauta.getId(), pauta.getTitulo(), totalSim, totalNao, resultado);
    }

    private Pauta buscarPauta(Long pautaId) {
        return pautaRepository.findByIdWithSessoesAndVotos(pautaId)
                .orElseThrow(() -> new PautaInvalidaException("Pauta não encontrada"));
    }

    private long contarVotosPorOpcao(Set<SessaoVotacao> sessoes, VotoOpcao opcao) {
        return sessoes.stream()
                .flatMap(sessao -> sessao.getVotos().stream())
                .filter(v -> v.getVoto() == opcao)
                .count();
    }

    private String calcularResultado(long totalSim, long totalNao) {
        if (totalSim == 0 && totalNao == 0) return "SEM VOTOS";
        if (totalSim > totalNao) return "APROVADA";
        if (totalNao > totalSim) return "REPROVADA";
        return "EMPATE";
    }
}
