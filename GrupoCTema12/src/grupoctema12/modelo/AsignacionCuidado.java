package grupoctema12.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Asociacion entre un cuidador y una especie, con su fecha de responsabilidad.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public final class AsignacionCuidado implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Cuidador cuidador;
    private final Especie especie;
    private LocalDate fechaAsignacion;

    AsignacionCuidado(Cuidador cuidador, Especie especie, LocalDate fechaAsignacion) {
        this.cuidador = Objects.requireNonNull(cuidador, "El cuidador es obligatorio.");
        this.especie = Objects.requireNonNull(especie, "La especie es obligatoria.");
        this.fechaAsignacion = Objects.requireNonNull(fechaAsignacion, "La fecha es obligatoria.");
    }

    public Cuidador getCuidador() { return cuidador; }
    public Especie getEspecie() { return especie; }
    public LocalDate getFechaAsignacion() { return fechaAsignacion; }

    void cambiarFechaAsignacion(LocalDate fechaAsignacion) {
        this.fechaAsignacion = Objects.requireNonNull(fechaAsignacion, "La fecha es obligatoria.");
    }
}
