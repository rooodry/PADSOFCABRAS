package usuarios;

import java.util.ArrayList;
import java.util.List;

import productos.ProductoSegundaMano;

public class Cartera {
    private int numProductos;
    private List<ProductoSegundaMano> productos;

    public Cartera() {
        this.productos = new ArrayList<>();
        this.numProductos = 0;
    }

    //SETTERS//
    public void añadirProducto(ProductoSegundaMano productoSegundaMano) {
        this.productos.add(productoSegundaMano);
        this.numProductos++;
    }

    //GETTERS//
    public List<ProductoSegundaMano> getProductos() {return this.productos;}
    public int getNumProductos() {return this.numProductos;}
}
