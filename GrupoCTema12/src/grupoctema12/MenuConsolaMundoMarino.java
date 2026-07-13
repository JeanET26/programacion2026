package grupoctema12;

import grupoctema12.modelo.Administrador;
import grupoctema12.modelo.AsignacionCuidado;
import grupoctema12.modelo.AsignacionGuia;
import grupoctema12.modelo.Cuidador;
import grupoctema12.modelo.Empleado;
import grupoctema12.modelo.Especie;
import grupoctema12.modelo.Guia;
import grupoctema12.modelo.Habitat;
import grupoctema12.modelo.Itinerario;
import grupoctema12.modelo.PersistenciaMundoMarino;
import grupoctema12.modelo.SistemaMundoMarino;
import grupoctema12.modelo.Usuario;
import grupoctema12.modelo.Zona;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;

/**
 * Menus de consola para los perfiles administrador, guia y cuidador.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public final class MenuConsolaMundoMarino {
    private final SistemaMundoMarino mundoMarino;
    private final Administrador administradora;
    private final PersistenciaMundoMarino persistencia;
    private final Scanner entrada = new Scanner(System.in);

    public MenuConsolaMundoMarino(SistemaMundoMarino mundoMarino, Administrador administradora,
            PersistenciaMundoMarino persistencia) {
        this.mundoMarino = mundoMarino;
        this.administradora = administradora;
        this.persistencia = persistencia;
    }

    /**
     * Muestra el menu principal y dirige la ejecucion al perfil elegido.
     */
    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n=== MUNDO MARINO ===");
            System.out.println("1. Administrador");
            System.out.println("2. Guia");
            System.out.println("3. Cuidador");
            System.out.println("0. Salir");
            opcion = leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> menuAdministrador();
                    case 2 -> menuGuia();
                    case 3 -> menuCuidador();
                    case 0 -> System.out.println("Datos guardados en " + persistencia.getArchivo() + ".");
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (IllegalArgumentException | SecurityException ex) {
                System.out.println("Operacion cancelada: " + ex.getMessage());
            }
        } while (opcion != 0);
    }

    private void menuAdministrador() {
        int opcion;
        do {
            System.out.println("\n--- MENU ADMINISTRADOR ---");
            System.out.println("1. Ver resumen completo");
            System.out.println("2. Registrar especie");
            System.out.println("3. Registrar habitat");
            System.out.println("4. Registrar zona");
            System.out.println("5. Registrar itinerario");
            System.out.println("6. Registrar guia");
            System.out.println("7. Registrar cuidador");
            System.out.println("8. Asociar especie a habitat o zona");
            System.out.println("9. Asignar cuidador a especie");
            System.out.println("10. Asignar guia a itinerario");
            System.out.println("11. Configurar especies a visitar en un itinerario");
            System.out.println("12. Modificar datos registrados");
            System.out.println("13. Modificar hora o fecha de una asignacion");
            System.out.println("14. Resetear todos los datos");
            System.out.println("0. Volver");
            opcion = leerEntero("Opcion: ");
            try {
                switch (opcion) {
                    case 1 -> mostrarResumenAdmin();
                    case 2 -> registrarEspecie();
                    case 3 -> registrarHabitat();
                    case 4 -> registrarZona();
                    case 5 -> registrarItinerario();
                    case 6 -> registrarGuia();
                    case 7 -> registrarCuidador();
                    case 8 -> relacionarEspecie();
                    case 9 -> asignarCuidador();
                    case 10 -> asignarGuia();
                    case 11 -> configurarItinerario(null, false);
                    case 12 -> modificarDatos();
                    case 13 -> modificarAsignacion();
                    case 14 -> resetearDatos();
                    case 0 -> { }
                    default -> System.out.println("Opcion invalida.");
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("No se pudo completar: " + ex.getMessage());
            }
        } while (opcion != 0);
    }

    private void registrarEspecie() {
        Especie especie = new Especie(leerTexto("Nombre en espanol: "),
                leerTexto("Nombre cientifico: "), leerTexto("Descripcion: "));
        mundoMarino.registrarEspecie(administradora, especie);
        guardarCambios();
    }

    private void registrarHabitat() {
        Habitat habitat = new Habitat(leerTexto("Nombre: "), leerTexto("Clima: "),
                leerTexto("Vegetacion predominante: "), leerConjunto("Continentes separados por coma: "));
        mundoMarino.registrarHabitat(administradora, habitat);
        guardarCambios();
    }

    private void registrarZona() {
        Zona zona = new Zona(leerTexto("Nombre: "), leerDouble("Extension en m2: "));
        mundoMarino.registrarZona(administradora, zona);
        guardarCambios();
    }

    private void registrarItinerario() {
        String codigo;
        do {
            int numero = leerEnteroEntre("Numero de itinerario (1 a 99): ", 1, 99);
            codigo = String.format("IT-%02d", numero);
            if (mundoMarino.existeCodigoItinerario(codigo)) {
                System.out.println("El codigo " + codigo + " ya existe. Ingrese otro numero.");
            }
        } while (mundoMarino.existeCodigoItinerario(codigo));
        Itinerario itinerario = new Itinerario(codigo,
                Duration.ofMinutes(leerEnteroPositivo("Duracion en minutos: ")),
                leerDoublePositivo("Longitud en km: "), leerEnteroPositivo("Maximo de visitantes: "));
        System.out.println("Itinerario creado con codigo " + codigo + ".");
        System.out.println("Debe asignar al menos una especie antes de guardarlo.");
        configurarItinerario(itinerario, true);
        mundoMarino.registrarItinerario(administradora, itinerario);
        guardarCambios();
    }

    private void configurarItinerario(Itinerario itinerarioSeleccionado, boolean requiereEspecie) {
        Itinerario itinerario = itinerarioSeleccionado == null
                ? seleccionar(mundoMarino.listarItinerarios(administradora), "itinerario", Itinerario::getCodigo)
                : itinerarioSeleccionado;
        if (itinerario == null) return;

        int opcion;
        do {
            System.out.println("\nConfigurando " + itinerario.getCodigo());
            System.out.println("1. Agregar especie a visitar");
            System.out.println("2. Ver especies y zonas incluidas");
            System.out.println("0. Terminar configuracion");
            opcion = leerEntero("Opcion: ");
            if (opcion == 1) {
                agregarEspecieAItinerario(itinerario);
            } else if (opcion == 2) {
                mostrarContenidoItinerario(itinerario);
            } else if (opcion == 0 && requiereEspecie && itinerario.getEspeciesVisitadas().isEmpty()) {
                System.out.println("No puede guardar el itinerario sin una especie asignada.");
                opcion = -1;
            } else if (opcion != 0) {
                System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void agregarEspecieAItinerario(Itinerario itinerario) {
        Especie especie = seleccionar(mundoMarino.listarEspecies(administradora), "especie", Especie::toString);
        if (especie == null) return;
        if (especie.getZona() == null) {
            System.out.println("La especie no tiene zona. Seleccione una para ubicarla.");
            Zona zona = seleccionar(mundoMarino.listarZonas(administradora), "zona", Zona::getNombre);
            if (zona == null) return;
            mundoMarino.ubicarEspecie(administradora, especie, zona);
        }
        mundoMarino.agregarEspecieAItinerario(administradora, itinerario, especie);
        System.out.println("Especie agregada con su zona " + especie.getZona().getNombre() + ".");
        guardarCambios();
    }

    private void mostrarContenidoItinerario(Itinerario itinerario) {
        System.out.println("Zonas: " + nombresZonas(itinerario.getZonas()));
        System.out.println("Especies a visitar: " + itinerario.getNumeroEspeciesVisitadas());
        for (Especie especie : itinerario.getEspeciesVisitadas()) {
            System.out.println("- " + especie + " | Zona: " + especie.getZona().getNombre());
        }
    }

    private void registrarGuia() {
        Guia guia = new Guia(leerTexto("Usuario: "), leerTexto("Nombre: "),
                leerTexto("Direccion: "), leerTexto("Telefono: "), leerFecha("Fecha de ingreso (AAAA-MM-DD): "));
        mundoMarino.registrarGuia(administradora, guia);
        guardarCambios();
    }

    private void registrarCuidador() {
        Cuidador cuidador = new Cuidador(leerTexto("Usuario: "), leerTexto("Nombre: "),
                leerTexto("Direccion: "), leerTexto("Telefono: "), leerFecha("Fecha de ingreso (AAAA-MM-DD): "));
        mundoMarino.registrarCuidador(administradora, cuidador);
        guardarCambios();
    }

    private void relacionarEspecie() {
        Especie especie = seleccionar(mundoMarino.listarEspecies(administradora), "especie", Especie::toString);
        if (especie == null) return;
        int opcion = leerEntero("1. Agregar habitat   2. Cambiar zona: ");
        if (opcion == 1) {
            Habitat habitat = seleccionar(mundoMarino.listarHabitats(administradora), "habitat", Habitat::getNombre);
            if (habitat != null) mundoMarino.asociarEspecieAHabitat(administradora, especie, habitat);
        } else if (opcion == 2) {
            Zona zona = seleccionar(mundoMarino.listarZonas(administradora), "zona", Zona::getNombre);
            if (zona != null) mundoMarino.ubicarEspecie(administradora, especie, zona);
        } else {
            System.out.println("Opcion invalida.");
            return;
        }
        guardarCambios();
    }

    private void asignarCuidador() {
        Cuidador cuidador = seleccionar(mundoMarino.listarCuidadores(administradora), "cuidador", Cuidador::getNombre);
        Especie especie = seleccionar(mundoMarino.listarEspecies(administradora), "especie", Especie::toString);
        if (cuidador != null && especie != null) {
            mundoMarino.asignarCuidador(administradora, cuidador, especie,
                    leerFecha("Fecha de asignacion (AAAA-MM-DD): "));
            guardarCambios();
        }
    }

    private void asignarGuia() {
        Guia guia = seleccionar(mundoMarino.listarGuias(administradora), "guia", Guia::getNombre);
        Itinerario itinerario = seleccionar(mundoMarino.listarItinerarios(administradora), "itinerario", Itinerario::getCodigo);
        if (guia != null && itinerario != null) {
            mundoMarino.asignarGuia(administradora, guia, itinerario, leerHora("Hora (HH:MM): "));
            guardarCambios();
        }
    }

    private void modificarDatos() {
        System.out.println("1. Especie  2. Habitat  3. Zona  4. Itinerario  5. Guia  6. Cuidador");
        switch (leerEntero("Elemento a modificar: ")) {
            case 1 -> modificarEspecie();
            case 2 -> modificarHabitat();
            case 3 -> modificarZona();
            case 4 -> modificarItinerario();
            case 5 -> modificarEmpleado(seleccionar(mundoMarino.listarGuias(administradora), "guia", Guia::getNombre));
            case 6 -> modificarEmpleado(seleccionar(mundoMarino.listarCuidadores(administradora), "cuidador", Cuidador::getNombre));
            default -> System.out.println("Opcion invalida.");
        }
    }

    private void modificarEspecie() {
        Especie especie = seleccionar(mundoMarino.listarEspecies(administradora), "especie", Especie::toString);
        if (especie != null) {
            mundoMarino.actualizarEspecie(administradora, especie, leerTexto("Nombre en espanol: "),
                    leerTexto("Nombre cientifico: "), leerTexto("Descripcion: "));
            guardarCambios();
        }
    }

    private void modificarHabitat() {
        Habitat habitat = seleccionar(mundoMarino.listarHabitats(administradora), "habitat", Habitat::getNombre);
        if (habitat != null) {
            mundoMarino.actualizarHabitat(administradora, habitat, leerTexto("Clima: "),
                    leerTexto("Vegetacion: "), leerConjunto("Continentes separados por coma: "));
            guardarCambios();
        }
    }

    private void modificarZona() {
        Zona zona = seleccionar(mundoMarino.listarZonas(administradora), "zona", Zona::getNombre);
        if (zona != null) {
            mundoMarino.actualizarZona(administradora, zona, leerDoublePositivo("Nueva extension en m2: "));
            guardarCambios();
        }
    }

    private void modificarItinerario() {
        Itinerario itinerario = seleccionar(mundoMarino.listarItinerarios(administradora), "itinerario", Itinerario::getCodigo);
        if (itinerario != null) {
            mundoMarino.actualizarItinerario(administradora, itinerario,
                    Duration.ofMinutes(leerEnteroPositivo("Duracion en minutos: ")),
                    leerDoublePositivo("Longitud en km: "), leerEnteroPositivo("Maximo de visitantes: "));
            guardarCambios();
        }
    }

    private void modificarEmpleado(Empleado empleado) {
        if (empleado != null) {
            mundoMarino.actualizarEmpleado(administradora, empleado, leerTexto("Nombre: "),
                    leerTexto("Direccion: "), leerTexto("Telefono: "),
                    leerFecha("Fecha de ingreso (AAAA-MM-DD): "));
            guardarCambios();
        }
    }

    private void modificarAsignacion() {
        int opcion = leerEntero("1. Hora de guia  2. Fecha de cuidador: ");
        if (opcion == 1) {
            Guia guia = seleccionar(mundoMarino.listarGuias(administradora), "guia", Guia::getNombre);
            if (guia == null) return;
            AsignacionGuia asignacion = seleccionar(guia.getAsignaciones(), "asignacion",
                    actual -> actual.getItinerario().getCodigo() + " - " + actual.getHora());
            if (asignacion != null) {
                mundoMarino.cambiarHoraAsignacion(administradora, asignacion, leerHora("Nueva hora (HH:MM): "));
                guardarCambios();
            }
        } else if (opcion == 2) {
            Cuidador cuidador = seleccionar(mundoMarino.listarCuidadores(administradora), "cuidador", Cuidador::getNombre);
            if (cuidador == null) return;
            AsignacionCuidado asignacion = seleccionar(cuidador.getResponsabilidades(), "asignacion",
                    actual -> actual.getEspecie().getNombreEspanol() + " - " + actual.getFechaAsignacion());
            if (asignacion != null) {
                mundoMarino.cambiarFechaAsignacion(administradora, asignacion,
                        leerFecha("Nueva fecha (AAAA-MM-DD): "));
                guardarCambios();
            }
        } else {
            System.out.println("Opcion invalida.");
        }
    }

    private void resetearDatos() {
        System.out.println("ATENCION: se eliminaran especies, habitats, zonas, itinerarios, guias,");
        System.out.println("cuidadores y todas sus asignaciones.");
        String confirmacion = leerTexto("Escriba RESETEAR para confirmar o CANCELAR para cancelar: ");
        if ("CANCELAR".equalsIgnoreCase(confirmacion)) {
            System.out.println("Reseteo cancelado.");
            return;
        }
        if (!"RESETEAR".equalsIgnoreCase(confirmacion)) {
            System.out.println("Confirmacion no valida. Reseteo cancelado.");
            return;
        }
        mundoMarino.reiniciarDatos(administradora);
        guardarCambios();
        System.out.println("Todos los datos fueron eliminados.");
    }

    private void menuGuia() {
        Guia guia = mundoMarino.ingresarComoGuia(leerTexto("Usuario de guia: "));
        Guia perfil = mundoMarino.consultarPerfilGuia(guia, guia.getId());
        mostrarEmpleado(perfil);
        if (perfil.getAsignaciones().isEmpty()) {
            System.out.println("No posee itinerarios asignados.");
        }
        for (AsignacionGuia asignacion : perfil.getAsignaciones()) {
            Itinerario itinerario = asignacion.getItinerario();
            System.out.println("\nHora: " + asignacion.getHora() + " | Itinerario " + itinerario.getCodigo()
                    + " | " + itinerario.getDuracion().toMinutes() + " min | "
                    + itinerario.getLongitudKilometros() + " km | Aforo " + itinerario.getMaximoVisitantes());
            System.out.println("Especies visitadas: " + itinerario.getNumeroEspeciesVisitadas());
            for (Especie especie : itinerario.getEspeciesVisitadas()) {
                System.out.println("  " + especie + " | Zona: " + especie.getZona().getNombre());
            }
        }
    }

    private void menuCuidador() {
        Cuidador cuidador = mundoMarino.ingresarComoCuidador(leerTexto("Usuario de cuidador: "));
        Cuidador perfil = mundoMarino.consultarPerfilCuidador(cuidador, cuidador.getId());
        mostrarEmpleado(perfil);
        if (perfil.getResponsabilidades().isEmpty()) {
            System.out.println("No posee especies asignadas.");
        }
        for (AsignacionCuidado asignacion : perfil.getResponsabilidades()) {
            Especie especie = asignacion.getEspecie();
            System.out.println("\nDesde: " + asignacion.getFechaAsignacion());
            System.out.println("Especie: " + especie + " | " + especie.getDescripcionGeneral());
            System.out.println("Zona: " + (especie.getZona() == null ? "Sin zona" : especie.getZona().getNombre()));
            System.out.println("Habitats: " + nombresHabitats(especie.getHabitats()));
            System.out.println("Cuidadores responsables: " + nombresCuidadores(especie.getResponsables()));
        }
    }

    private void mostrarResumenAdmin() {
        System.out.println("\nESPECIES");
        for (Especie especie : mundoMarino.listarEspecies(administradora)) {
            System.out.println("- " + especie + " | Zona: "
                    + (especie.getZona() == null ? "Sin zona" : especie.getZona().getNombre()));
        }
        System.out.println("\nHABITATS");
        for (Habitat habitat : mundoMarino.listarHabitats(administradora)) {
            System.out.println("- " + habitat.getNombre() + " | " + habitat.getClima() + " | " + habitat.getContinentes());
        }
        System.out.println("\nZONAS E ITINERARIOS");
        for (Zona zona : mundoMarino.listarZonas(administradora)) {
            System.out.println("- " + zona.getNombre() + " (" + zona.getExtensionMetrosCuadrados()
                    + " m2): " + zona.getEspecies());
        }
        for (Itinerario itinerario : mundoMarino.listarItinerarios(administradora)) {
            System.out.println("- " + itinerario.getCodigo() + ": " + itinerario.getNumeroEspeciesVisitadas()
                    + " especies " + itinerario.getEspeciesVisitadas() + ", zonas "
                    + nombresZonas(itinerario.getZonas()));
        }
        System.out.println("\nPERSONAL");
        mundoMarino.listarGuias(administradora).forEach(this::mostrarEmpleado);
        mundoMarino.listarCuidadores(administradora).forEach(this::mostrarEmpleado);
    }

    /** Polimorfismo: recibe un Empleado y funciona tanto con Guia como con Cuidador. */
    private void mostrarEmpleado(Empleado empleado) {
        Usuario usuario = empleado;
        System.out.println(usuario.getTipoPerfil() + ": " + empleado.getNombre() + " | Usuario: "
                + empleado.getNombreUsuario() + " | " + empleado.getDireccion() + " | "
                + empleado.getTelefono() + " | Ingreso: " + empleado.getFechaIngreso());
    }

    private String nombresHabitats(Collection<Habitat> habitats) {
        return habitats.stream().map(Habitat::getNombre).toList().toString();
    }

    private String nombresZonas(Collection<Zona> zonas) {
        return zonas.stream().map(Zona::getNombre).toList().toString();
    }

    private String nombresCuidadores(Collection<AsignacionCuidado> asignaciones) {
        return asignaciones.stream().map(actual -> actual.getCuidador().getNombre()).toList().toString();
    }

    private void guardarCambios() {
        try {
            persistencia.guardar(mundoMarino);
            System.out.println("Cambios guardados.");
        } catch (IOException ex) {
            System.out.println("No se pudieron guardar los cambios: " + ex.getMessage());
        }
    }

    private <T> T seleccionar(Collection<T> elementos, String tipo, Function<T, String> formato) {
        List<T> lista = new ArrayList<>(elementos);
        if (lista.isEmpty()) {
            System.out.println("No hay " + tipo + "s registrados.");
            return null;
        }
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ". " + formato.apply(lista.get(i)));
        }
        int opcion;
        do {
            opcion = leerEntero("Seleccione " + tipo + ": ");
            if (opcion < 1 || opcion > lista.size()) System.out.println("Numero invalido.");
        } while (opcion < 1 || opcion > lista.size());
        return lista.get(opcion - 1);
    }

    private Set<String> leerConjunto(String mensaje) {
        String texto = leerTexto(mensaje);
        Set<String> valores = new LinkedHashSet<>();
        for (String valor : texto.split(",")) {
            if (!valor.isBlank()) valores.add(valor.trim());
        }
        if (valores.isEmpty()) throw new IllegalArgumentException("Debe ingresar al menos un valor.");
        return valores;
    }

    private String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            if (!entrada.hasNextLine()) throw new IllegalArgumentException("No se ingreso un dato.");
            String valor = entrada.nextLine().trim();
            if (!valor.isEmpty()) return valor;
            System.out.println("El dato no puede estar vacio.");
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            try { return Integer.parseInt(leerTexto(mensaje)); }
            catch (NumberFormatException ex) { System.out.println("Ingrese un numero entero."); }
        }
    }

    private int leerEnteroPositivo(String mensaje) {
        int valor;
        do {
            valor = leerEntero(mensaje);
            if (valor <= 0) System.out.println("Debe ser mayor a cero.");
        } while (valor <= 0);
        return valor;
    }

    private int leerEnteroEntre(String mensaje, int minimo, int maximo) {
        int valor;
        do {
            valor = leerEntero(mensaje);
            if (valor < minimo || valor > maximo) {
                System.out.println("Ingrese un numero entre " + minimo + " y " + maximo + ".");
            }
        } while (valor < minimo || valor > maximo);
        return valor;
    }

    private double leerDouble(String mensaje) {
        while (true) {
            try { return Double.parseDouble(leerTexto(mensaje).replace(',', '.')); }
            catch (NumberFormatException ex) { System.out.println("Ingrese un numero valido."); }
        }
    }

    private double leerDoublePositivo(String mensaje) {
        double valor;
        do {
            valor = leerDouble(mensaje);
            if (valor <= 0) System.out.println("Debe ser mayor a cero.");
        } while (valor <= 0);
        return valor;
    }

    private LocalDate leerFecha(String mensaje) {
        while (true) {
            try { return LocalDate.parse(leerTexto(mensaje)); }
            catch (DateTimeParseException ex) { System.out.println("Formato invalido. Use AAAA-MM-DD."); }
        }
    }

    private LocalTime leerHora(String mensaje) {
        while (true) {
            try { return LocalTime.parse(leerTexto(mensaje)); }
            catch (DateTimeParseException ex) { System.out.println("Formato invalido. Use HH:MM."); }
        }
    }
}
