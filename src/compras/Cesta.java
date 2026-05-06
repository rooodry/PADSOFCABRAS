package compras;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import productos.ProductoTienda;

/**
 * Representa la cesta de la compra en la que se agrupan los productos
 * seleccionados por un usuario antes de formalizar un pedido.
 */
public class Cesta implements Serializable {

    private static final long serialVersionUID = 1L;
    private Map<ProductoTienda, Integer> productos;

    /**
     * Constructor de la clase Cesta.
     * Inicializa la colección de productos vacía.
     */
    public Cesta() {
        this.productos = new HashMap<>();
    }

    /**
     * Añade un producto a la cesta incrementando su cantidad.
     * Si el producto ya existe, suma la nueva cantidad a la actual.
     * * @param producto Objeto ProductoTienda a añadir.
     * @param cantidad Número de unidades a añadir (debe ser mayor que 0).
     */
    public void añadirProducto(ProductoTienda producto, int cantidad) {
        if(cantidad > 0) {
            this.productos.put(producto, this.productos.getOrDefault(producto, 0) + cantidad);
        }
    }

    /**
     * Obtiene el mapa completo de productos contenidos en la cesta y sus cantidades.
     * * @return Una copia del mapa de productos de la cesta.
     */
    public Map<ProductoTienda, Integer> getProductos() {
        return new HashMap<>(this.productos);
    }

    /**
     * Comprueba si la cesta se encuentra vacía.
     * * @return true si no hay productos en la cesta, false en caso contrario.
     */
    public boolean estaVacia() {
        return this.productos.isEmpty();
    }

    /**
     * Vacía completamente la cesta, eliminando todos los productos que contenga.
     */
    public void limpiarCesta() {
        this.productos.clear();
    }
   
    /**
     * Elimina por completo un producto de la cesta, independientemente de su cantidad.
     * * @param producto Objeto ProductoTienda a eliminar.
     */
    public void eliminarProducto(ProductoTienda producto) {
        this.productos.remove(producto);
    }
}
