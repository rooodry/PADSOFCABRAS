package estadisticas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import compras.Pedido;
import excepciones.ExcepcionUsuariosAdmin;
import productos.ProductoTienda;
import productos.ProductoSegundaMano;
import productos.categoria.*;
import usuarios.Empleado;
import usuarios.Gestor;
import usuarios.ClienteRegistrado;
import utilidades.EstadoPedido;
import utilidades.EstadoConservacion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class EstadisticaTest {

    private Gestor gestor;
    private Empleado empleado;
    private ClienteRegistrado cliente1;
    private ClienteRegistrado cliente2;
    private ProductoTienda pComic;
    private ProductoTienda pFigura;

    @BeforeEach
    public void setUp() {
        gestor   = new Gestor("Jefe", "pass");
        empleado = new Empleado("EmpleadoTest", "pass");

        cliente1 = new ClienteRegistrado("Ana",  "pass1", "11111111A");
        cliente2 = new ClienteRegistrado("Luis", "pass2", "22222222B");

        pComic = new ProductoTienda("Watchmen", "Comic", "img1.png");
        pComic.setPrecio(15.0);
        pComic.setCategoria(new Comic("Watchmen", 300, "Moore", "DC", Genero.AVENTURA, 1986));

        pFigura = new ProductoTienda("Batman", "Figura", "img2.png");
        pFigura.setPrecio(5.0);
        pFigura.setCategoria(new Figura("Batman", 15.0, "McFarlane", "Plastico"));
    }

    // -------------------------------------------------------------------------
    // Constructor y getFichero
    // -------------------------------------------------------------------------

    @Test
    public void testFicheroAsignado() {
        Estadistica est = new Estadistica("estadisticas_prueba.txt");
        assertEquals("estadisticas_prueba.txt", est.getFichero());
    }

    @Test
    public void testFicheroAsignado_RutaCompleja() {
        Estadistica est = new Estadistica("/tmp/stats/output.txt");
        assertEquals("/tmp/stats/output.txt", est.getFichero());
    }

    // -------------------------------------------------------------------------
    // estadisticaPedidos
    // -------------------------------------------------------------------------

    @Test
    public void testEstadisticaPedidosVacia() throws IOException {
        File tempFile = File.createTempFile("stats_pedidos_vacia", ".txt");
        tempFile.deleteOnExit();

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        est.estadisticaPedidos(new ArrayList<>());

        String contenido = Files.readString(tempFile.toPath());
        assertTrue(contenido.contains("DNI USUARIO | PRODUCTOS"));
    }

    @Test
    public void testEstadisticaPedidos_ConPedidos() throws IOException {
        File tempFile = File.createTempFile("stats_pedidos_con_datos", ".txt");
        tempFile.deleteOnExit();

        Pedido pedido = new Pedido(cliente1, new HashMap<>(Map.of(pComic, 2)));
        pedido.setEstadoPedido(EstadoPedido.ENTREGADO);

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        est.estadisticaPedidos(List.of(pedido));

        String contenido = Files.readString(tempFile.toPath());
        assertTrue(contenido.contains("DNI USUARIO | PRODUCTOS"));
        assertTrue(contenido.contains(cliente1.getDNI()));
        assertTrue(contenido.contains("Watchmen"));
    }

    @Test
    public void testEstadisticaPedidos_VariosPedidos() throws IOException {
        File tempFile = File.createTempFile("stats_pedidos_varios", ".txt");
        tempFile.deleteOnExit();

        Pedido p1 = new Pedido(cliente1, new HashMap<>(Map.of(pComic,  1)));
        Pedido p2 = new Pedido(cliente2, new HashMap<>(Map.of(pFigura, 3)));
        p1.setEstadoPedido(EstadoPedido.ENTREGADO);
        p2.setEstadoPedido(EstadoPedido.EN_PREPARACION);

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        est.estadisticaPedidos(Arrays.asList(p1, p2));

        String contenido = Files.readString(tempFile.toPath());
        assertTrue(contenido.contains(cliente1.getDNI()));
        assertTrue(contenido.contains(cliente2.getDNI()));
    }


    @Test
    public void testEstadisticaRecaudacionMes_Excepcion_ConEmpleado() throws IOException {
        File tempFile = File.createTempFile("stats_recaudacion_mes_exc", ".txt");
        tempFile.deleteOnExit();

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());

        assertThrows(ExcepcionUsuariosAdmin.class, () ->
            est.estadisticaRecaudacionMes(empleado, new ArrayList<>(), new ArrayList<>())
        );
    }

    @Test
    public void testEstadisticaRecaudacionMes_ExitoConGestor() throws IOException {
        File tempFile = File.createTempFile("stats_recaudacion_mes_ok", ".txt");
        tempFile.deleteOnExit();

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());

        assertDoesNotThrow(() ->
            est.estadisticaRecaudacionMes(gestor, new ArrayList<>(), new ArrayList<>())
        );
    }

    @Test
    public void testEstadisticaRecaudacionMes_ConPedidosEntregados() throws IOException {
        File tempFile = File.createTempFile("stats_recaudacion_mes_datos", ".txt");
        tempFile.deleteOnExit();

        Pedido pedido = new Pedido(cliente1, new HashMap<>(Map.of(pComic, 2)));
        pedido.setEstadoPedido(EstadoPedido.ENTREGADO);

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        assertDoesNotThrow(() ->
            est.estadisticaRecaudacionMes(gestor, List.of(pedido), new ArrayList<>())
        );

        String contenido = Files.readString(tempFile.toPath());
        // Deben aparecer los 12 meses
        assertTrue(contenido.contains("ENERO"));
        assertTrue(contenido.contains("DICIEMBRE"));
    }

    @Test
    public void testEstadisticaRecaudacionMes_PedidoNoEntregado_NoContabiliza() throws IOException {
        File tempFile = File.createTempFile("stats_recaudacion_mes_noentregado", ".txt");
        tempFile.deleteOnExit();

        Pedido pedido = new Pedido(cliente1, new HashMap<>(Map.of(pComic, 1)));
        // Estado EN_CARRITO → no debe contabilizarse

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        assertDoesNotThrow(() ->
            est.estadisticaRecaudacionMes(gestor, List.of(pedido), new ArrayList<>())
        );

        String contenido = Files.readString(tempFile.toPath());
        // Todos los meses deberían tener 0.0
        assertTrue(contenido.contains("ENERO: 0.0"));
    }

    @Test
    public void testEstadisticaRecaudacionTipo_Excepcion_ConEmpleado() throws IOException {
        File tempFile = File.createTempFile("stats_recaudacion_tipo_exc", ".txt");
        tempFile.deleteOnExit();

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());

        assertThrows(ExcepcionUsuariosAdmin.class, () ->
            est.estadisticaRecaudacionTipo(empleado, new ArrayList<>(), new ArrayList<>())
        );
    }

    @Test
    public void testEstadisticaRecaudacionTipo_ExitoConGestor() throws IOException {
        File tempFile = File.createTempFile("stats_recaudacion_tipo_ok", ".txt");
        tempFile.deleteOnExit();

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());

        assertDoesNotThrow(() ->
            est.estadisticaRecaudacionTipo(gestor, new ArrayList<>(), new ArrayList<>())
        );
    }

    @Test
    public void testEstadisticaRecaudacionTipo_ConPedidosEntregados() throws IOException {
        File tempFile = File.createTempFile("stats_recaudacion_tipo_datos", ".txt");
        tempFile.deleteOnExit();

        Pedido pedido = new Pedido(cliente1, new HashMap<>(Map.of(pComic, 2))); // 30€
        pedido.setEstadoPedido(EstadoPedido.ENTREGADO);

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        assertDoesNotThrow(() ->
            est.estadisticaRecaudacionTipo(gestor, List.of(pedido), new ArrayList<>())
        );

        String contenido = Files.readString(tempFile.toPath());
        assertTrue(contenido.contains("RECAUDACIÓN POR VENTAS:"));
        assertTrue(contenido.contains("RECAUDACIÓN POR VALORACIONES:"));
        // El pedido vale 30€ y está entregado
        assertTrue(contenido.contains("30.0"));
    }

    @Test
    public void testEstadisticaRecaudacionTipo_ConClienteRegistrado_LanzaExcepcion() throws IOException {
        File tempFile = File.createTempFile("stats_recaudacion_tipo_cliente", ".txt");
        tempFile.deleteOnExit();

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());

        assertThrows(ExcepcionUsuariosAdmin.class, () ->
            est.estadisticaRecaudacionTipo(cliente1, new ArrayList<>(), new ArrayList<>())
        );
    }

    @Test
    public void testEstadisticaComprasVacia() throws IOException {
        File tempFile = File.createTempFile("stats_compras_vacia", ".txt");
        tempFile.deleteOnExit();

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        est.estadisticasUsuariosMayorActividadCompras(new ArrayList<>());

        String contenido = Files.readString(tempFile.toPath());
        assertTrue(contenido.contains("NÚMERO DE COMPRAS | DNI"));
    }

    @Test
    public void testEstadisticaCompras_ConClientes_EscribeCorrectamente() throws IOException {
        File tempFile = File.createTempFile("stats_compras_datos", ".txt");
        tempFile.deleteOnExit();

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        est.estadisticasUsuariosMayorActividadCompras(Arrays.asList(cliente1, cliente2));

        String contenido = Files.readString(tempFile.toPath());
        assertTrue(contenido.contains("NÚMERO DE COMPRAS | DNI"));
        assertTrue(contenido.contains(cliente1.getDNI()));
        assertTrue(contenido.contains(cliente2.getDNI()));
    }

    @Test
    public void testEstadisticaCompras_HeaderPresente() throws IOException {
        File tempFile = File.createTempFile("stats_compras_header", ".txt");
        tempFile.deleteOnExit();

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        est.estadisticasUsuariosMayorActividadCompras(List.of(cliente1));

        String contenido = Files.readString(tempFile.toPath());
        assertTrue(contenido.contains("CLIENTES Y NÚMERO DE COMPRAS (MAYOR A MENOR)"));
    }

    @Test
    public void testEstadisticaIntercambiosVacia() throws IOException {
        File tempFile = File.createTempFile("stats_intercambios_vacia", ".txt");
        tempFile.deleteOnExit();

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        est.estadisticasUsuariosMayorActividadIntercambios(new ArrayList<>());

        String contenido = Files.readString(tempFile.toPath());
        assertTrue(contenido.contains("DNI | INTERCAMBIOS TOTALES"));
    }

    @Test
    public void testEstadisticaIntercambios_ConClientes() throws IOException {
        File tempFile = File.createTempFile("stats_intercambios_datos", ".txt");
        tempFile.deleteOnExit();

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        est.estadisticasUsuariosMayorActividadIntercambios(Arrays.asList(cliente1, cliente2));

        String contenido = Files.readString(tempFile.toPath());
        assertTrue(contenido.contains("DNI | INTERCAMBIOS TOTALES"));
        assertTrue(contenido.contains(cliente1.getDNI()));
        assertTrue(contenido.contains(cliente2.getDNI()));
    }

    @Test
    public void testEstadisticaIntercambios_HeaderPresente() throws IOException {
        File tempFile = File.createTempFile("stats_intercambios_header", ".txt");
        tempFile.deleteOnExit();

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        est.estadisticasUsuariosMayorActividadIntercambios(List.of(cliente1));

        String contenido = Files.readString(tempFile.toPath());
        assertTrue(contenido.contains("CLIENTES Y NÚMERO DE INTERCAMBIOS (MAYOR A MENOR)"));
    }

    @Test
    public void testEstadisticaValoracion_VaciaEscribeHeader() throws IOException {
        File tempFile = File.createTempFile("stats_valoracion_vacia", ".txt");
        tempFile.deleteOnExit();

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        est.estadisticaCompraUsuarioValoracion(new ArrayList<>());

        String contenido = Files.readString(tempFile.toPath());
        assertTrue(contenido.contains("NOMBRE PRODUCTO | DNI USUARIO | VALORACION"));
    }

    @Test
    public void testEstadisticaValoracion_ConPedidoYValoracion() throws IOException {
        File tempFile = File.createTempFile("stats_valoracion_datos", ".txt");
        tempFile.deleteOnExit();

        Pedido pedido = new Pedido(cliente1, new HashMap<>(Map.of(pComic, 1)));
        pedido.setValoracionesProductos(new HashMap<>(Map.of(pComic, 9)));

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        est.estadisticaCompraUsuarioValoracion(List.of(pedido));

        String contenido = Files.readString(tempFile.toPath());
        assertTrue(contenido.contains("Watchmen"));
        assertTrue(contenido.contains("9"));
    }

    @Test
    public void testEstadisticaValoracionPorUsuario_EscribeHeader() throws IOException {
        File tempFile = File.createTempFile("stats_valoracion_usuario", ".txt");
        tempFile.deleteOnExit();

        Estadistica est = new Estadistica(tempFile.getAbsolutePath());
        est.estadisticaCompraUsuarioValoracionPorUsuario(cliente1);

        String contenido = Files.readString(tempFile.toPath());
        assertTrue(contenido.contains("NOMBRE PRODUCTO | DNI USUARIO | VALORACION | CATEGORÍA | SUBCATEGORÍAS"));
    }
}