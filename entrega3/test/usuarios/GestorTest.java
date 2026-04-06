package usuarios;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GestorTest {

    @Test
    public void testConstructorYAddEstadistica() {
        Gestor gestor = new Gestor("Jefe", "admin123");
        
        // Verificamos herencia
        assertEquals("Jefe", gestor.getNombre());
        
        // Testeamos añadir estadística (con null ya que no tengo la clase Estadistica)
        // Ojo, en tu Gestor.java la lista se llama "Estadistica", no tiene getter todavía,
        // pero la prueba asegura que el método no lance excepciones.
        assertDoesNotThrow(() -> {
            gestor.addEstadistica(null);
        });
    }
}