package usuarios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productos.ProductoSegundaMano;
import productos.ProductoTienda;
import productos.Stock;
import utilidades.Status;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteRegistradoTest {

    private ClienteRegistrado cliente;
    private ProductoTienda productoTienda;
    private ProductoSegundaMano productoSM;
    private Stock stock;

    @BeforeEach
    public void setUp() {
        cliente = new ClienteRegistrado("user123", "pass", "12345678A");
        productoTienda = new ProductoTienda("Camiseta", "Talla M", "img.jpg");
        productoSM = new ProductoSegundaMano("Bici", "Vieja", "bici.jpg", cliente);
        
        stock = new Stock();
        stock.añadirProducto(productoTienda, 10);
    }

    @Test
    public void testComprarCestaVacia() {
        // Si la cesta está vacía, comprar() debe devolver ERROR
        assertEquals(Status.ERROR, cliente.comprar(), "No se debería poder comprar con la cesta vacía.");
    }

    @Test
    public void testAñadirACestaYComprar() {
        // El cliente añade un producto a la cesta (asumimos que la lógica interna reduce el stock o lo gestiona)
        cliente.añadirALaCesta(productoTienda, stock);
        
        // Al comprar, debe devolver OK y la cesta debe vaciarse
        assertEquals(Status.OK, cliente.comprar(), "La compra debería ser exitosa.");
        // Nota: Como 'cesta' es privado y no tiene getter directo en Cliente, 
        // lo deducimos porque si intentamos comprar de nuevo, debería dar ERROR (cesta vacía tras comprar)
        assertEquals(Status.ERROR, cliente.comprar(), "La cesta debería haberse vaciado tras la primera compra.");
    }

    @Test
    public void testSubirYPagarValoracion() {
        // Subimos un producto de segunda mano a la cartera
        assertEquals(Status.OK, cliente.subirProducto(productoSM));
        
        // Pagamos la valoración (el producto debería cambiar a PENDIENTE_DE_VALORAR internamente)
        assertEquals(Status.OK, cliente.pagarValoracion(productoSM));
        assertEquals(utilidades.EstadoProducto.PENDIENTE_DE_VALORAR, productoSM.getEstadoProducto());
    }
}