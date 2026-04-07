package usuarios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productos.ProductoSegundaMano;

import static org.junit.jupiter.api.Assertions.*;

public class CarteraTest {

    private Cartera cartera;
    private ProductoSegundaMano productoSM;
    private ClienteRegistrado cliente;

    @BeforeEach
    public void setUp() {
        cartera = new Cartera();
        cliente = new ClienteRegistrado("usuario1", "pass", "12345678A");
        productoSM = new ProductoSegundaMano("Bici clásica", "De los años 80", "bici.jpg", cliente);
    }

    @Test
    public void testEstadoInicial() {
        assertEquals(0, cartera.getNumProductos());
        assertNotNull(cartera.getProductos());
        assertTrue(cartera.getProductos().isEmpty());
    }

    @Test
    public void testAñadirProducto() {
        cartera.añadirProducto(productoSM);

        assertEquals(1, cartera.getNumProductos());
        assertEquals(1, cartera.getProductos().size());
        assertTrue(cartera.getProductos().contains(productoSM));
    }
}