package br.com.softdesign.votacao.dto;

import jakarta.validation.constraints.NotNull;

public record CriarSessaoVotacaoRequest(
        @NotNull(message = "O id da pauta é obrigatório") Long pautaId,
        int duracao
) {}
