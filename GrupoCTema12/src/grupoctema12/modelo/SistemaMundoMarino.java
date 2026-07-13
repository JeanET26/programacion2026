package grupoctema12.modelo;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Fachada de aplicacion: registra objetos, relaciona datos, aplica permisos y
 * concentra las reglas de negocio de Mundo Marino.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public final class SistemaMundoMarino implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<UUID, Especie> especies = new LinkedHashMap<>();
    private final Map<UUID, Habitat> habitats = new LinkedHashMap<>();
    private final Map<UUID, Zona> zonas = new LinkedHashMap<>();
    private final Map<UUID, Itinerario> itinerarios = new LinkedHashMap<>();
    private final Map<UUID, Guia> guias = new LinkedHashMap<>();
    private final Map<UUID, Cuidador> cuidadores = new LinkedHashMap<>();

    /**
     * Registra una especie. Solo puede ser ejecutado por un administrador.
     *
     * @param administrador usuario que realiza la operacion
     * @param especie especie a registrar
     * @throws SecurityException si el usuario no es administrador
     */
    public void registrarEspecie(Usuario administrador, Especie especie) {
        ControlAcceso.exigirAdministrador(administrador);
        especies.put(especie.getId(), especie);
    }

    public void registrarHabitat(Usuario administrador, Habitat habitat) {
        ControlAcceso.exigirAdministrador(administrador);
        habitats.put(habitat.getId(), habitat);
    }

    public void registrarZona(Usuario administrador, Zona zona) {
        ControlAcceso.exigirAdministrador(administrador);
        zonas.put(zona.getId(), zona);
    }

    /**
     * Registra un itinerario con codigo unico y al menos una especie asignada.
     *
     * @param administrador usuario que realiza la operacion
     * @param itinerario itinerario a registrar
     * @throws IllegalArgumentException si el codigo se repite o no tiene especies
     */
    public void registrarItinerario(Usuario administrador, Itinerario itinerario) {
        ControlAcceso.exigirAdministrador(administrador);
        if (existeCodigoItinerario(itinerario.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un itinerario con ese codigo.");
        }
        if (itinerario.getEspeciesVisitadas().isEmpty()) {
            throw new IllegalArgumentException("El itinerario debe tener al menos una especie asignada.");
        }
        itinerarios.put(itinerario.getId(), itinerario);
    }

    /** Permite validar el codigo antes de crear un nuevo itinerario. */
    public boolean existeCodigoItinerario(String codigo) {
        return itinerarios.values().stream()
                .anyMatch(actual -> actual.getCodigo().equalsIgnoreCase(codigo));
    }

    public void registrarGuia(Usuario administrador, Guia guia) {
        ControlAcceso.exigirAdministrador(administrador);
        guias.put(guia.getId(), guia);
    }

    public void registrarCuidador(Usuario administrador, Cuidador cuidador) {
        ControlAcceso.exigirAdministrador(administrador);
        cuidadores.put(cuidador.getId(), cuidador);
    }

    public void asociarEspecieAHabitat(Usuario administrador, Especie especie, Habitat habitat) {
        ControlAcceso.exigirAdministrador(administrador);
        especie.agregarHabitat(habitat);
    }

    public void ubicarEspecie(Usuario administrador, Especie especie, Zona zona) {
        ControlAcceso.exigirAdministrador(administrador);
        especie.ubicarseEn(zona);
    }

    public void agregarZonaAItinerario(Usuario administrador, Itinerario itinerario, Zona zona) {
        ControlAcceso.exigirAdministrador(administrador);
        itinerario.agregarZona(zona);
    }

    /**
     * Asigna una especie al recorrido e incorpora automaticamente la zona de la especie.
     *
     * @param administrador usuario que realiza la operacion
     * @param itinerario itinerario a configurar
     * @param especie especie que se visitara
     */
    public void agregarEspecieAItinerario(Usuario administrador, Itinerario itinerario,
            Especie especie) {
        ControlAcceso.exigirAdministrador(administrador);
        if (especie.getZona() == null) {
            throw new IllegalArgumentException("La especie debe estar ubicada en una zona antes de agregarla al itinerario.");
        }
        itinerario.agregarEspecie(especie);
    }

    /**
     * Asigna un guia a un itinerario para una hora determinada.
     *
     * @param administrador usuario que realiza la operacion
     * @param guia guia asignado
     * @param itinerario itinerario asignado
     * @param hora hora de salida
     * @return asignacion creada
     */
    public AsignacionGuia asignarGuia(Usuario administrador, Guia guia, Itinerario itinerario,
            LocalTime hora) {
        ControlAcceso.exigirAdministrador(administrador);
        boolean repetida = guia.getAsignaciones().stream().anyMatch(asignacion ->
                asignacion.getItinerario() == itinerario && asignacion.getHora().equals(hora));
        if (repetida) {
            throw new IllegalArgumentException("El guia ya tiene asignado ese itinerario a esa hora.");
        }
        AsignacionGuia asignacion = new AsignacionGuia(guia, itinerario, hora);
        guia.registrarAsignacion(asignacion);
        itinerario.registrarAsignacion(asignacion);
        return asignacion;
    }

    /**
     * Asigna un cuidador a una especie desde una fecha determinada.
     *
     * @param administrador usuario que realiza la operacion
     * @param cuidador cuidador responsable
     * @param especie especie asignada
     * @param fechaAsignacion fecha de inicio de la responsabilidad
     * @return asignacion creada
     */
    public AsignacionCuidado asignarCuidador(Usuario administrador, Cuidador cuidador,
            Especie especie, LocalDate fechaAsignacion) {
        ControlAcceso.exigirAdministrador(administrador);
        boolean repetida = cuidador.getResponsabilidades().stream()
                .anyMatch(asignacion -> asignacion.getEspecie() == especie);
        if (repetida) {
            throw new IllegalArgumentException("El cuidador ya esta a cargo de esta especie.");
        }
        AsignacionCuidado asignacion = new AsignacionCuidado(cuidador, especie, fechaAsignacion);
        cuidador.registrarResponsabilidad(asignacion);
        especie.registrarResponsabilidad(asignacion);
        return asignacion;
    }

    public void actualizarEspecie(Usuario administrador, Especie especie, String nombreEspanol,
            String nombreCientifico, String descripcion) {
        ControlAcceso.exigirAdministrador(administrador);
        especie.actualizarDatos(nombreEspanol, nombreCientifico, descripcion);
    }

    public void actualizarHabitat(Usuario administrador, Habitat habitat, String clima,
            String vegetacion, Set<String> continentes) {
        ControlAcceso.exigirAdministrador(administrador);
        habitat.actualizarDatos(clima, vegetacion, continentes);
    }

    public void actualizarZona(Usuario administrador, Zona zona, double extension) {
        ControlAcceso.exigirAdministrador(administrador);
        zona.actualizarExtension(extension);
    }

    public void actualizarItinerario(Usuario administrador, Itinerario itinerario,
            java.time.Duration duracion, double longitud, int maximoVisitantes) {
        ControlAcceso.exigirAdministrador(administrador);
        itinerario.actualizarDatos(duracion, longitud, maximoVisitantes);
    }

    /** Usa Empleado como abstraccion: el mismo metodo sirve para guia y cuidador. */
    public void actualizarEmpleado(Usuario administrador, Empleado empleado, String nombre,
            String direccion, String telefono, LocalDate fechaIngreso) {
        ControlAcceso.exigirAdministrador(administrador);
        empleado.actualizarDatosPersonales(nombre, direccion, telefono, fechaIngreso);
    }

    public void cambiarHoraAsignacion(Usuario administrador, AsignacionGuia asignacion,
            LocalTime nuevaHora) {
        ControlAcceso.exigirAdministrador(administrador);
        boolean ocupada = asignacion.getGuia().getAsignaciones().stream().anyMatch(actual ->
                actual != asignacion && actual.getHora().equals(nuevaHora));
        if (ocupada) {
            throw new IllegalArgumentException("El guia ya posee otra asignacion en esa hora.");
        }
        asignacion.cambiarHora(nuevaHora);
    }

    public void cambiarFechaAsignacion(Usuario administrador, AsignacionCuidado asignacion,
            LocalDate nuevaFecha) {
        ControlAcceso.exigirAdministrador(administrador);
        asignacion.cambiarFechaAsignacion(nuevaFecha);
    }

    public Guia consultarPerfilGuia(Usuario solicitante, UUID idGuia) {
        Guia guia = obtener(guias, idGuia, "guia");
        ControlAcceso.exigirConsultaDeEmpleado(solicitante, guia);
        return guia;
    }

    public Cuidador consultarPerfilCuidador(Usuario solicitante, UUID idCuidador) {
        Cuidador cuidador = obtener(cuidadores, idCuidador, "cuidador");
        ControlAcceso.exigirConsultaDeEmpleado(solicitante, cuidador);
        return cuidador;
    }

    public Set<Itinerario> consultarItinerariosPropios(Usuario solicitante) {
        if (!(solicitante instanceof Guia guia)) {
            throw new SecurityException("Esta consulta corresponde unicamente a guias.");
        }
        Set<Itinerario> resultado = new LinkedHashSet<>();
        guia.getAsignaciones().forEach(asignacion -> resultado.add(asignacion.getItinerario()));
        return Set.copyOf(resultado);
    }

    public Set<Especie> consultarEspeciesACargo(Usuario solicitante) {
        if (!(solicitante instanceof Cuidador cuidador)) {
            throw new SecurityException("Esta consulta corresponde unicamente a cuidadores.");
        }
        Set<Especie> resultado = new LinkedHashSet<>();
        cuidador.getResponsabilidades().forEach(asignacion -> resultado.add(asignacion.getEspecie()));
        return Set.copyOf(resultado);
    }

    public Collection<Especie> listarEspecies(Usuario administrador) {
        ControlAcceso.exigirAdministrador(administrador);
        return Listas.inmodificable(especies.values());
    }

    public Collection<Habitat> listarHabitats(Usuario administrador) {
        ControlAcceso.exigirAdministrador(administrador);
        return Listas.inmodificable(habitats.values());
    }

    public Collection<Zona> listarZonas(Usuario administrador) {
        ControlAcceso.exigirAdministrador(administrador);
        return Listas.inmodificable(zonas.values());
    }

    public Collection<Itinerario> listarItinerarios(Usuario administrador) {
        ControlAcceso.exigirAdministrador(administrador);
        return Listas.inmodificable(itinerarios.values());
    }

    public Collection<Guia> listarGuias(Usuario administrador) {
        ControlAcceso.exigirAdministrador(administrador);
        return Listas.inmodificable(guias.values());
    }

    public Collection<Cuidador> listarCuidadores(Usuario administrador) {
        ControlAcceso.exigirAdministrador(administrador);
        return Listas.inmodificable(cuidadores.values());
    }

    /** Elimina todos los datos administrados, incluidas las asignaciones relacionadas. */
    public void reiniciarDatos(Usuario administrador) {
        ControlAcceso.exigirAdministrador(administrador);
        especies.clear();
        habitats.clear();
        zonas.clear();
        itinerarios.clear();
        guias.clear();
        cuidadores.clear();
    }

    public Guia ingresarComoGuia(String nombreUsuario) {
        return guias.values().stream()
                .filter(guia -> guia.getNombreUsuario().equalsIgnoreCase(nombreUsuario))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe un guia con ese usuario."));
    }

    public Cuidador ingresarComoCuidador(String nombreUsuario) {
        return cuidadores.values().stream()
                .filter(cuidador -> cuidador.getNombreUsuario().equalsIgnoreCase(nombreUsuario))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No existe un cuidador con ese usuario."));
    }

    private static <T> T obtener(Map<UUID, T> elementos, UUID id, String tipo) {
        T elemento = elementos.get(id);
        if (elemento == null) {
            throw new IllegalArgumentException("No existe el " + tipo + " solicitado.");
        }
        return elemento;
    }

    /** Actualiza los objetos que provienen de archivos creados por una version anterior. */
    private void readObject(ObjectInputStream entrada) throws IOException, ClassNotFoundException {
        entrada.defaultReadObject();
        itinerarios.values().forEach(Itinerario::migrarEspeciesDesdeZonasSiHaceFalta);
    }

    private static final class Listas {
        private Listas() { }
        static <T> Collection<T> inmodificable(Collection<T> elementos) {
            return java.util.List.copyOf(elementos);
        }
    }
}
