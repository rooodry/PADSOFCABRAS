package productos;

import java.util.ArrayList;
import java.util.List;

public class Stock {
    private List<ProductoTienda> productos = new ArrayList<>();
    
    public Stock(List<ProductoTienda> productos) {
        this.productos = productos;
    }
    
}