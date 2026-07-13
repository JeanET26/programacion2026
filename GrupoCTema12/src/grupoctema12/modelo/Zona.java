package grupoctema12.modelo;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Zona fisica de Mundo Marino que aloja especies y recorridos.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public final class Zona implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id = UUID.randomUUID();
    private final String nombre;
    private double extensionMetrosCuadrados;
    private final Set<Especie> especies = new LinkedHashSet<>();
    private final Set<Itinerario> itinerarios = new LinkedHashSet<>();

    public Zona(String nombre, double extensionMetrosCuadrados) {
        this.nombre = Usuario.textoObligatorio(nombre, "nombre de la zona");
        actualizarExtension(extensionMetrosCuadrados);
    }

    /**
     * Incorpora una especie en la zona.
     *
     * @param especie especie que sera ubicada en la zona
     */
    public void incorporarEspecie(Especie especie) {
        Objects.requireNonNull(especie, "La especie es obligatoria.");
        especie.ubicarseEn(this);
    }

    void incorporarEspecieDesdeEspecie(Especie especie) { especies.add(especie); }
    void retirarEspecieDesdeEspecie(Especie especie) { especies.remove(especie); }

    public void agregarItinerario(Itinerario itinerario) {
        Objects.requireNonNull(itinerario, "El itinerario es obligatorio.");
        if (itinerarios.add(itinerario)) {
            itinerario.registrarZonaDesdeZona(this);
        }
    }

    void registrarItinerarioDesdeItinerario(Itinerario itinerario) { itinerarios.add(itinerario); }

    void actualizarExtension(double extensionMetrosCuadrados) {
        if (extensionMetrosCuadrados <= 0) {
            throw new IllegalArgumentException("La extension debe ser positiva.");
        }
        this.extensionMetrosCuadrados = extensionMetrosCuadrados;
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public double getExtensionMetrosCuadrados() { return extensionMetrosCuadrados; }
    public Set<Especie> getEspecies() { return Collections.unmodifiableSet(especies); }
    public Set<Itinerario> getItinerarios() { return Collections.unmodifiableSet(itinerarios); }
}
