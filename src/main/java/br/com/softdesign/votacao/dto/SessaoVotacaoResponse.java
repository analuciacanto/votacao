package br.com.softdesign.votacao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessaoVotacaoResponse {

    private Long id;
    private Long pautaId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private boolean isSessaoAberta;
    private int duracao;
}
