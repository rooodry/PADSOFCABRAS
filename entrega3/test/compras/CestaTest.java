package compras;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import productos.ProductoTienda;
import productos.Stock;
import utilidades.Status;

public class CestaTest {

    private Cesta cesta;
    private ProductoTienda productoTienda;
    private Stock stock;

    @BeforeEach
    void setUp() {
        cesta = new Cesta();
        productoTienda = new ProductoTienda("Funko Iron Man", "Figura", "iron.jpg");
        stock = new Stock();
        stock.añadirProducto(productoTienda, 5);
    }

    @Test
    void testCestaInicialEstaVacia() {
        assertTrue(cesta.estaVacia());
    }

    @Test
    void testAñadirProducto() {
        cesta.añadirProducto(productoTienda, 2);
        assertFalse(cesta.estaVacia());
        assertEquals(2, cesta.getProductos().get(productoTienda));
        
        cesta.añadirProducto(productoTienda, 3);
        assertEquals(5, cesta.getProductos().get(productoTienda));
    }

    @Test
    void testEliminarProducto() {
        cesta.añadirProducto(productoTienda, 1);
        cesta.eliminarProducto(productoTienda);
        assertTrue(cesta.estaVacia());
    }

    @Test
    void testLimpiarCesta() {
        cesta.añadirProducto(productoTienda, 5);
        cesta.limpiarCesta();
        assertTrue(cesta.estaVacia());
    }
}