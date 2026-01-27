package br.com.softdesign.votacao.dto;

import br.com.softdesign.votacao.domain.VotoOpcao;
import br.com.softdesign.votacao.util.CPFValidation;
import jakarta.validation.constraints.NotNull;

public record CriarVotoRequest(
        @NotNull(message = "A sessão de votação é obrigatória para registrar um voto")
        Long sessaoVotacaoId,

        @CPFValidation
        String cpf,

        @NotNull(message = "A opcao de voto deve sem SIM ou NAO")
        VotoOpcao voto
) {}
