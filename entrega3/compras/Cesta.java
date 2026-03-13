package compras;

import java.util.HashMap;
import java.util.Map;
import productos.ProductoTienda;
import productos.Stock;
import utilidades.Status;

public class Cesta {
    private Map<ProductoTienda, Integer> productos;

    public Cesta() {
        this.productos = new HashMap<>();
    }

    public Status comprobarStock(Stock stock, ProductoTienda producto, int cantidad) {
        if(stock != null && stock.getNumProductos(producto) >= cantidad) {
            return Status.OK;
        }

        return Status.ERROR;
    }

    public boolean estaVacia() {
        return this.productos.isEmpty();
    }

    public void limpiarCesta() {
        this.productos.clear();
    }

    public void añadirProducto(ProductoTienda producto, int cantidad) {
        if(cantidad > 0) {
            this.productos.put(producto, this.productos.getOrDefault(producto, 0) + cantidad);
        }
    }

    public void eliminarProducto(ProductoTienda producto) {
        this.productos.remove(producto);
    }

    public Map<ProductoTienda, Integer> getProductos() {
        return this.productos;
    }
}Producto
