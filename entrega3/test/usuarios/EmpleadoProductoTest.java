package usuarios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productos.ProductoTienda;
import productos.Stock;
import utilidades.Status;

import static org.junit.jupiter.api.Assertions.*;

public class EmpleadoProductoTest {

    private EmpleadoProducto empleado;
    private Stock stock;
    private ProductoTienda producto;

    @BeforeEach
    public void setUp() {
        stock = new Stock();
        empleado = new EmpleadoProducto("admin_productos", "secreta", stock);
        producto = new ProductoTienda("Camiseta", "Talla M", "camis.png");
        producto.setPrecio(15.0);
    }

    @Test
    public void testAñadirProductoExito() {
        Status resultado = empleado.añadirProducto(producto, 10);
        
        assertEquals(Status.OK, resultado);
        assertEquals(10, stock.getNumProductos(producto));
    }

    @Test
    public void testAñadirProductoErrorCantidad() {
        Status resultado = empleado.añadirProducto(producto, -5);
        
        assertEquals(Status.ERROR, resultado);
        assertEquals(0, stock.getNumProductos(producto));
    }

    @Test
    public void testRetirarProducto() {
        stock.añadirProducto(producto, 5);
        
        empleado.retirarProducto(producto);
        
        assertEquals(0, stock.getNumProductos(producto));
    }

    @Test
    public void testEditarProducto() {
        empleado.editarProducto(producto, "Camiseta Roja", "Talla L", "roja.png", 20.0);

        assertEquals("Camiseta Roja", producto.getNombre());
        assertEquals("Talla L", producto.getDescripcion());
        assertEquals("roja.png", producto.getImagen());
        assertEquals(20.0, producto.getPrecio());
    }
}