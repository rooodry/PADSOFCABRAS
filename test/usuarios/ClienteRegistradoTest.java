package usuarios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productos.ProductoSegundaMano;
import productos.ProductoTienda;
import productos.Stock;
import utilidades.Status;
import utilidades.TipoNotificacion;
import notificaciones.Notificacion;
import compras.Pedido;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteRegistradoTest {

    private ClienteRegistrado cliente;
    private Stock stock;
    private ProductoTienda producto;

    @BeforeEach
    public void setUp() {
        cliente = new ClienteRegistrado("user1", "pass", "12345678A");
        stock = new Stock();
        producto = new ProductoTienda("Comic", "Desc", "img.jpg");
        producto.setPrecio(10.0);
        stock.añadirProducto(producto, 5);
    }

    @Test
    public void testConstructorYGetters() {
        assertEquals("12345678A", cliente.getDNI());
        assertNotNull(cliente.getCartera());
        assertNotNull(cliente.getCesta());
        assertTrue(cliente.getPedidos().isEmpty());
        assertTrue(cliente.getOfertasRealizadas().isEmpty());
        assertTrue(cliente.getOfertasRecibidas().isEmpty());
        assertTrue(cliente.getIntercambios().isEmpty());
        assertTrue(cliente.getCodigos().isEmpty());
    }

    @Test
    public void testEditarPerfil() {
        cliente.editarPerfil("nuevoUser", "nuevaPass");
        assertEquals("nuevoUser", cliente.getNombre());
        assertEquals("nuevaPass", cliente.getContraseña());
    }

    @Test
    public void testAñadirALaCesta() {
        cliente.añadirALaCesta(producto, stock);
        assertFalse(cliente.getCesta().estaVacia());
        assertEquals(4, stock.getNumProductos(producto));
    }

    @Test
    public void testComprar() {
        cliente.añadirALaCesta(producto, stock);
        Status status = cliente.comprar();
        
        assertEquals(Status.OK, status);
        assertTrue(cliente.getCesta().estaVacia());
        assertEquals(1, cliente.getPedidos().size());
    }

    @Test
    public void testComprarCestaVacia() {
        Status status = cliente.comprar();
        assertEquals(Status.ERROR, status);
        assertTrue(cliente.getPedidos().isEmpty());
    }

    @Test
    public void testPagarPedidoAjenoDaError() {
        Pedido pedidoAjeno = new Pedido(new ClienteRegistrado("otro", "pass", "22222222B"), new HashMap<>());
        Status status = cliente.pagarPedido(pedidoAjeno, "1234567890123456");
        assertEquals(Status.ERROR, status);
    }

    @Test
    public void testSubirProducto() {
        ProductoSegundaMano p = new ProductoSegundaMano("Consola", "desc", "img.jpg", cliente);
        Status st = cliente.subirProducto(p);
        
        assertEquals(Status.OK, st);
        assertTrue(cliente.getCartera().getProductos().contains(p));
    }

    @Test
    public void testPagarValoracionProductoAjenoDaError() {
        ClienteRegistrado otro = new ClienteRegistrado("otro", "pass", "22222222B");
        ProductoSegundaMano p = new ProductoSegundaMano("Consola", "desc", "img.jpg", otro);
        
        Status st = cliente.pagarValoracion(p, "1234567890123456");
        assertEquals(Status.ERROR, st);
    }

    @Test
    public void testNotificaciones() {
        Notificacion notif = new Notificacion(TipoNotificacion.PEDIDO_LISTO, "Test");
        cliente.addNotificacion(notif);
        
        assertEquals(1, cliente.getNotificaciones().size());
        assertFalse(notif.getLeida());
        
        cliente.leerNotificacion(notif);
        assertTrue(notif.getLeida());
        
        cliente.borrarNotificacion(notif);
        assertTrue(cliente.getNotificaciones().isEmpty());
    }
}