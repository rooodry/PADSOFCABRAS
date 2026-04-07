package compras;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import productos.ProductoTienda;
import usuarios.ClienteRegistrado; 
import utilidades.EstadoPedido;

public class PedidoTest {

    private Pedido pedido;
    private ProductoTienda p1;
    private Map<ProductoTienda, Integer> productos;
    private ClienteRegistrado cliente; 

    @BeforeEach
    void setUp() {
        p1 = new ProductoTienda("Funko Iron Man", "Figura", "iron.jpg");
        productos = new HashMap<>();
        cliente = new ClienteRegistrado("usuario1", "pass", "12345678A");
    }

    @Test
    void testEstadoInicial() {
        pedido = new Pedido(cliente, productos);
        
        assertEquals(EstadoPedido.EN_CARRITO, pedido.getEstadoPedido());
        assertNotNull(pedido.getCodigo());
        assertNotNull(pedido.getFechaRealizacion());
        assertNull(pedido.getFechaPago());
    }

    @Test
    void testCambioEstadoEnPreparacion() {
        pedido = new Pedido(cliente, productos);
        pedido.setEstadoPedido(EstadoPedido.EN_PREPARACION);

        assertEquals(EstadoPedido.EN_PREPARACION, pedido.getEstadoPedido());
        assertNotNull(pedido.getFechaPago()); 
    }

    @Test
    void testCancelarPedido() {
        pedido = new Pedido(cliente, productos);
        pedido.cancelar();
        assertEquals(EstadoPedido.CANCELADO, pedido.getEstadoPedido());
    }

    @Test
    void testCalcularPrecioTotalBasico() {
        p1.setPrecio(10.0);
        productos.put(p1, 4);
        
        pedido = new Pedido(cliente, productos);

        assertEquals(40.0, pedido.calcularPrecioTotal(), 0.01);
    }

    @Test
    void testCalcularPrecioTotalCon2x1YDescuentoVolumen5PorCiento() {
        p1.setPrecio(20.0);
        p1.setTiene2x1(true);
        
        productos.put(p1, 8);
        pedido = new Pedido(cliente, productos);

        assertEquals(76.0, pedido.calcularPrecioTotal(), 0.01);
    }

    @Test
    void testCalcularPrecioTotalConRebajaFijaYDescuentoVolumen15PorCiento() {
        p1.setPrecio(100.0);
        p1.setRebajaFija(10.0);
        
        productos.put(p1, 2);
        pedido = new Pedido(cliente, productos);

        assertEquals(153.0, pedido.calcularPrecioTotal(), 0.01);
    }
}