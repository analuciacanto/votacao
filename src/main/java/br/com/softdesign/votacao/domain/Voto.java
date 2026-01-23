package br.com.softdesign.votacao.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
//Garantindo um unico registro por sessao + cpf
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sessao_id", "cpf"})
})
public class Voto
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muitos votos podem pertencer a uma sessão
    @ManyToOne
    @JoinColumn(name = "sessao_id", nullable = false)
    private SessaoVotacao sessaoVotacao;

    private String cpf;

    @Enumerated(EnumType.STRING)
    private VotoOpcao voto;

    private LocalDateTime dataVoto;

    public Voto(SessaoVotacao sessaoVotacao, String cpf, VotoOpcao voto) {
        this.sessaoVotacao = sessaoVotacao;
        this.cpf = cpf;
        this.voto = voto;
        this.dataVoto = LocalDateTime.now();
    }
}
