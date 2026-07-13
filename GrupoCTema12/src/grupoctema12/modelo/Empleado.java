package grupoctema12.modelo;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Informacion comun a guias y cuidadores.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public abstract class Empleado extends Usuario {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private String direccion;
    private String telefono;
    private LocalDate fechaIngreso;

    protected Empleado(String nombreUsuario, Rol rol, String nombre, String direccion,
            String telefono, LocalDate fechaIngreso) {
        super(nombreUsuario, rol);
        this.nombre = textoObligatorio(nombre, "nombre");
        this.direccion = textoObligatorio(direccion, "direccion");
        this.telefono = textoObligatorio(telefono, "telefono");
        this.fechaIngreso = Objects.requireNonNull(fechaIngreso, "La fecha de ingreso es obligatoria.");
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    void actualizarDatosPersonales(String nombre, String direccion, String telefono,
            LocalDate fechaIngreso) {
        this.nombre = textoObligatorio(nombre, "nombre");
        this.direccion = textoObligatorio(direccion, "direccion");
        this.telefono = textoObligatorio(telefono, "telefono");
        this.fechaIngreso = Objects.requireNonNull(fechaIngreso, "La fecha de ingreso es obligatoria.");
    }
}
