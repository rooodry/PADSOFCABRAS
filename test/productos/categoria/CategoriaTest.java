package productos.categoria;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CategoriaTest {

    @Test
    public void testCategoriaAbstracta() {
        Categoria categoria = new Categoria("CategoriaBase") {};

        assertEquals("CategoriaBase", categoria.getNombre());
        assertNull(categoria.getSubcategoria(), "La subcategoría debe ser null inicialmente.");

        Categoria subcategoria = new Categoria("Sub") {};
        categoria.setSubCategoria(subcategoria);

        assertNotNull(categoria.getSubcategoria());
        assertEquals(subcategoria, categoria.getSubcategoria());
    }
}