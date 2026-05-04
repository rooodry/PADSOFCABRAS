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
    private final List<Producto> productos;
    private final List<Pack> subpacks;
    private double precio;

    /**
     * Constructor de la clase Pack.
     *
     * @param nombre    Nombre del pack.
     * @param precio    Precio total del conjunto.
     * @param productos Lista inicial de productos incluidos en el pack.
     */
    public Pack(String nombre, double precio, List<Producto> productos) {
        this.nombre = nombre;
        this.precio = precio;
        this.productos = new ArrayList<>(productos); 
        this.subpacks = new ArrayList<>();
    }

    /**
     * Añade un nuevo pack como contenido de este pack (anidamiento).
     * @param subpack Objeto Pack a agregar.
     */
    public void addSubpack(Pack subpack) {this.subpacks.add(subpack);}

    public void addProducto(Producto producto) {
        if (producto != null && !this.productos.contains(producto)) {
            this.productos.add(producto);
        }
    }

    public void removeProducto(Producto producto) {
        this.productos.remove(producto);
    }

    /**
     * Establece o modifica el precio del pack completo.
     * @param precio Nuevo precio.
     */
    public void setPrecio(double precio) {this.precio = precio;}

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

    /**
     * Obtiene la lista de productos sueltos contenidos en el pack.
     * @return Copia de la lista de productos.
     */
    public List<Producto> getProductos() {return new ArrayList<>(this.productos);}

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
