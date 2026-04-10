package productos.categoria;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ComicTest {

    @Test
    public void testConstructorYGetters() {
        Comic comic = new Comic("Spiderman", 150, "Stan Lee", "Marvel", Genero.AVENTURA, 1962);

        assertEquals("Comic:Spiderman", comic.getNombre(), "El constructor debe añadir el prefijo 'Comic:'");
        assertEquals(150, comic.getNumPaginas());
        assertEquals("Stan Lee", comic.getAutor());
        assertEquals("Marvel", comic.getEditorial());
        assertEquals(Genero.AVENTURA, comic.getGenero());
        assertEquals(1962, comic.getAño());
        assertNull(comic.getSubcategoria());
    }
}