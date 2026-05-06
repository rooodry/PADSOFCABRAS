package compras;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import productos.ProductoTienda;
import productos.Pack;


public class Cesta implements Serializable {

    private static final long serialVersionUID = 1L;
    private Map<ProductoTienda, Integer> productos;
    private Map<Pack, Integer> packs;

    /**
     * Constructor de la clase Cesta.
     * Inicializa la colección de productos vacía.
     */
    public Cesta() {
        this.productos = new HashMap<>();
        this.packs = new HashMap<>();
    }

    /**
     * Añade un producto a la cesta incrementando su cantidad.
     * Si el producto ya existe, suma la nueva cantidad a la actual.
     * * @param producto Objeto ProductoTienda a añadir.
     * @param cantidad Número de unidades a añadir (debe ser mayor que 0).
     */
    public void añadirProducto(ProductoTienda producto, int cantidad) {
        asegurarMapasInicializados();
        if(cantidad > 0) {
            this.productos.put(producto, this.productos.getOrDefault(producto, 0) + cantidad);
        }
    }

    public void añadirPack(Pack pack) {
        asegurarMapasInicializados();
        this.packs.merge(pack, 1, Integer::sum);
    }

    /**
     * Obtiene el mapa completo de productos contenidos en la cesta y sus cantidades.
     * * @return Una copia del mapa de productos de la cesta.
     */
    public Map<ProductoTienda, Integer> getProductos() {
        asegurarMapasInicializados();
        return new HashMap<>(this.productos);
    }

    public Map<Pack, Integer> getPacks() {
        asegurarMapasInicializados();
        return new HashMap<>(this.packs);
    }


    /**
     * Comprueba si la cesta se encuentra vacía.
     * * @return true si no hay productos en la cesta, false en caso contrario.
     */
    public boolean estaVacia() {
        asegurarMapasInicializados();
        return this.productos.isEmpty() && this.packs.isEmpty();
    }

    /**
     * Vacía completamente la cesta, eliminando todos los productos que contenga.
     */
    public void limpiarCesta() {
        asegurarMapasInicializados();
        this.productos.clear();
        this.packs.clear();
    }
   
    /**
     * Elimina por completo un producto de la cesta, independientemente de su cantidad.
     * * @param producto Objeto ProductoTienda a eliminar.
     */
    public void eliminarProducto(ProductoTienda producto) {
        asegurarMapasInicializados();
        this.productos.remove(producto);
    }

    private void asegurarMapasInicializados() {
        if (this.productos == null) {
            this.productos = new HashMap<>();
        }
        if (this.packs == null) {
            this.packs = new HashMap<>();
        }
    }

    public void retirarPack(Pack pack) {
    if (!packs.containsKey(pack)) {
        return;
    }

    int cantidad = packs.get(pack);

    if (cantidad <= 1) {
        packs.remove(pack);
    } else {
        packs.put(pack, cantidad - 1);
    }
}
}
