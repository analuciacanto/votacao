package br.com.softdesign.votacao.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CriarSessaoVotacaoRequest {

    @NotNull(message = "O id da pauta é obrigatório")
    private Long pautaId;

    private int duracao;
}
