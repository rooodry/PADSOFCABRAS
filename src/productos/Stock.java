package productos;

import java.util.HashMap;
import java.util.Map;

import utilidades.Status;

/**
 * Gestiona el inventario de los productos de la tienda, 
 * controlando las existencias disponibles de cada uno.
 */
public class Stock {
    private Map<ProductoTienda, Integer> productos;
    
    /**
     * Constructor que inicializa el inventario vacío.
     */
    public Stock() {
        this.productos = new HashMap<>();
    }

    /**
     * Obtiene un mapa con todos los productos y sus existencias actuales.
     * @return Una copia del mapa de productos e inventario.
     */
    public Map<ProductoTienda, Integer> getProductos() {return new HashMap<ProductoTienda, Integer>(this.productos);}

    /**
     * Elimina por completo un producto del inventario, sin importar su cantidad.
     * @param producto Objeto ProductoTienda a eliminar.
     */
    public void retirarProducto(ProductoTienda producto) {
        this.productos.remove(producto);
    }

    /**
     * Añade una cantidad específica de un producto al inventario.
     * Si el producto ya existe, suma la cantidad a la existente.
     * @param producto Objeto ProductoTienda a añadir.
     * @param cantidad Cantidad de unidades a incrementar.
     */
    public void añadirProducto(ProductoTienda producto, int cantidad) {
        this.productos.put(producto, this.productos.getOrDefault(producto, 0) + cantidad);
    }

    /**
     * Reduce las existencias de un producto en la cantidad indicada.
     * Si la cantidad final es menor o igual a 0, el producto se elimina del inventario.
     * @param producto Objeto ProductoTienda a reducir.
     * @param cantidad Cantidad de unidades a descontar.
     */
    public void reducirStock(ProductoTienda producto, int cantidad) {
        int nuevaCantidad = 0;
        if(this.productos.containsKey(producto)) {
            nuevaCantidad = this.productos.get(producto) - cantidad;
        }

        if(nuevaCantidad <= 0) {
            this.productos.remove(producto);
        } else {
            this.productos.put(producto, nuevaCantidad);
        }
    }

    /**
     * Verifica si existe una cantidad suficiente de un producto en el inventario.
     * @param producto ProductoTienda a verificar.
     * @param cantidad Cantidad requerida.
     * @return Status.OK si hay suficiente stock, Status.ERROR en caso contrario.
     */
    public Status comprobarStock(ProductoTienda producto, int cantidad) {
        if(this.getNumProductos(producto) >= cantidad) {
            return Status.OK;
        }

        return Status.ERROR;
    }

    /**
     * Obtiene la cantidad de unidades disponibles de un producto específico.
     * @param producto ProductoTienda a consultar.
     * @return Número entero con la cantidad de existencias. Retorna 0 si no existe en inventario.
     */
    public int getNumProductos(ProductoTienda producto) {
        return this.productos.getOrDefault(producto, 0);
    }

}