package br.com.softdesign.votacao.dto;

import br.com.softdesign.votacao.domain.VotoOpcao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VotoResponse {

    private String cpf;
    private VotoOpcao voto;
}
