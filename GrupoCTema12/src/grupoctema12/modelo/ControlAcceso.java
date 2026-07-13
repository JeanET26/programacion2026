package grupoctema12.modelo;

/** Centraliza las reglas de acceso del enunciado. */
final class ControlAcceso {
    private ControlAcceso() { }

    static void exigirAdministrador(Usuario usuario) {
        if (!(usuario instanceof Administrador)) {
            throw new SecurityException("Solo un administrador puede realizar esta operacion.");
        }
    }

    static void exigirConsultaDeEmpleado(Usuario solicitante, Empleado empleado) {
        if (solicitante instanceof Administrador) {
            return;
        }
        if (solicitante.getId().equals(empleado.getId())
                && solicitante.getRol() == empleado.getRol()) {
            return;
        }
        throw new SecurityException("Solo puede consultar sus propios datos.");
    }
}
