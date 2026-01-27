package br.com.softdesign.votacao.dto;

import java.time.LocalDateTime;

public record SessaoVotacaoResponse(
        Long id,
        Long pautaId,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        boolean isSessaoAberta,
        int duracao
) {}
