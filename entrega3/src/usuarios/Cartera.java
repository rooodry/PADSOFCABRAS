package usuarios;

import java.util.ArrayList;
import java.util.List;

import productos.ProductoSegundaMano;

public class Cartera {
    private List<ProductoSegundaMano> productos;

    public Cartera() {
        this.productos = new ArrayList<>();
    }

    //SETTERS//
    public void añadirProducto(ProductoSegundaMano productoSegundaMano) {
        this.productos.add(productoSegundaMano);
    }

    public void retirarProducto(ProductoSegundaMano productoSegundaMano) {
        this.productos.remove(productoSegundaMano);
    }

    //GETTERS//
    public List<ProductoSegundaMano> getProductos() {return new ArrayList<>(this.productos);}
    public int getNumProductos() {return this.productos.size();}
}
