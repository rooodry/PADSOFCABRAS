package intercambios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import intercambios.Oferta;
import productos.ProductoSegundaMano;
import usuarios.ClienteRegistrado;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import utilidades.EstadoOferta;

public class OfertaTest {

    @Test
    public void testConstructorYGetters() {
        private ClienteRegistrado lanzador;
        private ClienteRegistrado receptor;
        private ProductoSegundaMano pOfertado;
        private ProductoSegundaMano pDeseado;
        private Oferta oferta;

        @BeforeEach
        public void setUp() {
            ClienteRegistrado lanzador = new ClienteRegistrado("usuario1", "pass", "12345678A");
            ClienteRegistrado receptor = new ClienteRegistrado("usuario2", "pass", "87654321B");
            ProductoSegundaMano pOfertado = new ProductoSegundaMano("Comic", "Usado", "img.jpg", lanzador);
            ProductoSegundaMano pDeseado = new ProductoSegundaMano("Figura", "Nueva", "img2.jpg", receptor);

            ofertaReal = new Oferta(pOfertado, pDeseado, receptor, lanzador);

        }
        
    @Test
    public void testConstructorYGetters() {
        assertEquals(EstadoOferta.PENDIENTE, oferta.getEstadoOferta(), "La oferta debe empezar como PENDIENTE");
        assertEquals(oferta.getProductoOfertado());
        assertEquals(oferta.getProductoDeseado());
        assertEquals(oferta.getUsuarioReceptor());
        assertEquals(oferta.getUsuarioLanzador());
    }

    @Test
    public void testSetEstadoOferta() {
        oferta.setEstadoOferta(EstadoOferta.ACEPTADA);
        assertEquals(EstadoOferta.ACEPTADA, oferta.getEstadoOferta());
    }
}