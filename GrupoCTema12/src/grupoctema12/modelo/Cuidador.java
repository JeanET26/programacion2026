package grupoctema12.modelo;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Empleado responsable del cuidado de una o varias especies.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public final class Cuidador extends Empleado {
    private static final long serialVersionUID = 1L;
    private final Set<AsignacionCuidado> responsabilidades = new LinkedHashSet<>();

    public Cuidador(String nombreUsuario, String nombre, String direccion, String telefono,
            LocalDate fechaIngreso) {
        super(nombreUsuario, Rol.CUIDADOR, nombre, direccion, telefono, fechaIngreso);
    }

    void registrarResponsabilidad(AsignacionCuidado responsabilidad) {
        responsabilidades.add(responsabilidad);
    }

    public Set<AsignacionCuidado> getResponsabilidades() {
        return Collections.unmodifiableSet(responsabilidades);
    }

    @Override
    public String getTipoPerfil() {
        return "Cuidador";
    }
}
