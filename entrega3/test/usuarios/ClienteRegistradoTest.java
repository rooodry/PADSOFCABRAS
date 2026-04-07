package usuarios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productos.ProductoSegundaMano;
import utilidades.Status;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteRegistradoTest {

    private ClienteRegistrado cliente;

    @BeforeEach
    public void setUp() {
        cliente = new ClienteRegistrado("user1", "pass", "12345678A");
    }

    @Test
    public void testConstructorYGetters() {
        assertEquals("12345678A", cliente.getDNI());
        assertNotNull(cliente.getCartera());
        assertNotNull(cliente.getCesta());
        assertTrue(cliente.getPedidos().isEmpty());
    }

    @Test
    public void testEditarPerfil() {
        cliente.editarPerfil("nuevoUser", "nuevaPass");
        assertEquals("nuevoUser", cliente.getNombre());
        assertEquals("nuevaPass", cliente.getContraseña());
    }

    @Test
    public void testSubirProducto() {
        ProductoSegundaMano p = new ProductoSegundaMano("Consola", "desc", "img.jpg", cliente);
        Status st = cliente.subirProducto(p);
        
        assertEquals(Status.OK, st);
        assertTrue(cliente.getCartera().getProductos().contains(p));
    }
}