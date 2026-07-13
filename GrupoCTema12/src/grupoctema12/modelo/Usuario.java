package grupoctema12.modelo;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Base abstracta de las cuentas que acceden al sistema.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public abstract class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id = UUID.randomUUID();
    private final String nombreUsuario;
    private final Rol rol;

    protected Usuario(String nombreUsuario, Rol rol) {
        this.nombreUsuario = textoObligatorio(nombreUsuario, "nombre de usuario");
        this.rol = Objects.requireNonNull(rol, "El rol es obligatorio.");
    }

    public UUID getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public Rol getRol() {
        return rol;
    }

    /**
     * Informa el perfil concreto sin que el cliente conozca la clase concreta.
     *
     * @return nombre del perfil del usuario
     */
    public abstract String getTipoPerfil();

    static String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El " + campo + " es obligatorio.");
        }
        return valor.trim();
    }
}
