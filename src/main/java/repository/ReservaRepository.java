package repository;

import model.Reserva;
import model.Sala;
import model.StatusReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Retorna true se houver qualquer reserva ATIVA no intervalo que coincida, com exceção do idAtual caso de atualização
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.sala = :sala " +
           "AND r.status = :status " +
           "AND (r.id != :reservaIdOuNulo OR :reservaIdOuNulo IS NULL) " +
           "AND (r.inicio < :fim AND r.fim > :inicio)")
    boolean existeSobreposicaoComOutra(
            @Param("sala") Sala sala, 
            @Param("inicio") LocalDateTime inicio, 
            @Param("fim") LocalDateTime fim, 
            @Param("status") StatusReserva status,
            @Param("reservaIdOuNulo") Long reservaIdOuNulo);

    // Método para buscar reservas de uma sala com paginação
    Page<Reserva> findBySalaId(Long salaId, Pageable pageable);

    // Método para buscar por intervalo com paginação
    @Query("SELECT r FROM Reserva r WHERE r.sala.id = :salaId " +
           "AND r.inicio >= :inicio AND r.fim <= :fim " +
           "AND r.status = :status")
    Page<Reserva> buscarPorIntervalo(
            @Param("salaId") Long salaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            @Param("status") StatusReserva status,
            Pageable pageable);
}
