package usuarios;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import productos.ProductoSegundaMano;


public class Cartera implements Serializable {

    private static final long serialVersionUID = 1L;


    private List<ProductoSegundaMano> productos;

    /**
     * Construye una cartera vacía.
     */
    public Cartera() {
        this.productos = new ArrayList<>();
    }


    /**
     * Añade un producto de segunda mano a la cartera.
     *
     * @param productoSegundaMano producto a añadir; no debe ser {@code null}
     */
    public void añadirProducto(ProductoSegundaMano productoSegundaMano) {
        this.productos.add(productoSegundaMano);
    }

    /**
     * Retira un producto de segunda mano de la cartera.
     *
     * <p>Si el producto no está en la cartera, la operación no tiene efecto.</p>
     *
     * @param productoSegundaMano producto a retirar
     */
    public void retirarProducto(ProductoSegundaMano productoSegundaMano) {
        this.productos.remove(productoSegundaMano);
    }


    /**
     * Devuelve la lista de productos de segunda mano de la cartera.
     *
     * @return copia defensiva de la lista de productos; nunca {@code null}
     */
    public List<ProductoSegundaMano> getProductos() {
        return new ArrayList<>(this.productos);
    }

    /**
     * Devuelve el número de productos de segunda mano en la cartera.
     *
     * @return número de productos (≥ 0)
     */
    public int getNumProductos() {
        return this.productos.size();
    }
}
