package productos.categoria;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JuegoTest {

    @Test
    public void testConstructorYGetters() {
        Juego juego = new Juego("Catan", 4, 10, TipoJuego.JUEGO_MESA);

        assertEquals("Juego:Catan", juego.getNombre(), "El constructor debe añadir el prefijo 'Juego:'");
        assertEquals(4, juego.getNumJugadores());
        assertEquals(10, juego.getEdadMinima());
        assertEquals(TipoJuego.JUEGO_MESA, juego.getTipoJuego());
        assertNull(juego.getSubcategoria());
    }
}