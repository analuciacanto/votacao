package br.com.softdesign.votacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CriarPautaRequest {

    @NotBlank(message = "O título da pauta é obrigatório")
    @NotNull(message = "O título da pauta é obrigatório")
    private String titulo;

    private String descricao;
}
