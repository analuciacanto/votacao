package br.com.softdesign.votacao.service;

import br.com.softdesign.votacao.domain.Pauta;
import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.domain.VotoOpcao;
import br.com.softdesign.votacao.dto.ResultadoVotacaoResponse;
import br.com.softdesign.votacao.exception.PautaInvalidaException;
import br.com.softdesign.votacao.repository.PautaRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ResultadoVotacaoService {

    private final PautaRepository pautaRepository;
    private static final Logger log = LoggerFactory.getLogger(ResultadoVotacaoService.class);

    @Timed(value = "resultadoVotacao.contarVotos.tempo", description = "Tempo gasto para apuração de votos")
    public ResultadoVotacaoResponse contarVotos(Long pautaId) {
        log.info("Iniciando apuração de votos | pautaId={}", pautaId);

        Pauta pauta = buscarPauta(pautaId);
        Set<SessaoVotacao> sessoes = pauta.getSessoes();

        log.debug("Quantidade de sessões encontradas para apuração | pautaId={} | totalSessoes={}",
                pautaId, sessoes.size());

        long totalSim = contarVotosPorOpcao(sessoes, VotoOpcao.SIM);
        long totalNao = contarVotosPorOpcao(sessoes, VotoOpcao.NAO);
        String resultado = calcularResultado(totalSim, totalNao);

        log.info("Apuração finalizada | pautaId={} | sim={} | nao={} | resultado={}",
                pautaId, totalSim, totalNao, resultado);

        return new ResultadoVotacaoResponse(pauta.getId(), pauta.getTitulo(), totalSim, totalNao, resultado);
    }

    private Pauta buscarPauta(Long pautaId) {
        log.debug("Buscando pauta para apuração | pautaId={}", pautaId);

        return pautaRepository.findByIdWithSessoesAndVotos(pautaId)
                .orElseThrow(() -> {
                    log.warn("Pauta não encontrada para apuração | pautaId={}", pautaId);
                    return new PautaInvalidaException("Pauta não encontrada");
                });
    }

    private long contarVotosPorOpcao(Set<SessaoVotacao> sessoes, VotoOpcao opcao) {
        log.debug("Contando votos por opção | opcao={} | totalSessoes={}",
                opcao, sessoes.size());
        return sessoes.stream()
                .flatMap(sessao -> sessao.getVotos().stream())
                .filter(v -> v.getVoto() == opcao)
                .count();
    }

    private String calcularResultado(long totalSim, long totalNao) {
        log.debug("Calculando resultado | totalSim={} | totalNao={}", totalSim, totalNao);

        if (totalSim == 0 && totalNao == 0) return "SEM VOTOS";
        if (totalSim > totalNao) return "APROVADA";
        if (totalNao > totalSim) return "REPROVADA";
        return "EMPATE";
    }
}
