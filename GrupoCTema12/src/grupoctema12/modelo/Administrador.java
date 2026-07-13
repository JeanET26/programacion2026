package grupoctema12.modelo;

/**
 * Usuario con permisos de administracion total.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public final class Administrador extends Usuario {
    private static final long serialVersionUID = 1L;
    private final String nombre;

    public Administrador(String nombreUsuario, String nombre) {
        super(nombreUsuario, Rol.ADMINISTRADOR);
        this.nombre = textoObligatorio(nombre, "nombre");
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String getTipoPerfil() {
        return "Administrador";
    }
}
