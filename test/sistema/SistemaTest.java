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
import utilidades.EstadoPedido;
import descuentos.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SistemaTest {

    private Sistema sistema;
    private Gestor admin;
    private ClienteRegistrado clienteNormal;
    private Empleado empleado;
    private Stock stock;
    private ProductoTienda pComic;
    private ProductoTienda pFigura;
    private ProductoTienda pJuego;

    private final String FILE_PROD_IN  = "test_prod_in.csv";
    private final String FILE_PROD_OUT = "test_prod_out.csv";
    private final String FILE_USER_IN  = "test_user_in.csv";
    private final String FILE_USER_OUT = "test_user_out.csv";

    @BeforeEach
    public void setUp() {
        sistema = new Sistema();
        admin   = new Gestor("Jefe", "adminPass");
        clienteNormal = new ClienteRegistrado("Paco", "pass", "12345678Z");
        empleado = new Empleado("Empleado1", "emp123");
        empleado.addPermiso(TiposEmpleado.EMPLEADOS_PRODUCTO);

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

        stock.añadirProducto(pComic,  10);
        stock.añadirProducto(pFigura, 50);
        stock.añadirProducto(pJuego,   5);

        sistema.addProducto(pComic);
        sistema.addProducto(pFigura);
        sistema.addProducto(pJuego);

        sistema.addUsuario(admin);
        sistema.addUsuario(clienteNormal);
        sistema.addUsuario(empleado);
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
    public void testDarAltaEmpleado_ComoEmpleado_LanzaExcepcion() {
        assertThrows(ExcepcionUsuariosAdmin.class, () -> {
            sistema.darAltaEmpleado(empleado, "Intruso2", "1234", TiposEmpleado.EMPLEADOS_PRODUCTO);
        });
    }

    @Test
    public void testDarBajaEmpleado_ComoAdmin() {
        assertDoesNotThrow(() -> {
            sistema.darBajaEmpleado(admin, empleado);
        });
    }

    @Test
    public void testDarBajaEmpleado_ComoNoAdmin_LanzaExcepcion() {
        assertThrows(ExcepcionUsuariosAdmin.class, () -> {
            sistema.darBajaEmpleado(clienteNormal, empleado);
        });
    }

    @Test
    public void testModificarPermisos_ComoAdmin() {
        Set<TiposEmpleado> nuevosPermisos = new HashSet<>(Arrays.asList(
                TiposEmpleado.EMPLEADOS_INTERCAMBIO, TiposEmpleado.EMPLEADOS_PEDIDO));
        assertDoesNotThrow(() -> {
            sistema.modificarPermisos(admin, empleado, nuevosPermisos);
        });
    }

    @Test
    public void testModificarPermisos_ComoNoAdmin_LanzaExcepcion() {
        Set<TiposEmpleado> permisos = new HashSet<>(Arrays.asList(TiposEmpleado.EMPLEADOS_PRODUCTO));
        assertThrows(ExcepcionUsuariosAdmin.class, () -> {
            sistema.modificarPermisos(clienteNormal, empleado, permisos);
        });
    }

    @Test
    public void testCrearPack_ComoAdmin() {
        assertDoesNotThrow(() -> {
            var pack = sistema.crearPack(admin, "PackTest", 50.0, Arrays.asList(pComic, pFigura));
            assertNotNull(pack);
            assertEquals("PackTest", pack.getNombre());
        });
    }

    @Test
    public void testCrearPack_ComoNoAdmin_LanzaExcepcion() {
        assertThrows(ExcepcionUsuariosAdmin.class, () -> {
            sistema.crearPack(clienteNormal, "PackMalo", 50.0, Arrays.asList(pComic));
        });
    }

    @Test
    public void testActualizarStock_SumarUnidades() {
        assertDoesNotThrow(() -> {
            sistema.actualizarStock(admin, pComic, 5);
            assertEquals(15, stock.getNumProductos(pComic));
        });
    }

    @Test
    public void testActualizarStock_RestarUnidades() {
        assertDoesNotThrow(() -> {
            sistema.actualizarStock(admin, pComic, -2);
            assertEquals(8, stock.getNumProductos(pComic));
        });
    }

    @Test
    public void testActualizarStock_RetirarConCero() {
        assertDoesNotThrow(() -> {
            sistema.actualizarStock(admin, pFigura, 0);
            assertEquals(0, stock.getNumProductos(pFigura));
        });
    }

    @Test
    public void testActualizarStock_ComoNoAdmin_LanzaExcepcion() {
        assertThrows(ExcepcionUsuariosAdmin.class, () -> {
            sistema.actualizarStock(clienteNormal, pComic, 5);
        });
    }

    @Test
    public void testRegistrarPedidoConRegalo_CuandoTotalSuperaDoscientos() {
        pJuego.setPrecio(210.0);
        Pedido p = new Pedido(clienteNormal, new HashMap<>(Map.of(pJuego, 1)));
        sistema.registrarPedido(p);
        assertNotNull(p.getRegalo());
        assertTrue(p.getRegalo().getPrecio() <= 15.0);
    }

    @Test
    public void testRegistrarPedidoSinRegalo_CuandoTotalMenorDoscientos() {
        Pedido p = new Pedido(clienteNormal, new HashMap<>(Map.of(pComic, 1)));
        sistema.registrarPedido(p);
        assertNull(p.getRegalo());
    }

    @Test
    public void testRegistrarPedido_SeAnhadeAlListado() {
        Pedido p = new Pedido(clienteNormal, new HashMap<>(Map.of(pComic, 1)));
        sistema.addPedido(p);
        sistema.registrarPedido(p);
        assertEquals(EstadoPedido.EN_CARRITO, p.getEstadoPedido());
    }

    @Test
    public void testCancelarPedido_DevuelveStockCorrectamente() {
        int stockAntes = stock.getNumProductos(pComic);
        Pedido p = new Pedido(clienteNormal, new HashMap<>(Map.of(pComic, 3)));
        sistema.registrarPedido(p);
        sistema.cancelarPedido(p);
        assertEquals(stockAntes + 3, stock.getNumProductos(pComic));
    }

    @Test
    public void testCancelarPedido_EstadoCancelado() {
        Pedido p = new Pedido(clienteNormal, new HashMap<>(Map.of(pFigura, 1)));
        sistema.registrarPedido(p);
        sistema.cancelarPedido(p);
        assertEquals(EstadoPedido.CANCELADO, p.getEstadoPedido());
    }

    @Test
    public void testCalcularPrecioFinalPedido_SinDescuentos() {
        Pedido p = new Pedido(clienteNormal, new HashMap<>(Map.of(pComic, 2)));
        double precio = sistema.calcularPrecioFinalPedido(p);
        assertEquals(30.0, precio, 0.001);
    }

    @Test
    public void testCalcularPrecioFinalPedido_ConDescuentoPorcentaje() {
        Date inicio = new Date(System.currentTimeMillis() - 10000);
        Date fin    = new Date(System.currentTimeMillis() + 100000);
        Descuento desc = new DescuentoPorcentaje(inicio, fin, 10.0);
        sistema.addDescuento(desc);

        Pedido p = new Pedido(clienteNormal, new HashMap<>(Map.of(pComic, 2)));
        double precio = sistema.calcularPrecioFinalPedido(p);
        assertEquals(27.0, precio, 0.001);
    }

    @Test
    public void testRecomendarProductosBasadoEnIntereses() {
        Map<String, Integer> intereses = new HashMap<>();
        intereses.put("AVENTURA", 10);
        intereses.put("FIGURA",    5);

        List<ProductoTienda> candidatos = Arrays.asList(pFigura, pComic);
        List<ProductoTienda> recomendados = sistema.recomendarProductos(intereses, candidatos);

        assertEquals(pComic, recomendados.get(0));
    }

    @Test
    public void testRecomendarProductos_FiguraPriorizadaSiInteresAlto() {
        Map<String, Integer> intereses = new HashMap<>();
        intereses.put("FIGURA", 20);
        intereses.put("AVENTURA", 2);

        List<ProductoTienda> candidatos = Arrays.asList(pComic, pFigura);
        List<ProductoTienda> recomendados = sistema.recomendarProductos(intereses, candidatos);

        assertEquals(pFigura, recomendados.get(0));
    }

    @Test
    public void testRecomendarProductos_JuegoPriorizadoCorrectamente() {
        Map<String, Integer> intereses = new HashMap<>();
        intereses.put("JUEGO_MESA", 15);
        intereses.put("AVENTURA", 1);

        List<ProductoTienda> candidatos = Arrays.asList(pComic, pJuego);
        List<ProductoTienda> recomendados = sistema.recomendarProductos(intereses, candidatos);

        assertEquals(pJuego, recomendados.get(0));
    }

    @Test
    public void testRecomendarProductos_ListaVacia() {
        Map<String, Integer> intereses = new HashMap<>();
        intereses.put("AVENTURA", 10);

        List<ProductoTienda> recomendados = sistema.recomendarProductos(intereses, new ArrayList<>());
        assertTrue(recomendados.isEmpty());
    }

    @Test
    public void testFiltrarPorCategoria_Comics() {
        List<Producto> catalogo = Arrays.asList(pComic, pFigura, pJuego);
        List<Producto> comics = sistema.filtrarPorCategoria(catalogo, "COMIC");
        assertEquals(1, comics.size());
        assertEquals(pComic, comics.get(0));
    }

    @Test
    public void testFiltrarPorCategoria_Figuras() {
        List<Producto> catalogo = Arrays.asList(pComic, pFigura, pJuego);
        List<Producto> figuras = sistema.filtrarPorCategoria(catalogo, "FIGURA");
        assertEquals(1, figuras.size());
        assertEquals(pFigura, figuras.get(0));
    }

    @Test
    public void testFiltrarPorCategoria_Juegos() {
        List<Producto> catalogo = Arrays.asList(pComic, pFigura, pJuego);
        List<Producto> juegos = sistema.filtrarPorCategoria(catalogo, "JUEGO");
        assertEquals(1, juegos.size());
        assertEquals(pJuego, juegos.get(0));
    }

    @Test
    public void testFiltrarPorCategoria_CategoriaInexistente_DevuelveVacio() {
        List<Producto> catalogo = Arrays.asList(pComic, pFigura, pJuego);
        List<Producto> resultado = sistema.filtrarPorCategoria(catalogo, "DESCONOCIDA");
        assertTrue(resultado.isEmpty());
    }

    @Test
    public void testFiltrarPorValoracion_SuperiorA8() {
        List<Producto> catalogo = Arrays.asList(pComic, pFigura, pJuego);
        List<Producto> resultado = sistema.filtrarPorValoracion(catalogo, 8);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(pComic));
        assertTrue(resultado.contains(pJuego));
    }

    @Test
    public void testFiltrarPorValoracion_TodosIncluidos() {
        List<Producto> catalogo = Arrays.asList(pComic, pFigura, pJuego);
        List<Producto> resultado = sistema.filtrarPorValoracion(catalogo, 1);
        assertEquals(3, resultado.size());
    }

    @Test
    public void testFiltrarPorValoracion_NingunoCumple() {
        List<Producto> catalogo = Arrays.asList(pComic, pFigura, pJuego);
        List<Producto> resultado = sistema.filtrarPorValoracion(catalogo, 11);
        assertTrue(resultado.isEmpty());
    }

    @Test
    public void testFiltrarPorPrecio_MayoresOIgualesA15() {
        List<ProductoTienda> catalogo = Arrays.asList(pComic, pFigura, pJuego);
        List<ProductoTienda> resultado = sistema.filtrarPorPrecio(catalogo, 15.0);
        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(pComic));
        assertTrue(resultado.contains(pJuego));
    }

    @Test
    public void testFiltrarPorPrecio_NingunoCumple() {
        List<ProductoTienda> catalogo = Arrays.asList(pComic, pFigura, pJuego);
        List<ProductoTienda> resultado = sistema.filtrarPorPrecio(catalogo, 100.0);
        assertTrue(resultado.isEmpty());
    }

    @Test
    public void testOrdenarAlfabeticoAscendente() {
        List<Producto> catalogo = new ArrayList<>(Arrays.asList(pJuego, pComic, pFigura));
        List<Producto> ordenado = sistema.ordenarPorOrdenAlfabetico(catalogo, true);
        assertEquals("Batman",   ordenado.get(0).getNombre());
        assertEquals("Catan",    ordenado.get(1).getNombre());
        assertEquals("Watchmen", ordenado.get(2).getNombre());
    }

    @Test
    public void testOrdenarAlfabeticoDescendente() {
        List<Producto> catalogo = new ArrayList<>(Arrays.asList(pJuego, pComic, pFigura));
        List<Producto> ordenado = sistema.ordenarPorOrdenAlfabetico(catalogo, false);
        assertEquals("Watchmen", ordenado.get(0).getNombre());
        assertEquals("Catan",    ordenado.get(1).getNombre());
        assertEquals("Batman",   ordenado.get(2).getNombre());
    }

    @Test
    public void testOrdenarPorPrecioAscendente() {
        List<ProductoTienda> catalogo = new ArrayList<>(Arrays.asList(pJuego, pComic, pFigura));
        List<ProductoTienda> ordenado = sistema.ordenarPorPrecio(catalogo, true);
        assertEquals("Batman",   ordenado.get(0).getNombre());
        assertEquals("Watchmen", ordenado.get(1).getNombre());
        assertEquals("Catan",    ordenado.get(2).getNombre());
    }

    @Test
    public void testOrdenarPorPrecioDescendente() {
        List<ProductoTienda> catalogo = new ArrayList<>(Arrays.asList(pJuego, pComic, pFigura));
        List<ProductoTienda> ordenado = sistema.ordenarPorPrecio(catalogo, false);
        assertEquals("Catan",    ordenado.get(0).getNombre());
        assertEquals("Watchmen", ordenado.get(1).getNombre());
        assertEquals("Batman",   ordenado.get(2).getNombre());
    }

    @Test
    public void testOrdenarPorValoracionAscendente() {
        List<Producto> catalogo = new ArrayList<>(Arrays.asList(pJuego, pComic, pFigura));
        List<Producto> ordenado = sistema.ordenarPorValoracion(catalogo, true);
        assertEquals("Batman",   ordenado.get(0).getNombre());
        assertEquals("Watchmen", ordenado.get(1).getNombre());
        assertEquals("Catan",    ordenado.get(2).getNombre());
    }

    @Test
    public void testOrdenarPorValoracionDescendente() {
        List<Producto> catalogo = new ArrayList<>(Arrays.asList(pJuego, pComic, pFigura));
        List<Producto> ordenado = sistema.ordenarPorValoracion(catalogo, false);
        assertEquals("Catan",    ordenado.get(0).getNombre());
        assertEquals("Watchmen", ordenado.get(1).getNombre());
        assertEquals("Batman",   ordenado.get(2).getNombre());
    }

    @Test
    public void testSetEstadoPedido_EnPreparacion() {
        Pedido p = new Pedido(clienteNormal, new HashMap<>(Map.of(pComic, 1)));
        sistema.setEstadoPedido(p, EstadoPedido.EN_PREPARACION);
        assertEquals(EstadoPedido.EN_PREPARACION, p.getEstadoPedido());
        assertNotNull(p.getFechaPago());
    }

    @Test
    public void testSetEstadoPedido_Entregado() {
        Pedido p = new Pedido(clienteNormal, new HashMap<>(Map.of(pComic, 1)));
        sistema.setEstadoPedido(p, EstadoPedido.ENTREGADO);
        assertEquals(EstadoPedido.ENTREGADO, p.getEstadoPedido());
    }

    @Test
    public void testGenerarCodigo_NoNulo() {
        var codigo = sistema.generarCodigo();
        assertNotNull(codigo);
    }

    @Test
    public void testGenerarCodigo_CodigosDistintos() {
        var c1 = sistema.generarCodigo();
        var c2 = sistema.generarCodigo();
        assertNotEquals(c1, c2);
    }

    @Test
    public void testEnviarCodigo_ClienteLoRecibe() {
        var codigo = sistema.generarCodigo();
        sistema.enviarCodigo(clienteNormal, codigo);
        assertTrue(clienteNormal.getCodigos().contains(codigo));
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
        assertTrue(out.length() > 0);
    }

    @Test
    public void testCargaProductos_ArchivoInexistente_NoLanzaExcepcion() {
        assertDoesNotThrow(() -> {
            sistema.cargaProductos("archivo_que_no_existe.csv");
        });
    }

    @Test
    public void testCargaProductos_TipoFigura() throws IOException {
        FileWriter writer = new FileWriter(FILE_PROD_IN);
        writer.write("F;ID2;IronMan;Figura metal;20.0;5;N/A;0;N/A;N/A;N/A;0;0;N/A;Hasbro;Metal;20.5\n");
        writer.close();

        int productosAntes = stock.getProductos().size();
        sistema.cargaProductos(FILE_PROD_IN);
        assertTrue(stock.getProductos().size() > productosAntes);
    }

    @Test
    public void testCargaProductos_TipoJuego() throws IOException {
        FileWriter writer = new FileWriter(FILE_PROD_IN);
        writer.write("J;ID3;Poker;Juego cartas;15.0;3;N/A;0;N/A;N/A;N/A;6;12;Cartas;N/A;N/A;0\n");
        writer.close();

        int productosAntes = stock.getProductos().size();
        sistema.cargaProductos(FILE_PROD_IN);
        assertTrue(stock.getProductos().size() > productosAntes);
    }

    @Test
    public void testCargaYDescargaUsuarios() throws IOException {
        FileWriter writer = new FileWriter(FILE_USER_IN);
        writer.write("C;Maria;pass123;87654321A\n");
        writer.write("G;Boss;secret\n");
        writer.write("E;Worker;emp456;;Producto\n");
        writer.close();

        sistema.cargaUsuarios(FILE_USER_IN);
        sistema.descargaUsuarios(FILE_USER_OUT);

        File out = new File(FILE_USER_OUT);
        assertTrue(out.exists());
        assertTrue(out.length() > 0);
    }

    @Test
    public void testCargaUsuarios_ArchivoInexistente_NoLanzaExcepcion() {
        assertDoesNotThrow(() -> {
            sistema.cargaUsuarios("no_existe.csv");
        });
    }
}