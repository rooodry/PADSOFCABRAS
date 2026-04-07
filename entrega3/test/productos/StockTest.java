package productos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilidades.Status;
import static org.junit.jupiter.api.Assertions.*;

public class StockTest {

    private Stock stock;
    private ProductoTienda producto1;
    private ProductoTienda producto2;

    @BeforeEach
    public void setUp() {
        stock = new Stock();
        producto1 = new ProductoTienda("Comic 1", "Desc", "img.jpg");
        producto2 = new ProductoTienda("Juego de mesa", "Desc", "img.jpg");
    }

    @Test
    public void testAñadirProducto() {
        assertEquals(0, stock.getNumProductos(producto1), "El stock inicial debería ser 0.");
        
        stock.añadirProducto(producto1, 10);
        assertEquals(10, stock.getNumProductos(producto1), "El stock debería ser 10.");

        stock.añadirProducto(producto1, 5);
        assertEquals(15, stock.getNumProductos(producto1), "El stock debería haberse sumado a 15.");
    }

    @Test
    public void testReducirStock() {
        stock.añadirProducto(producto1, 10);
        
        stock.reducirStock(producto1, 4);
        assertEquals(6, stock.getNumProductos(producto1), "El stock debería haberse reducido a 6.");

        stock.reducirStock(producto1, 10);
        assertEquals(0, stock.getNumProductos(producto1), "El stock debería ser 0 si se reduce más de lo que hay.");
        assertFalse(stock.getProductos().containsKey(producto1), "El producto debería desaparecer del mapa.");
    }

    @Test
    public void testRetirarProducto() {
        stock.añadirProducto(producto1, 20);
        stock.añadirProducto(producto2, 5);

        stock.retirarProducto(producto1);
        
        assertEquals(0, stock.getNumProductos(producto1), "El producto 1 debería haber sido retirado.");
        assertEquals(5, stock.getNumProductos(producto2), "El producto 2 no debería verse afectado.");
    }

    @Test
    void testComprobarStockOk() {
        stock.añadirProducto(producto1, 5);
        Status status = stock.comprobarStock(producto1, 3);
        assertEquals(Status.OK, status);
    }

    @Test
    void testComprobarStockError() {
        stock.añadirProducto(producto1, 5);
        Status status = stock.comprobarStock(producto1, 10);
        assertEquals(Status.ERROR, status);
    }
}