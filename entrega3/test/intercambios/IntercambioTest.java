package intercambios;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import productos.ProductoSegundaMano;
import usuarios.ClienteRegistrado;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import utilidades.EstadoOferta;

public class IntercambioTest {
    private Oferta oferta;
    private Intercambio intercambio;

    @BeforeEach
    public void setUp() {
        ClienteRegistrado lanzador = new ClienteRegistrado("usuario1", "pass", "12345678A");
        ClienteRegistrado receptor = new ClienteRegistrado("usuario2", "pass", "87654321B");
        ProductoSegundaMano pOfertado = new ProductoSegundaMano("Comic", "Usado", "img.jpg", lanzador);
        ProductoSegundaMano pDeseado = new ProductoSegundaMano("Figura", "Nueva", "img2.jpg", receptor);

        oferta = new Oferta(pOfertado, pDeseado, receptor, lanzador);
        intercambio = new Intercambio(new Date(), oferta);
    }

    @Test
    public void testConstructorYFechas() {
        assertNotNull(intercambio.getFechaOferta());
        assertNotNull(intercambio.getFechaLimite());
        assertNull(intercambio.getFechaAceptada());
        assertFalse(intercambio.getIntercambiado());
        
        assertTrue(intercambio.getFechaLimite().after(intercambio.getFechaOferta()));
    }

    @Test
    public void testAceptarOferta() {
        intercambio.aceptarOferta();
        assertEquals(EstadoOferta.ACEPTADA, oferta.getEstadoOferta());
        assertNotNull(intercambio.getFechaAceptada());
    }

    @Test
    public void testRechazarOferta() {
        intercambio.rechazarOferta();
        assertEquals(EstadoOferta.RECHAZADA, oferta.getEstadoOferta());
        assertNull(intercambio.getFechaAceptada());
    }
}