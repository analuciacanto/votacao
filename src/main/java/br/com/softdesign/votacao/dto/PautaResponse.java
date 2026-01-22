package br.com.softdesign.votacao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PautaResponse {

    private Long id;
    private String titulo;
    private String descricao;
}
