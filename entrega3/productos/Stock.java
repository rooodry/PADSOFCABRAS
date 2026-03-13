package productos;

import java.util.HashMap;
import java.util.Map;

public class Stock {
    private Map<ProductoTienda, Integer> productos;
    
    public Stock() {
        this.productos = new HashMap<>();
    }

    public void retirarProducto(ProductoTienda producto) {
        this.productos.remove(producto);
    }

    public void añadirProducto(ProductoTienda producto, int cantidad) {
        if(cantidad > 0) {
            this.productos.put(producto, this.productos.getOrDefault(producto, 0));
        }
    }

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

    public int getNumProductos(ProductoTienda producto) {
        return this.productos.getOrDefault(producto, 0);
    }    
}
