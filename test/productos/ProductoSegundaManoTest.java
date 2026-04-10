package productos;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usuarios.ClienteRegistrado;
import utilidades.EstadoConservacion;
import utilidades.EstadoProducto;
import static org.junit.jupiter.api.Assertions.*;

public class ProductoSegundaManoTest {

    private ProductoSegundaMano productoSM;
    private ClienteRegistrado propietario;

    @BeforeEach
    public void setUp() {
        propietario = new ClienteRegistrado("user1", "pass", "12345678A");
        productoSM = new ProductoSegundaMano("Comic Antiguo", "Edición 1990", "comic.jpg", propietario);
    }

    @Test
    public void testConstructorYEstadoInicial() {
        assertEquals("Comic Antiguo", productoSM.getNombre());
        assertEquals(propietario, productoSM.getPropietario(), "El propietario debe ser el asignado en el constructor.");
        assertFalse(productoSM.getDisponibilidad(), "El producto no debe estar disponible recién creado.");
        assertEquals(EstadoProducto.PENDIENTE_DE_VALORAR, productoSM.getEstadoProducto());
    }

    @Test
    public void testSetValoracion() {
        productoSM.setValoracion(4, 50.0, EstadoConservacion.MUY_BUENO);
        
        assertTrue(productoSM.getEstaValorado());
        assertEquals(4, productoSM.getValoracionEmpleado());
        assertEquals(50.0, productoSM.getValorEstimado(), 0.001);
        assertEquals(EstadoConservacion.MUY_BUENO, productoSM.getEstadoConservacion());
        assertEquals(EstadoProducto.VALORADO, productoSM.getEstadoProducto());
    }
}