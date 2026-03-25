package test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import compras.Pedido;
import productos.ProductoTienda;
import usuarios.ClienteRegistrado;
import utilidades.EstadoPedido;

public class PedidoTest {

    private Pedido pedido;
    private ClienteRegistrado clienteMock;
    private ProductoTienda productoMock1;
    private ProductoTienda productoMock2;
    private Map<ProductoTienda, Integer> productosMap;

    @BeforeEach
    public void setUp() {
        // 1. Creamos los mocks de las dependencias
        clienteMock = mock(ClienteRegistrado.class);
        productoMock1 = mock(ProductoTienda.class);
        productoMock2 = mock(ProductoTienda.class);

        // 2. Configuramos el comportamiento de los productos mockeados
        // Supongamos que el producto 1 cuesta 15.0 y el producto 2 cuesta 25.0
        when(productoMock1.getPrecio()).thenReturn(15.0);
        when(productoMock2.getPrecio()).thenReturn(25.0);

        // 3. Preparamos el mapa de productos (2 unidades del P1, 1 unidad del P2)
        productosMap = new HashMap<>();
        productosMap.put(productoMock1, 2); // 2 * 15.0 = 30.0
        productosMap.put(productoMock2, 1); // 1 * 25.0 = 25.0
                                            // Total esperado = 55.0

        // 4. Instanciamos el pedido
        pedido = new Pedido(clienteMock, productosMap);
    }

    @Test
    public void testConstructorEstadoInicial() {
        // Comprobamos que el pedido arranca en el estado correcto y con datos válidos
        assertEquals(EstadoPedido.EN_CARRITO, pedido.getEstadoPedido(), "El estado inicial debe ser EN_CARRITO");
        assertNotNull(pedido.getCodigo(), "El código no debe ser nulo");
        assertNotNull(pedido.getFechaRealizacion(), "La fecha de realización no debe ser nula");
        
        // Las demás fechas deben ser nulas al inicio
        assertNull(pedido.getFechaPago(), "La fecha de pago debe ser nula inicialmente");
        assertNull(pedido.getFechaPreparacion(), "La fecha de preparación debe ser nula inicialmente");
        assertNull(pedido.getFechaRecogida(), "La fecha de recogida debe ser nula inicialmente");
        
        // El cliente debe ser el que pasamos por parámetro
        assertEquals(clienteMock, pedido.getCliente(), "El cliente debe coincidir con el asignado");
    }

    @Test
    public void testSetEstadoPedidoEnPreparacion() {
        pedido.setEstadoPedido(EstadoPedido.EN_PREPARACION);
        
        assertEquals(EstadoPedido.EN_PREPARACION, pedido.getEstadoPedido());
        assertNotNull(pedido.getFechaPago(), "Al pasar a EN_PREPARACION, se debe registrar la fecha de pago");
    }

    @Test
    public void testSetEstadoPedidoListo() {
        pedido.setEstadoPedido(EstadoPedido.LISTO);
        
        assertEquals(EstadoPedido.LISTO, pedido.getEstadoPedido());
        assertNotNull(pedido.getFechaPreparacion(), "Al pasar a LISTO, se debe registrar la fecha de preparación");
    }

    @Test
    public void testSetEstadoPedidoEntregado() {
        pedido.setEstadoPedido(EstadoPedido.ENTREGADO);
        
        assertEquals(EstadoPedido.ENTREGADO, pedido.getEstadoPedido());
        assertNotNull(pedido.getFechaRecogida(), "Al pasar a ENTREGADO, se debe registrar la fecha de recogida");
    }

    @Test
    public void testCancelarPedido() {
        pedido.cancelar();
        assertEquals(EstadoPedido.CANCELADO, pedido.getEstadoPedido(), "El método cancelar debe cambiar el estado a CANCELADO");
    }

    @Test
    public void testCalcularPrecioTotal() {
        // Precio esperado: (2 * 15.0) + (1 * 25.0) = 55.0
        double totalEsperado = 55.0;
        double totalCalculado = pedido.calcularPrecioTotal();
        
        assertEquals(totalEsperado, totalCalculado, "El cálculo del precio total no es correcto");
    }
}