package br.com.alura.room.repository;

import br.com.alura.room.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    Optional<Sala> findByNome(String nome);
}
