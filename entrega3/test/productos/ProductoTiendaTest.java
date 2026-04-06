package productos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductoTiendaTest {

    private ProductoTienda producto;

    @BeforeEach
    public void setUp() {
        // Inicializamos un producto tienda antes de cada test
        producto = new ProductoTienda("Figura Batman", "Figura de colección DC", "batman.png");
        producto.setPrecio(25.50);
    }

    @Test
    public void testAtributosHeredadosDeProducto() {
        assertNotNull(producto.getId(), "El ID del producto no debería ser null (UUID).");
        assertEquals("Figura Batman", producto.getNombre());
        assertEquals("Figura de colección DC", producto.getDescripcion());
        assertEquals("batman.png", producto.getImagen());
        assertNotNull(producto.getFechaPublicacion(), "La fecha de publicación debe inicializarse.");
        assertEquals(0, producto.getValoracion(), "La valoración por defecto debe ser 0.");
        assertNull(producto.getCategoria(), "La categoría por defecto debe ser null.");
    }

    @Test
    public void testAtributosPropiosProductoTienda() {
        assertEquals(25.50, producto.getPrecio(), "El precio inicial no coincide.");
        assertFalse(producto.isTiene2x1(), "Por defecto no debería tener 2x1.");
        assertEquals(0, producto.getRebajaFija());
        assertEquals(0, producto.getRebajaPorcentaje());

        // Modificamos atributos
        producto.setTiene2x1(true);
        producto.setRebajaFija(5.0);
        producto.setRebajaPorcentaje(10.0);

        // Comprobamos
        assertTrue(producto.isTiene2x1());
        assertEquals(5.0, producto.getRebajaFija());
        assertEquals(10.0, producto.getRebajaPorcentaje());
    }
}