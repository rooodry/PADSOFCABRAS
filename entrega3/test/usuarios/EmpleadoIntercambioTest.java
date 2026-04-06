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

        // Añadimos los productos a sus respectivas carteras (estado inicial)
        lanzador.subirProducto(prodLanzador);
        receptor.subirProducto(prodReceptor);

        // El lanzador ofrece su consola a cambio del juego del receptor
        oferta = new Oferta(prodLanzador, prodReceptor, receptor, lanzador);
        intercambio = new Intercambio(new Date(), oferta);
    }

    @Test
    public void testTransferirPropiedad() {
        // Verificamos estado antes de la transferencia
        assertTrue(lanzador.getCartera().getProductos().contains(prodLanzador));
        assertTrue(receptor.getCartera().getProductos().contains(prodReceptor));

        // Realizamos la transferencia
        empleado.transferirPropiedad(intercambio);

        // Verificamos que los productos han cambiado de dueño en las carteras
        assertFalse(lanzador.getCartera().getProductos().contains(prodLanzador), "El lanzador ya no debe tener su consola.");
        assertTrue(lanzador.getCartera().getProductos().contains(prodReceptor), "El lanzador ahora debe tener el juego.");

        assertFalse(receptor.getCartera().getProductos().contains(prodReceptor), "El receptor ya no debe tener su juego.");
        assertTrue(receptor.getCartera().getProductos().contains(prodLanzador), "El receptor ahora debe tener la consola.");

        // Verificamos el estado del intercambio
        assertTrue(intercambio.getIntercambiado(), "El intercambio debe marcarse como true.");
        assertNotNull(intercambio.getFechaAceptada(), "La fecha de aceptación debe registrarse.");
    }
}