package br.com.softdesign.votacao.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultadoVotacaoResponse {
    private Long pautaId;
    private String tituloPauta;
    private long totalSim;
    private long totalNao;
    private String resultado; // "APROVADA", "REPROVADA" ou "EMPATE"

}
