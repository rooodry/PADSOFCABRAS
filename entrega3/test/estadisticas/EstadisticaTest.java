package estadisticas;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class EstadisticaTest {

    @Test
    public void testFicheroAsignado() {
        Estadistica est = new Estadistica("estadisticas.txt");
        assertEquals("estadisticas.txt", est.getFichero());
    }

    @Test
    public void testEstablecerYCrearEstadisticaPedidosVacia() throws IOException {
        File tempFile = File.createTempFile("stats_pedidos", ".txt");
        tempFile.deleteOnExit();

        Estadistica estadistica = new Estadistica(tempFile.getAbsolutePath());
        
        estadistica.estadisticaPedidos(new ArrayList<>());

        String contenido = Files.readString(tempFile.toPath());
        
        assertTrue(contenido.contains("DNI USUARIO | PRODUCTOS"), "El archivo debería contener la cabecera.");
    }
}