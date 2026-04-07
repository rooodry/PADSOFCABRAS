package usuarios;

import intercambios.Intercambio;
import intercambios.Oferta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productos.ProductoSegundaMano;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class EmpleadoIntercambioTest {

    private EmpleadoIntercambio empleado;
    private ClienteRegistrado lanzador;
    private ClienteRegistrado receptor;
    private ProductoSegundaMano prodLanzador;
    private ProductoSegundaMano prodReceptor;
    private Oferta oferta;
    private Intercambio intercambio;

    @BeforeEach
    public void setUp() {
        empleado = new EmpleadoIntercambio("empleadoInter", "pass");
        
        lanzador = new ClienteRegistrado("lanzador", "pass", "111A");
        receptor = new ClienteRegistrado("receptor", "pass", "222B");

        prodLanzador = new ProductoSegundaMano("Consola", "Antigua", "c.jpg", lanzador);
        prodReceptor = new ProductoSegundaMano("Juego", "Usado", "j.jpg", receptor);

        lanzador.subirProducto(prodLanzador);
        receptor.subirProducto(prodReceptor);

        oferta = new Oferta(prodLanzador, prodReceptor, receptor, lanzador);
        intercambio = new Intercambio(new Date(), oferta);
        
        intercambio.aceptarOferta(new Date());
    }

    @Test
    public void testTransferirPropiedad() {
        assertTrue(lanzador.getCartera().getProductos().contains(prodLanzador));
        assertTrue(receptor.getCartera().getProductos().contains(prodReceptor));

        empleado.transferirPropiedad(intercambio);

        assertFalse(lanzador.getCartera().getProductos().contains(prodLanzador));
        assertTrue(lanzador.getCartera().getProductos().contains(prodReceptor));

        assertFalse(receptor.getCartera().getProductos().contains(prodReceptor));
        assertTrue(receptor.getCartera().getProductos().contains(prodLanzador));

        assertTrue(intercambio.getIntercambiado());
        assertNotNull(intercambio.getFechaAceptada());
    }
}