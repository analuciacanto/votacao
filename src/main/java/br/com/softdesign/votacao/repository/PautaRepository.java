package br.com.softdesign.votacao.repository;

import br.com.softdesign.votacao.domain.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long> {
        @Query("""
        SELECT DISTINCT p FROM Pauta p
        LEFT JOIN FETCH p.sessoes s
        LEFT JOIN FETCH s.votos
        WHERE p.id = :id
        """)
    Optional<Pauta> findByIdWithSessoesAndVotos(@Param("id") Long id);

}
