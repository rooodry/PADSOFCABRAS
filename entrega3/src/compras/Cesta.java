package compras;

import java.util.HashMap;
import java.util.Map;
import productos.ProductoTienda;

public class Cesta {
    private Map<ProductoTienda, Integer> productos;

    public Cesta() {
        this.productos = new HashMap<>();
    }

    public void añadirProducto(ProductoTienda producto, int cantidad) {
        if(cantidad > 0) this.productos.put(producto, this.productos.getOrDefault(producto, 0) + cantidad);
    }

    public Map<ProductoTienda, Integer> getProductos() {
        return new HashMap<>(this.productos);
    }

    public boolean estaVacia() {
        return this.productos.isEmpty();
    }

    public void limpiarCesta() {
        this.productos.clear();
    }
   
    public void eliminarProducto(ProductoTienda producto) {
        this.productos.remove(producto);
    }


}
