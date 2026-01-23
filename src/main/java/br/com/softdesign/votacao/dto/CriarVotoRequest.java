package br.com.softdesign.votacao.dto;

import br.com.softdesign.votacao.domain.VotoOpcao;
import br.com.softdesign.votacao.util.CPFValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CriarVotoRequest {

    @NotNull(message = "A sessão de votação é obrigatória para registrar um voto")
    private Long sessaoVotacaoId;

    @CPFValidation
    private String cpf;

    @NotNull(message = "A opcao de voto deve sem SIM ou NAO")
    private VotoOpcao voto;

}
