package grupoctema12.modelo;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Asociacion entre un guia y un itinerario, incluyendo la hora de salida.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public final class AsignacionGuia implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Guia guia;
    private final Itinerario itinerario;
    private LocalTime hora;

    AsignacionGuia(Guia guia, Itinerario itinerario, LocalTime hora) {
        this.guia = Objects.requireNonNull(guia, "El guia es obligatorio.");
        this.itinerario = Objects.requireNonNull(itinerario, "El itinerario es obligatorio.");
        this.hora = Objects.requireNonNull(hora, "La hora es obligatoria.");
    }

    public Guia getGuia() { return guia; }
    public Itinerario getItinerario() { return itinerario; }
    public LocalTime getHora() { return hora; }

    void cambiarHora(LocalTime hora) {
        this.hora = Objects.requireNonNull(hora, "La hora es obligatoria.");
    }
}
