package grupoctema12.modelo;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Especie presente en Mundo Marino, con habitats, zona y responsables.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public final class Especie implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id = UUID.randomUUID();
    private String nombreEspanol;
    private String nombreCientifico;
    private String descripcionGeneral;
    private final Set<Habitat> habitats = new LinkedHashSet<>();
    private final Set<AsignacionCuidado> responsables = new LinkedHashSet<>();
    private Zona zona;

    public Especie(String nombreEspanol, String nombreCientifico, String descripcionGeneral) {
        this.nombreEspanol = Usuario.textoObligatorio(nombreEspanol, "nombre en espanol");
        this.nombreCientifico = Usuario.textoObligatorio(nombreCientifico, "nombre cientifico");
        this.descripcionGeneral = Usuario.textoObligatorio(descripcionGeneral, "descripcion");
    }

    /**
     * Asocia un habitat natural a la especie y mantiene la relacion bidireccional.
     *
     * @param habitat habitat que ocupa la especie
     */
    public void agregarHabitat(Habitat habitat) {
        Objects.requireNonNull(habitat, "El habitat es obligatorio.");
        if (habitats.add(habitat)) {
            habitat.registrarEspecieDesdeEspecie(this);
        }
    }

    void registrarHabitatDesdeHabitat(Habitat habitat) { habitats.add(habitat); }

    /**
     * Ubica la especie en una zona fisica de Mundo Marino.
     *
     * @param nuevaZona zona en la que sera ubicada la especie
     */
    public void ubicarseEn(Zona nuevaZona) {
        Objects.requireNonNull(nuevaZona, "La zona es obligatoria.");
        if (zona == nuevaZona) {
            return;
        }
        if (zona != null) {
            zona.retirarEspecieDesdeEspecie(this);
        }
        zona = nuevaZona;
        nuevaZona.incorporarEspecieDesdeEspecie(this);
    }

    void registrarResponsabilidad(AsignacionCuidado responsabilidad) {
        responsables.add(responsabilidad);
    }

    void actualizarDatos(String nombreEspanol, String nombreCientifico, String descripcionGeneral) {
        this.nombreEspanol = Usuario.textoObligatorio(nombreEspanol, "nombre en espanol");
        this.nombreCientifico = Usuario.textoObligatorio(nombreCientifico, "nombre cientifico");
        this.descripcionGeneral = Usuario.textoObligatorio(descripcionGeneral, "descripcion");
    }

    public UUID getId() { return id; }
    public String getNombreEspanol() { return nombreEspanol; }
    public String getNombreCientifico() { return nombreCientifico; }
    public String getDescripcionGeneral() { return descripcionGeneral; }
    public Zona getZona() { return zona; }
    public Set<Habitat> getHabitats() { return Collections.unmodifiableSet(habitats); }
    public Set<AsignacionCuidado> getResponsables() { return Collections.unmodifiableSet(responsables); }

    @Override
    public String toString() {
        return nombreEspanol + " (" + nombreCientifico + ")";
    }
}
