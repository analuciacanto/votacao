package br.com.softdesign.votacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarPautaRequest(
        @NotBlank(message = "O título da pauta é obrigatório")
        @NotNull(message = "O título da pauta é obrigatório")
        String titulo,
        String descricao
) { }
