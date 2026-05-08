package productos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una agrupación de productos o de otros packs (subpacks)
 * vendidos en conjunto bajo un precio específico.
 */
public class Pack implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String nombre;
    private final List<ProductoTienda> productos;
    private final List<Pack> subpacks;
    private double precio;
    private String categoria;

    /**
     * Constructor de la clase Pack.
     *
     * @param nombre    Nombre del pack.
     * @param precio    Precio total del conjunto.
     * @param productos Lista inicial de productos incluidos en el pack.
     */
    public Pack(String nombre, double precio, List<ProductoTienda> productos) {
        this(nombre, "", precio, productos);
    }

    public Pack(String nombre, String categoria, double precio, List<ProductoTienda> productos) {
        this.nombre = nombre;
        this.categoria = categoria == null ? "" : categoria.trim();
        this.precio = precio;
        this.productos = new ArrayList<>(productos); 
        this.subpacks = new ArrayList<>();
    }

    /**
     * Añade un nuevo pack como contenido de este pack (anidamiento).
     * @param subpack Objeto Pack a agregar.
     */
    public void addSubpack(Pack subpack) {this.subpacks.add(subpack);}

    public void addProducto(ProductoTienda producto) {
        if (producto != null) {
            this.productos.add(producto);
        }
    }

    public void addProducto(ProductoTienda producto, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            addProducto(producto);
        }
    }

    public void removeProducto(ProductoTienda producto) {
        this.productos.remove(producto);
    }

    /**
     * Establece o modifica el precio del pack completo.
     * @param precio Nuevo precio.
     */
    public void setPrecio(double precio) {this.precio = precio;}

    public void setCategoria(String categoria) {
        this.categoria = categoria == null ? "" : categoria.trim();
    }

    /**
     * Obtiene el nombre del pack.
     * @return Nombre del pack.
     */
    public String getNombre() {return this.nombre;}

    /**
     * Obtiene el precio del pack.
     * @return Precio monetario del pack.
     */
    public double getPrecio() {return this.precio;}

    public String getCategoria() {return this.categoria == null ? "" : this.categoria;}

    /**
     * Obtiene la lista de productos sueltos contenidos en el pack.
     * @return Copia de la lista de productos.
     */
    public List<ProductoTienda> getProductos() {return new ArrayList<>(this.productos);}

    /**
     * Obtiene la lista de subpacks contenidos en este pack.
     * @return Copia de la lista de subpacks.
     */
    public List<Pack> getSubpacks() {return new ArrayList<>(this.subpacks);}

    /**
     * Elimina un subpack específico de la estructura.
     * @param subpack Objeto Pack a retirar.
     */
    public void removeSubpack(Pack subpack) {
        subpacks.remove(subpack);
    }

}
