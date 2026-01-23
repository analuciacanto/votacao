package br.com.softdesign.votacao.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Pauta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String descricao;

    private LocalDateTime dataCriacao;

    // Uma pauta pode ter várias sessões de votação
    @OneToMany(mappedBy = "pauta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SessaoVotacao> sessoes = new ArrayList<>();

    public Pauta(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataCriacao = LocalDateTime.now();
    }
}
