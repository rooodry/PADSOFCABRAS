package intercambios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import productos.ProductoSegundaMano;
import usuarios.ClienteRegistrado;

import static org.junit.jupiter.api.Assertions.*;

import utilidades.EstadoOferta;

public class OfertaTest {
    private ClienteRegistrado lanzador;
    private ClienteRegistrado receptor;
    private ProductoSegundaMano pOfertado;
    private ProductoSegundaMano pDeseado;
    private Oferta oferta;

    @BeforeEach
    public void setUp() {
        lanzador = new ClienteRegistrado("usuario1", "pass", "12345678A");
        receptor = new ClienteRegistrado("usuario2", "pass", "87654321B");
        pOfertado = new ProductoSegundaMano("Comic", "Usado", "img.jpg", lanzador);
        pDeseado = new ProductoSegundaMano("Figura", "Nueva", "img2.jpg", receptor);

        oferta = new Oferta(pOfertado, pDeseado, receptor, lanzador);
    }
        
    @Test
    public void testConstructorYGetters() {
        assertEquals(EstadoOferta.PENDIENTE, oferta.getEstadoOferta(), "La oferta debe empezar como PENDIENTE");
        
        assertEquals(pOfertado, oferta.getProductoOfertado());
        assertEquals(pDeseado, oferta.getProductoDeseado());
        assertEquals(receptor, oferta.getUsuarioReceptor());
        assertEquals(lanzador, oferta.getUsuarioLanzador());
    }

    @Test
    public void testSetEstadoOferta() {
        oferta.setEstadoOferta(EstadoOferta.ACEPTADA);
        assertEquals(EstadoOferta.ACEPTADA, oferta.getEstadoOferta());
    }
}