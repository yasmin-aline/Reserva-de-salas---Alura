package br.com.alura.booking.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReservaCriadaEvent implements Serializable {

    private String eventId;
    private Long reservaId;
    private Long salaId;
    private Long usuarioId;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private LocalDateTime criadoEm;

    public ReservaCriadaEvent() {}

    public ReservaCriadaEvent(Long reservaId, Long salaId, Long usuarioId,
                               LocalDateTime inicio, LocalDateTime fim) {
        this.eventId   = UUID.randomUUID().toString();
        this.reservaId = reservaId;
        this.salaId    = salaId;
        this.usuarioId = usuarioId;
        this.inicio    = inicio;
        this.fim       = fim;
        this.criadoEm  = LocalDateTime.now();
    }

    public String getEventId()          { return eventId; }
    public Long getReservaId()          { return reservaId; }
    public Long getSalaId()             { return salaId; }
    public Long getUsuarioId()          { return usuarioId; }
    public LocalDateTime getInicio()    { return inicio; }
    public LocalDateTime getFim()       { return fim; }
    public LocalDateTime getCriadoEm()  { return criadoEm; }

    public void setEventId(String eventId)            { this.eventId = eventId; }
    public void setReservaId(Long reservaId)          { this.reservaId = reservaId; }
    public void setSalaId(Long salaId)                { this.salaId = salaId; }
    public void setUsuarioId(Long usuarioId)          { this.usuarioId = usuarioId; }
    public void setInicio(LocalDateTime inicio)       { this.inicio = inicio; }
    public void setFim(LocalDateTime fim)             { this.fim = fim; }
    public void setCriadoEm(LocalDateTime criadoEm)  { this.criadoEm = criadoEm; }

    @Override
    public String toString() {
        return "ReservaCriadaEvent{eventId='" + eventId + "', reservaId=" + reservaId +
               ", salaId=" + salaId + ", usuarioId=" + usuarioId + '}';
    }
}