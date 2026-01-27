package br.com.softdesign.votacao.dto;

import br.com.softdesign.votacao.domain.VotoOpcao;

public record VotoResponse(
        String cpf,
        VotoOpcao voto
) {}
