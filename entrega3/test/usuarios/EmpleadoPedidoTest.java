package usuarios;

import compras.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilidades.EstadoPedido;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class EmpleadoPedidoTest {

    private EmpleadoPedido empleado;
    private Pedido pedido;
    private ClienteRegistrado cliente;

    @BeforeEach
    public void setUp() {
        empleado = new EmpleadoPedido("empleadoPedidos", "1234");
        cliente = new ClienteRegistrado("cli", "123", "1111A");
        
        // Creamos un pedido con un mapa vacío para la prueba
        pedido = new Pedido(cliente, new HashMap<>());
    }

    @Test
    public void testFlujoDeEstadosDePedido() {
        // Estado inicial por constructor
        assertEquals(EstadoPedido.EN_CARRITO, pedido.getEstadoPedido());

        // El empleado lo marca como listo
        empleado.marcarComoListo(pedido);
        assertEquals(EstadoPedido.LISTO, pedido.getEstadoPedido());
        assertNotNull(pedido.getFechaPreparacion(), "La fecha de preparación debe registrarse al marcar como LISTO.");

        // El empleado lo entrega
        empleado.entregarPedido(pedido);
        assertEquals(EstadoPedido.ENTREGADO, pedido.getEstadoPedido());
    }
}