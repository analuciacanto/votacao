package br.com.softdesign.votacao.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SessaoVotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "pauta_id", nullable = false, unique = true)
    private Pauta pauta;

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private int duracao;

    public SessaoVotacao(Pauta pauta, int duracao){
        this.pauta = pauta;
        this.duracao = duracao > 0 ? duracao : 1;
    }

    public boolean isSessaoAberta() {
        return LocalDateTime.now().isBefore(dataFim);
    }

}