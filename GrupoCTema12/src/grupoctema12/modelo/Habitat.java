package grupoctema12.modelo;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Habitat natural que puede ser compartido por varias especies.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public final class Habitat implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id = UUID.randomUUID();
    private final String nombre;
    private String clima;
    private String vegetacionPredominante;
    private final Set<String> continentes = new LinkedHashSet<>();
    private final Set<Especie> especies = new LinkedHashSet<>();

    public Habitat(String nombre, String clima, String vegetacionPredominante,
            Set<String> continentes) {
        this.nombre = Usuario.textoObligatorio(nombre, "nombre del habitat");
        actualizarDatos(clima, vegetacionPredominante, continentes);
    }

    /**
     * Asocia una especie al habitat y mantiene la relacion bidireccional.
     *
     * @param especie especie que vive en el habitat
     */
    public void agregarEspecie(Especie especie) {
        Objects.requireNonNull(especie, "La especie es obligatoria.");
        if (especies.add(especie)) {
            especie.registrarHabitatDesdeHabitat(this);
        }
    }

    void registrarEspecieDesdeEspecie(Especie especie) {
        especies.add(especie);
    }

    void actualizarDatos(String clima, String vegetacionPredominante, Set<String> continentes) {
        this.clima = Usuario.textoObligatorio(clima, "clima");
        this.vegetacionPredominante = Usuario.textoObligatorio(vegetacionPredominante, "vegetacion");
        Objects.requireNonNull(continentes, "Los continentes son obligatorios.");
        if (continentes.isEmpty()) {
            throw new IllegalArgumentException("Debe indicarse al menos un continente.");
        }
        this.continentes.clear();
        this.continentes.addAll(continentes);
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getClima() { return clima; }
    public String getVegetacionPredominante() { return vegetacionPredominante; }
    public Set<String> getContinentes() { return Collections.unmodifiableSet(continentes); }
    public Set<Especie> getEspecies() { return Collections.unmodifiableSet(especies); }
}
