package grupoctema12.modelo;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Empleado que conduce itinerarios.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public final class Guia extends Empleado {
    private static final long serialVersionUID = 1L;
    private final Set<AsignacionGuia> asignaciones = new LinkedHashSet<>();

    public Guia(String nombreUsuario, String nombre, String direccion, String telefono,
            LocalDate fechaIngreso) {
        super(nombreUsuario, Rol.GUIA, nombre, direccion, telefono, fechaIngreso);
    }

    void registrarAsignacion(AsignacionGuia asignacion) {
        asignaciones.add(asignacion);
    }

    public Set<AsignacionGuia> getAsignaciones() {
        return Collections.unmodifiableSet(asignaciones);
    }

    @Override
    public String getTipoPerfil() {
        return "Guia";
    }
}
