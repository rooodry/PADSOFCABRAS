package sistema;

import compras.Pedido;
import excepciones.ExcepcionUsuariosAdmin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productos.Producto;
import productos.ProductoTienda;
import productos.Stock;
import productos.categoria.*;
import usuarios.*;
import utilidades.TiposEmpleado;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SistemaTest {

    private Sistema sistema;
    private Gestor admin;
    private ClienteRegistrado clienteNormal;
    private Stock stock;
    private ProductoTienda pComic;
    private ProductoTienda pFigura;
    private ProductoTienda pJuego;

    private final String FILE_PROD_IN = "test_prod_in.csv";
    private final String FILE_PROD_OUT = "test_prod_out.csv";
    private final String FILE_USER_IN = "test_user_in.csv";
    private final String FILE_USER_OUT = "test_user_out.csv";

    @BeforeEach
    public void setUp() {
        sistema = new Sistema();
        admin = new Gestor("Jefe", "adminPass");
        clienteNormal = new ClienteRegistrado("Paco", "pass", "12345678Z");
        
        stock = new Stock();
        sistema.setStock(stock);

        pComic = new ProductoTienda("Watchmen", "Comic clásico", "img1.png");
        pComic.setPrecio(15.0);
        pComic.setValoracion(8);
        pComic.setCategoria(new Comic("Watchmen", 300, "Alan Moore", "DC", Genero.AVENTURA, 1986));

        pFigura = new ProductoTienda("Batman", "Figura articulada", "img2.png");
        pFigura.setPrecio(5.0);
        pFigura.setValoracion(5);
        pFigura.setCategoria(new Figura("Batman", 15.0, "McFarlane", "Plastico"));

        pJuego = new ProductoTienda("Catan", "Juego de mesa", "img3.png");
        pJuego.setPrecio(40.0);
        pJuego.setValoracion(10);
        pJuego.setCategoria(new Juego("Catan", 4, 10, TipoJuego.JUEGO_MESA));

        stock.añadirProducto(pComic, 10);
        stock.añadirProducto(pFigura, 50);
        stock.añadirProducto(pJuego, 5);
        
        sistema.addProducto(pComic);
        sistema.addProducto(pFigura);
        sistema.addProducto(pJuego);
        
        sistema.addUsuario(admin);
        sistema.addUsuario(clienteNormal);
    }

    @AfterEach
    public void tearDown() {
        new File(FILE_PROD_IN).delete();
        new File(FILE_PROD_OUT).delete();
        new File(FILE_USER_IN).delete();
        new File(FILE_USER_OUT).delete();
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
    public void testActualizarStock() {
        assertDoesNotThrow(() -> {
            sistema.actualizarStock(admin, pComic, 5);
            assertEquals(15, stock.getNumProductos(pComic));
            
            sistema.actualizarStock(admin, pComic, -2);
            assertEquals(13, stock.getNumProductos(pComic));
            
            sistema.actualizarStock(admin, pFigura, 0);
            assertEquals(0, stock.getNumProductos(pFigura));
        });
    }

    @Test
    public void testRegistrarPedidoConRegalo() {
        pJuego.setPrecio(210.0);
        Pedido p = new Pedido(clienteNormal, new HashMap<>(Map.of(pJuego, 1)));
        sistema.registrarPedido(p);
        assertNotNull(p.getRegalo());
        assertTrue(p.getRegalo().getPrecio() <= 15.0);
    }

    @Test
    public void testRecomendarProductosBasadoEnIntereses() {
        Map<String, Integer> intereses = new HashMap<>();
        intereses.put("AVENTURA", 10);
        intereses.put("FIGURA", 5);
        
        List<ProductoTienda> candidatos = Arrays.asList(pFigura, pComic);
        List<ProductoTienda> recomendados = sistema.recomendarProductos(intereses, candidatos);
        
        assertEquals(pComic, recomendados.get(0));
    }

    @Test
    public void testFiltrarPorCategoria() {
        List<Producto> catalogo = Arrays.asList(pComic, pFigura, pJuego);
        List<Producto> comics = sistema.filtrarPorCategoria(catalogo, "COMIC");
        assertEquals(1, comics.size());
        assertEquals(pComic, comics.get(0));
    }

    @Test
    public void testCargaYDescargaProductos() throws IOException {
        FileWriter writer = new FileWriter(FILE_PROD_IN);
        writer.write("C;ID1;Spiderman;Un comic;12.0;10;Aventura;150;Stan Lee;Marvel;1962;0;0;N/A;N/A;N/A;0\n");
        writer.close();

        sistema.cargaProductos(FILE_PROD_IN);
        sistema.descargarProductos(FILE_PROD_OUT);
        
        File out = new File(FILE_PROD_OUT);
        assertTrue(out.exists());
    }
}