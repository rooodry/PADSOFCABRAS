package productos;

import java.util.ArrayList;
import java.util.List;

public class Stock {
    private List<ProductoTienda> productos = new ArrayList<>();
    
    public Stock(List<ProductoTienda> productos) {
        this.productos = new ArrayList<>(productos);
    }

    public void retirarProducto(ProductoTienda p) {
        productos.remove(p);
    }

    public void añadirProducto(ProductoTienda p) {
        productos.add(p);
    }

    public void reducirStock(ProductoTienda p) {
        productos.remove(p);
    }
    
}