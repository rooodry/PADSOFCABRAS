package sistema;

import compras.Pedido;
import excepciones.ExcepcionUsuariosAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productos.ProductoTienda;
import productos.Stock;
import usuarios.*;
import utilidades.TiposEmpleado;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SistemaTest {

    private Sistema sistema;
    private Gestor admin;
    private ClienteRegistrado clienteNormal;
    private Stock stock;
    private ProductoTienda productoCaro;
    private ProductoTienda productoBarato;

    @BeforeEach
    public void setUp() {
        sistema = new Sistema();
        admin = new Gestor("Jefe", "adminPass");
        clienteNormal = new ClienteRegistrado("Paco", "pass", "12345678Z");
        
        stock = new Stock();
        sistema.setStock(stock);

        productoCaro = new ProductoTienda("Consola", "Videoconsola", "consola.jpg");
        productoCaro.setPrecio(250.0); 
        
        productoBarato = new ProductoTienda("Llavero", "Llavero friki", "llavero.jpg");
        productoBarato.setPrecio(5.0); 

        stock.añadirProducto(productoCaro, 10);
        stock.añadirProducto(productoBarato, 50);
    }

    @Test
    public void testDarAltaEmpleado_ComoAdmin() {
        assertDoesNotThrow(() -> {
            sistema.darAltaEmpleado(admin, "NuevoEmpleado", "1234", TiposEmpleado.EMPLEADOS_PRODUCTO);
        });
    }

    @Test
    public void testDarAltaEmpleado_ComoNoAdmin_LanzaExcepcion() {
        assertThrows(ExcepcionUsuariosAdmin.class, () -> {
            sistema.darAltaEmpleado(clienteNormal, "Intruso", "1234", TiposEmpleado.EMPLEADOS_PRODUCTO);
        });
    }

    @Test
    public void testActualizarStock_ComoAdmin() {
        assertDoesNotThrow(() -> {
            sistema.actualizarStock(admin, productoCaro, 5);
        });

        assertEquals(15, stock.getNumProductos(productoCaro));
    }

    @Test
    public void testRegistrarPedido_ConRegalo() {
        Map<ProductoTienda, Integer> productosPedido = new HashMap<>();
        productosPedido.put(productoCaro, 1); 
        Pedido pedido = new Pedido(clienteNormal, productosPedido);

        sistema.registrarPedido(pedido);

        assertNotNull(pedido.getRegalo());
        assertEquals(productoBarato, pedido.getRegalo());
    }

    @Test
    public void testCancelarPedido_RestauraStock() {
        Map<ProductoTienda, Integer> productosPedido = new HashMap<>();
        productosPedido.put(productoCaro, 2); 
        Pedido pedido = new Pedido(clienteNormal, productosPedido);
        
        stock.reducirStock(productoCaro, 2);
        assertEquals(8, stock.getNumProductos(productoCaro));

        sistema.registrarPedido(pedido);
        
        sistema.cancelarPedido(pedido);

        assertEquals(10, stock.getNumProductos(productoCaro));
    }

    @Test
    public void testRecomendarProductos() {

    }
}