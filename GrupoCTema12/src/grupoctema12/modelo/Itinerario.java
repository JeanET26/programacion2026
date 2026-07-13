package grupoctema12.modelo;

import java.io.Serializable;
import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Recorrido de Mundo Marino con especies, zonas y asignaciones de guias.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public final class Itinerario implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id = UUID.randomUUID();
    private final String codigo;
    private Duration duracion;
    private double longitudKilometros;
    private int maximoVisitantes;
    private final Set<Zona> zonas = new LinkedHashSet<>();
    private Set<Especie> especiesVisitadas = new LinkedHashSet<>();
    private final Set<AsignacionGuia> asignaciones = new LinkedHashSet<>();

    public Itinerario(String codigo, Duration duracion, double longitudKilometros,
            int maximoVisitantes) {
        this.codigo = Usuario.textoObligatorio(codigo, "codigo de itinerario");
        actualizarDatos(duracion, longitudKilometros, maximoVisitantes);
    }

    /**
     * Agrega una zona al recorrido.
     *
     * @param zona zona recorrida por el itinerario
     */
    public void agregarZona(Zona zona) {
        Objects.requireNonNull(zona, "La zona es obligatoria.");
        if (zonas.add(zona)) {
            zona.registrarItinerarioDesdeItinerario(this);
        }
    }

    /**
     * Agrega una especie al recorrido e incorpora automaticamente su zona.
     *
     * @param especie especie que se visitara
     * @throws IllegalArgumentException si la especie no posee una zona asignada
     */
    public void agregarEspecie(Especie especie) {
        Objects.requireNonNull(especie, "La especie es obligatoria.");
        if (especie.getZona() == null) {
            throw new IllegalArgumentException("La especie debe tener una zona asignada.");
        }
        especiesVisitadas.add(especie);
        agregarZona(especie.getZona());
    }

    void registrarZonaDesdeZona(Zona zona) { zonas.add(zona); }
    void registrarAsignacion(AsignacionGuia asignacion) { asignaciones.add(asignacion); }

    void actualizarDatos(Duration duracion, double longitudKilometros, int maximoVisitantes) {
        this.duracion = Objects.requireNonNull(duracion, "La duracion es obligatoria.");
        if (duracion.isNegative() || duracion.isZero() || longitudKilometros <= 0 || maximoVisitantes <= 0) {
            throw new IllegalArgumentException("La duracion, longitud y aforo deben ser positivos.");
        }
        this.longitudKilometros = longitudKilometros;
        this.maximoVisitantes = maximoVisitantes;
    }

    /**
     * Obtiene la cantidad de especies seleccionadas para el recorrido.
     *
     * @return numero de especies visitadas
     */
    public int getNumeroEspeciesVisitadas() {
        return especiesVisitadas.size();
    }

    public UUID getId() { return id; }
    public String getCodigo() { return codigo; }
    public Duration getDuracion() { return duracion; }
    public double getLongitudKilometros() { return longitudKilometros; }
    public int getMaximoVisitantes() { return maximoVisitantes; }
    public Set<Zona> getZonas() { return Collections.unmodifiableSet(zonas); }
    public Set<Especie> getEspeciesVisitadas() { return Collections.unmodifiableSet(especiesVisitadas); }
    public Set<AsignacionGuia> getAsignaciones() { return Collections.unmodifiableSet(asignaciones); }

    /** Completa datos de archivos guardados antes de existir la lista de especies. */
    void migrarEspeciesDesdeZonasSiHaceFalta() {
        if (especiesVisitadas == null) {
            especiesVisitadas = new LinkedHashSet<>();
            for (Zona zona : zonas) {
                especiesVisitadas.addAll(zona.getEspecies());
            }
        }
    }
}
