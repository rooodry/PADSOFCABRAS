package usuarios;

import estadisticas.Estadistica;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GestorTest {

    @Test
    public void testConstructorYAddEstadistica() {
        Gestor gestor = new Gestor("Jefe", "admin123");
        
        assertEquals("Jefe", gestor.getNombre());
        
        Estadistica estadistica = new Estadistica("test.txt");
        
        assertDoesNotThrow(() -> {
            gestor.addEstadistica(estadistica);
        });

        assertEquals(1, gestor.getEstadisticas().size());
    }
}