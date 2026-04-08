package productos;

import java.util.HashMap;
import java.util.Map;

import utilidades.Status;

public class Stock {
    private Map<ProductoTienda, Integer> productos;
    
    public Stock() {
        this.productos = new HashMap<>();
    }

    public Map<ProductoTienda, Integer> getProductos() {return new HashMap<ProductoTienda, Integer>(this.productos);}

    public void retirarProducto(ProductoTienda producto) {
        this.productos.remove(producto);
    }

    public void añadirProducto(ProductoTienda producto, int cantidad) {this.productos.put(producto, this.productos.getOrDefault(producto, 0) + cantidad);}

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


    public Status comprobarStock(ProductoTienda producto, int cantidad) {
        if(this.getNumProductos(producto) >= cantidad) {
            return Status.OK;
        }

        return Status.ERROR;
    }

    public int getNumProductos(ProductoTienda producto) {
        return this.productos.getOrDefault(producto, 0);
    }

}