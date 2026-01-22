package br.com.softdesign.votacao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class PautaResponse {

    private Long id;
    private String titulo;
    private String descricao;
    private LocalDateTime dataCriacao;
}
