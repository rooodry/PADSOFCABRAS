package usuarios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productos.ProductoSegundaMano;

import static org.junit.jupiter.api.Assertions.*;

public class CarteraTest {

    private Cartera cartera;
    private ProductoSegundaMano productoSM;

    @BeforeEach
    public void setUp() {
        cartera = new Cartera();
        // Pasamos null como ClienteRegistrado (propietario) para aislar la prueba y evitar dependencias cíclicas en el setup
        productoSM = new ProductoSegundaMano("Bici clásica", "De los años 80", "bici.jpg", null);
    }

    @Test
    public void testEstadoInicial() {
        assertEquals(0, cartera.getNumProductos(), "La cartera debe iniciar con 0 productos.");
        assertNotNull(cartera.getProductos());
        assertTrue(cartera.getProductos().isEmpty());
    }

    @Test
    public void testAñadirProducto() {
        cartera.añadirProducto(productoSM);

        assertEquals(1, cartera.getNumProductos(), "El contador de productos debe ser 1.");
        assertEquals(1, cartera.getProductos().size(), "La lista de productos debe tener tamaño 1.");
        assertTrue(cartera.getProductos().contains(productoSM), "La cartera debería contener el producto añadido.");
    }
}