package estadisticas;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import excepciones.ExcepcionUsuariosAdmin;
import usuarios.Empleado;
import usuarios.Gestor;
import usuarios.ClienteRegistrado;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class EstadisticaTest {

    @Test
    public void testFicheroAsignado() {
        Estadistica est = new Estadistica("estadisticas_prueba.txt");
        assertEquals("estadisticas_prueba.txt", est.getFichero());
    }

    @Test
    public void testEstablecerYCrearEstadisticaPedidosVacia() throws IOException {
        File tempFile = File.createTempFile("stats_pedidos", ".txt");
        tempFile.deleteOnExit();

        Estadistica estadistica = new Estadistica(tempFile.getAbsolutePath());
        estadistica.estadisticaPedidos(new ArrayList<>());

        String contenido = Files.readString(tempFile.toPath());
        
        assertTrue(contenido.contains("DNI USUARIO | PRODUCTOS"));
    }

    @Test
    public void testEstadisticaRecaudacionMesExcepcion() throws IOException {
        File tempFile = File.createTempFile("stats_recaudacion_mes", ".txt");
        tempFile.deleteOnExit();
        
        Estadistica estadistica = new Estadistica(tempFile.getAbsolutePath());
        
        Empleado empleado = new Empleado("EmpleadoTest", "pass"); 
        
        assertThrows(ExcepcionUsuariosAdmin.class, () -> {
            estadistica.estadisticaRecaudacionMes(empleado, new ArrayList<>(), new ArrayList<>());
        });
    }

    @Test
    public void testEstadisticaRecaudacionMesExito() throws IOException {
        File tempFile = File.createTempFile("stats_recaudacion_mes_exito", ".txt");
        tempFile.deleteOnExit();
        
        Estadistica estadistica = new Estadistica(tempFile.getAbsolutePath());
        Gestor gestor = new Gestor("Jefe", "pass");
        
        assertDoesNotThrow(() -> {
            estadistica.estadisticaRecaudacionMes(gestor, new ArrayList<>(), new ArrayList<>());
        });
    }

    @Test
    public void testEstadisticaRecaudacionTipoExcepcion() throws IOException {
        File tempFile = File.createTempFile("stats_recaudacion_tipo", ".txt");
        tempFile.deleteOnExit();
        
        Estadistica estadistica = new Estadistica(tempFile.getAbsolutePath());
        Empleado empleado = new Empleado("EmpleadoTest", "pass");
        
        assertThrows(ExcepcionUsuariosAdmin.class, () -> {
            estadistica.estadisticaRecaudacionTipo(empleado, new ArrayList<>(), new ArrayList<>());
        });
    }

    @Test
    public void testEstadisticasUsuariosMayorActividadComprasVacia() throws IOException {
        File tempFile = File.createTempFile("stats_actividad_compras", ".txt");
        tempFile.deleteOnExit();
        
        Estadistica estadistica = new Estadistica(tempFile.getAbsolutePath());
        estadistica.estadisticasUsuariosMayorActividadCompras(new ArrayList<>());
        
        String contenido = Files.readString(tempFile.toPath());
        
        assertTrue(contenido.contains("NÚMERO DE COMPRAS | DNI"));
    }
    
    @Test
    public void testEstadisticasUsuariosMayorActividadIntercambiosVacia() throws IOException {
        File tempFile = File.createTempFile("stats_actividad_intercambios", ".txt");
        tempFile.deleteOnExit();
        
        Estadistica estadistica = new Estadistica(tempFile.getAbsolutePath());
        estadistica.estadisticasUsuariosMayorActividadIntercambios(new ArrayList<>());
        
        String contenido = Files.readString(tempFile.toPath());
        
        assertTrue(contenido.contains("DNI | INTERCAMBIOS TOTALES"));
    }
}