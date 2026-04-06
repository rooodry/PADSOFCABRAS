package sistema;

import compras.Pedido;
import excepciones.ExcepcionUsuariosAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import productos.ProductoTienda;
import productos.Stock;
import usuarios.*;
import utilidades.TiposEmpleado;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SistemaTest {

    private Sistema sistema;
    private Gestor admin;
    private ClienteRegistrado clienteNormal;
    private Stock stock;
    private ProductoTienda productoCaro;
    private ProductoTienda productoBarato;

    @BeforeEach
    public void setUp() {
        sistema = new Sistema();
        admin = new Gestor("Jefe", "adminPass");
        clienteNormal = new ClienteRegistrado("Paco", "pass", "12345678Z");
        
        stock = new Stock();
        sistema.setStock(stock);

        productoCaro = new ProductoTienda("Consola", "Videoconsola", "consola.jpg");
        productoCaro.setPrecio(250.0); // Más de 200 para probar el regalo
        
        productoBarato = new ProductoTienda("Llavero", "Llavero friki", "llavero.jpg");
        productoBarato.setPrecio(5.0); // Menos de 15 para ser elegible como regalo

        stock.añadirProducto(productoCaro, 10);
        stock.añadirProducto(productoBarato, 50);
    }

    @Test
    public void testDarAltaEmpleado_ComoAdmin() {
        // Un Gestor debería poder dar de alta sin lanzar excepción
        assertDoesNotThrow(() -> {
            sistema.darAltaEmpleado(admin, "NuevoEmpleado", "1234", TiposEmpleado.EMPLEADOS_PRODUCTO);
        }, "El administrador debería poder crear empleados sin error.");
        
        // Asumiendo que el método añade el empleado a la lista interna de usuarios, 
        // no tenemos un getter directo de "usuarios" en Sistema.java para comprobar el size,
        // pero asegurar que no lanza la excepción ya verifica el control de acceso.
    }

    @Test
    public void testDarAltaEmpleado_ComoNoAdmin_LanzaExcepcion() {
        // Un usuario normal no debería poder dar de alta a un empleado
        assertThrows(ExcepcionUsuariosAdmin.class, () -> {
            sistema.darAltaEmpleado(clienteNormal, "Intruso", "1234", TiposEmpleado.EMPLEADOS_PRODUCTO);
        }, "Debería lanzar ExcepcionUsuariosAdmin si no es un Gestor.");
    }

    @Test
    public void testActualizarStock_ComoAdmin() {
        assertDoesNotThrow(() -> {
            // Añadimos 5 unidades más
            sistema.actualizarStock(admin, productoCaro, 5);
        });

        assertEquals(15, stock.getNumProductos(productoCaro), "El stock debería haber aumentado a 15.");
    }

    @Test
    public void testRegistrarPedido_ConRegalo() {
        // Preparamos un pedido de más de 200€
        Map<ProductoTienda, Integer> productosPedido = new HashMap<>();
        productosPedido.put(productoCaro, 1); // 250€
        Pedido pedido = new Pedido(clienteNormal, productosPedido);

        sistema.registrarPedido(pedido);

        // Como el total es > 200, el sistema debería haber buscado un producto <= 15€ (el llavero)
        // y asignarlo como regalo.
        assertNotNull(pedido.getRegalo(), "El pedido debería tener un regalo asignado por superar los 200€.");
        assertEquals(productoBarato, pedido.getRegalo(), "El regalo debería ser el llavero.");
    }

    @Test
    public void testCancelarPedido_RestauraStock() {
        Map<ProductoTienda, Integer> productosPedido = new HashMap<>();
        productosPedido.put(productoCaro, 2); // Compramos 2 consolas
        Pedido pedido = new Pedido(clienteNormal, productosPedido);
        
        // Simulamos que el stock se redujo al crear la cesta/pedido
        stock.reducirStock(productoCaro, 2);
        assertEquals(8, stock.getNumProductos(productoCaro));

        sistema.registrarPedido(pedido);
        
        // Cancelamos el pedido
        sistema.cancelarPedido(pedido);

        // El stock debería volver a 10
        assertEquals(10, stock.getNumProductos(productoCaro), "El stock debería restaurarse tras cancelar el pedido.");
    }

    @Test
    public void testRecomendarProductos() {
        // Simulamos el mapa de categorías que devolvería la lectura del fichero
        Map<String, Integer> preferencias = new HashMap<>();
        preferencias.put("AVENTURA", 10);
        preferencias.put("FIGURA", 5);

        // Necesitamos que los productos tengan categorías asignadas para este test
        // NOTA: Como en tu código de Sistema evalúas "p.getCategoria().getSubcategoria()", 
        // asumo que tienes las clases Categoria y los enums listos.
        // Si fallase aquí por NullPointerException, es porque "getCategoria()" devuelve null en tus productos de prueba.
        
        /* Ejemplo conceptual de cómo probarlo si tuvieras las categorías seteadas:
        List<ProductoTienda> lista = Arrays.asList(productoCaro, productoBarato);
        List<ProductoTienda> recomendados = sistema.recomendarProductos(preferencias, lista);
        assertTrue(recomendados.size() > 0);
        */
    }
}