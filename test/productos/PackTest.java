package productos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PackTest {

    private Pack packPrincipal;
    private Pack subpack;
    private ProductoTienda producto;

    @BeforeEach
    public void setUp() {
        producto = new ProductoTienda("Figura Star Wars", "Desc", "img.png");
        List<Producto> listaProductos = new ArrayList<>();
        listaProductos.add(producto);

        packPrincipal = new Pack("Pack Sci-Fi", 50.0, listaProductos);
        
        subpack = new Pack("Subpack Accesorios", 10.0, new ArrayList<>());
    }

    @Test
    public void testConstructorYGetters() {
        assertEquals("Pack Sci-Fi", packPrincipal.getNombre());
        assertEquals(50.0, packPrincipal.getPrecio());
        
        assertEquals(1, packPrincipal.getProductos().size());
        assertTrue(packPrincipal.getProductos().contains(producto));
    }

    @Test
    public void testAñadirYRemoverSubpack() {
        assertTrue(packPrincipal.getSubpacks().isEmpty(), "El pack no debería tener subpacks inicialmente.");

        packPrincipal.addSubpack(subpack);
        assertEquals(1, packPrincipal.getSubpacks().size(), "Debería haber 1 subpack.");
        assertTrue(packPrincipal.getSubpacks().contains(subpack));

        packPrincipal.removeSubpack(subpack);
        assertTrue(packPrincipal.getSubpacks().isEmpty(), "El subpack debería haber sido eliminado.");
    }

    @Test
    public void testSetPrecio() {
        packPrincipal.setPrecio(45.0);
        assertEquals(45.0, packPrincipal.getPrecio(), "El precio debería haberse actualizado a 45.0");
    }
}