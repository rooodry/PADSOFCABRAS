package usuarios;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import productos.ProductoSegundaMano;

/**
 * Representa la cartera virtual de un {@link ClienteRegistrado} donde se
 * almacenan sus productos de segunda mano.
 *
 * <p>La cartera actúa como contenedor de los {@link ProductoSegundaMano}
 * que el cliente ha subido al sistema para vender o intercambiar. Permite
 * añadir y retirar productos, así como consultar el contenido actual.</p>
 *
 * <p>El accesor {@link #getProductos()} devuelve una copia defensiva para
 * evitar modificaciones externas de la lista interna.</p>
 */
public class Cartera implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Lista de productos de segunda mano del cliente. */
    private List<ProductoSegundaMano> productos;

    /**
     * Construye una cartera vacía.
     */
    public Cartera() {
        this.productos = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Modificadores
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Accesores
    // -------------------------------------------------------------------------

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
