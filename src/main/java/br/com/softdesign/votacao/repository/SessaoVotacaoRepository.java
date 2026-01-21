package br.com.softdesign.votacao.repository;

import br.com.softdesign.votacao.domain.SessaoVotacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {
    Collection<Object> findByPautaIdAndDataFimAfter(Long id, LocalDateTime now);
}
