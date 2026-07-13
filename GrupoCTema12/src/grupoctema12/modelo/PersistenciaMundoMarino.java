package grupoctema12.modelo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Guarda y recupera el estado completo en un archivo, sin usar base de datos.
 *
 * @author Jean Pier Graterol
 * @author Mauricio Castellano
 * @version 1.0
 */
public final class PersistenciaMundoMarino {
    private final Path archivo;

    public PersistenciaMundoMarino(Path archivo) {
        this.archivo = archivo;
    }

    /**
     * Serializa el estado del sistema en el archivo configurado.
     *
     * @param mundoMarino sistema que se desea guardar
     * @throws IOException si ocurre un problema al escribir el archivo
     */
    public void guardar(SistemaMundoMarino mundoMarino) throws IOException {
        Path carpeta = archivo.toAbsolutePath().getParent();
        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }
        try (ObjectOutputStream salida = new ObjectOutputStream(Files.newOutputStream(archivo))) {
            salida.writeObject(mundoMarino);
        }
    }

    /**
     * Recupera el estado guardado si existe un archivo de persistencia.
     *
     * @return sistema recuperado o vacio si el archivo no existe
     * @throws IOException si ocurre un problema de lectura
     * @throws ClassNotFoundException si no se reconoce una clase serializada
     */
    public Optional<SistemaMundoMarino> cargar() throws IOException, ClassNotFoundException {
        if (Files.notExists(archivo)) {
            return Optional.empty();
        }
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(archivo))) {
            return Optional.of((SistemaMundoMarino) entrada.readObject());
        }
    }

    public Path getArchivo() {
        return archivo;
    }
}
