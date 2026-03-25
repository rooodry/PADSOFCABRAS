package test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import productos.ProductoTienda;
import productos.Stock;
import utilidades.Status;
import compras.Cesta;

public class CestaTest {

    private Cesta cesta;
    private ProductoTienda productoMock;
    private Stock stockMock;

    // Esta anotación hace que este método se ejecute ANTES de CADA test.
    // Nos asegura que cada prueba empieza con una cesta nueva y limpia.
    @BeforeEach
    public void setUp() {
        cesta = new Cesta();
        
        // Creamos "mocks" (simulaciones) de las clases de las que dependemos
        productoMock = mock(ProductoTienda.class);
        stockMock = mock(Stock.class);
    }

    // --- EMPIEZAN LOS TESTS ---

    @Test
    public void testCestaInicialmenteVacia() {
        // Comprobamos que al crear la cesta, está vacía
        assertEquals(cesta.estaVacia(), true);
        assertEquals(0, cesta.getProductos().size());
    }

    @Test
    public void testAñadirProducto() {
        // Añadimos 2 unidades del producto
        cesta.añadirProducto(productoMock, 2);
        
        // Verificamos que ya no está vacía y que hay 2 unidades
        assertFalse(cesta.estaVacia());
        assertEquals(2, cesta.getProductos().get(productoMock));

        // Añadimos 3 unidades MÁS del mismo producto
        cesta.añadirProducto(productoMock, 3);
        
        // Verificamos que se han sumado correctamente (2 + 3 = 5)
        assertEquals(5, cesta.getProductos().get(productoMock));
    }

    @Test
    public void testAñadirProductoCantidadNegativaOCero() {
        // Intentamos añadir una cantidad negativa
        cesta.añadirProducto(productoMock, -1);
        
        // Como tienes un "if(cantidad > 0)" en tu código, la cesta debería seguir vacía
        assertTrue(cesta.estaVacia(), "No se debería haber añadido una cantidad negativa");
    }

    @Test
    public void testEliminarProducto() {
        // Preparamos la cesta añadiendo un producto
        cesta.añadirProducto(productoMock, 1);
        
        // Lo eliminamos
        cesta.eliminarProducto(productoMock);
        
        // Comprobamos que se ha borrado correctamente
        assertTrue(cesta.estaVacia());
    }

    @Test
    public void testLimpiarCesta() {
        // Añadimos un par de productos diferentes
        ProductoTienda otroProductoMock = mock(ProductoTienda.class);
        cesta.añadirProducto(productoMock, 1);
        cesta.añadirProducto(otroProductoMock, 2);

        // Limpiamos la cesta
        cesta.limpiarCesta();
        
        // Verificamos que está vacía
        assertTrue(cesta.estaVacia());
    }

    @Test
    public void testComprobarStockSuficiente() {
        // Configuramos nuestro mock de Stock: 
        // Le decimos "cuando alguien te pregunte cuántos hay de productoMock, responde 10"
        when(stockMock.getNumProductos(productoMock)).thenReturn(10);

        // Llamamos a nuestro método pidiendo 5 unidades
        Status resultado = cesta.comprobarStock(stockMock, productoMock, 5);
        
        // Como 10 >= 5, debería devolver OK
        assertEquals(Status.OK, resultado);
    }

    @Test
    public void testComprobarStockInsuficiente() {
        // Configuramos el mock de Stock diciendo que solo quedan 2 unidades
        when(stockMock.getNumProductos(productoMock)).thenReturn(2);

        // Llamamos a nuestro método pidiendo 5 unidades
        Status resultado = cesta.comprobarStock(stockMock, productoMock, 5);
        
        // Como 2 no es mayor o igual a 5, debería devolver ERROR
        assertEquals(Status.ERROR, resultado);
    }
    
    @Test
    public void testComprobarStockNulo() {
        // Probamos qué pasa si le pasamos un stock null
        Status resultado = cesta.comprobarStock(null, productoMock, 5);
        
        // Según tu código, si stock != null no se cumple, devuelve ERROR. Lo comprobamos.
        assertEquals(Status.ERROR, resultado);
    }
}