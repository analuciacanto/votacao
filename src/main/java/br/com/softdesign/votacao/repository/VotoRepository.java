package br.com.softdesign.votacao.repository;

import br.com.softdesign.votacao.domain.SessaoVotacao;
import br.com.softdesign.votacao.domain.Voto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsBySessaoVotacaoAndCpf(SessaoVotacao sessaoVotacao, String cpf);

}
