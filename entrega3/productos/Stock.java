package productos;

import java.util.ArrayList;
import java.util.List;

public class Stock {
    private List<ProductoTienda> productos = new ArrayList<>();
    
    public Stock(List<ProductoTienda> productos) {
        this.productos = new ArrayList<>(productos);
    }

    public void retirarProducto(ProductoTienda producto) {
        productos.remove(producto);
    }

    public void añadirProducto(ProductoTienda producto) {
        productos.add(producto);
    }

    public void reducirStock(ProductoTienda producto) {
        productos.remove(producto);
    }

    public int getNumProductos(ProductoTienda producto) {
        return this.productos.size();
    }
    
}