package productos.categoria;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FiguraTest {

    @Test
    public void testConstructorYGetters() {
        Figura figura = new Figura("Batman", 15.5, "Funko", "Vinilo");

        assertEquals("Figura:Batman", figura.getNombre(), "El constructor debe añadir el prefijo 'Figura:'");
        assertEquals(15.5, figura.getAltura(), 0.001);
        assertEquals("Funko", figura.getMarca());
        assertEquals("Vinilo", figura.getMaterial());
        assertNull(figura.getSubcategoria());
    }
}