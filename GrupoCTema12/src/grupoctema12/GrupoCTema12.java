package grupoctema12;

import grupoctema12.modelo.Administrador;
import grupoctema12.modelo.Cuidador;
import grupoctema12.modelo.Especie;
import grupoctema12.modelo.Guia;
import grupoctema12.modelo.Habitat;
import grupoctema12.modelo.Itinerario;
import grupoctema12.modelo.PersistenciaMundoMarino;
import grupoctema12.modelo.SistemaMundoMarino;
import grupoctema12.modelo.Zona;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

/**
 * Inicia Mundo Marino y carga los datos persistidos en un archivo local.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public class GrupoCTema12 {
    private static final Path ARCHIVO_DATOS = Path.of("datos_mundo_marino.dat");

    /**
     * Punto de entrada de la aplicacion.
     *
     * @param args argumentos de linea de comandos, no utilizados
     */
    public static void main(String[] args) {
        Administrador administradora = new Administrador("admin.ana", "Ana Ruiz");
        PersistenciaMundoMarino persistencia = new PersistenciaMundoMarino(ARCHIVO_DATOS);
        SistemaMundoMarino mundoMarino;

        try {
            mundoMarino = persistencia.cargar().orElseGet(() -> crearDatosIniciales(administradora));
            if (!java.nio.file.Files.exists(ARCHIVO_DATOS)) {
                persistencia.guardar(mundoMarino);
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("No se pudo cargar el archivo de datos. Se iniciara una sesion nueva.");
            mundoMarino = crearDatosIniciales(administradora);
        }

        new MenuConsolaMundoMarino(mundoMarino, administradora, persistencia).iniciar();
    }

    private static SistemaMundoMarino crearDatosIniciales(Administrador administradora) {
        Guia guia = new Guia("guia.lucia", "Lucia Gomez", "Av. del Mar 120",
                "11-5555-1000", LocalDate.of(2024, 3, 4));
        Cuidador cuidador = new Cuidador("cuidador.diego", "Diego Perez",
                "Calle Coral 45", "11-5555-2000", LocalDate.of(2023, 8, 15));
        Habitat arrecife = new Habitat("Arrecife coralino", "Tropical calido",
                "Corales y algas", Set.of("Oceania", "America", "Asia"));
        Zona arrecifeCentral = new Zona("Arrecife central", 850.0);
        Zona tunelOceanico = new Zona("Tunel oceanico", 1_100.0);
        Especie pezPayaso = new Especie("Pez payaso", "Amphiprion ocellaris",
                "Pez marino que vive asociado a las anemonas.");
        Especie tiburonPuntaNegra = new Especie("Tiburon de punta negra",
                "Carcharhinus melanopterus", "Tiburon costero de arrecifes tropicales.");
        Itinerario recorridoTropical = new Itinerario("IT-01", Duration.ofMinutes(45), 1.8, 25);
        SistemaMundoMarino mundoMarino = new SistemaMundoMarino();

        mundoMarino.registrarHabitat(administradora, arrecife);
        mundoMarino.registrarZona(administradora, arrecifeCentral);
        mundoMarino.registrarZona(administradora, tunelOceanico);
        mundoMarino.registrarEspecie(administradora, pezPayaso);
        mundoMarino.registrarEspecie(administradora, tiburonPuntaNegra);
        mundoMarino.registrarGuia(administradora, guia);
        mundoMarino.registrarCuidador(administradora, cuidador);
        mundoMarino.asociarEspecieAHabitat(administradora, pezPayaso, arrecife);
        mundoMarino.asociarEspecieAHabitat(administradora, tiburonPuntaNegra, arrecife);
        mundoMarino.ubicarEspecie(administradora, pezPayaso, arrecifeCentral);
        mundoMarino.ubicarEspecie(administradora, tiburonPuntaNegra, tunelOceanico);
        mundoMarino.agregarEspecieAItinerario(administradora, recorridoTropical, pezPayaso);
        mundoMarino.agregarEspecieAItinerario(administradora, recorridoTropical, tiburonPuntaNegra);
        mundoMarino.registrarItinerario(administradora, recorridoTropical);
        mundoMarino.asignarGuia(administradora, guia, recorridoTropical, LocalTime.of(10, 0));
        mundoMarino.asignarCuidador(administradora, cuidador, pezPayaso,
                LocalDate.of(2025, 2, 1));
        return mundoMarino;
    }
}
