


package productos.categoria;

import java.io.Serializable;


public abstract class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;


    private String nombre;


    private Categoria subCategoria;

    /**
     * Construye una categoría con el nombre indicado y sin subcategoría.
     *
     * @param nombre nombre de la categoría
     */
    public Categoria(String nombre) {
        this.nombre      = nombre;
        this.subCategoria = null;
    }

    /**
     * Establece la subcategoría anidada.
     *
     * @param subcategoria subcategoría a asignar; puede ser {@code null}
     */
    public void setSubCategoria(Categoria subcategoria) {
        this.subCategoria = subcategoria;
    }

    /**
     * Devuelve el nombre de la categoría.
     *
     * @return nombre de la categoría
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Devuelve la subcategoría anidada.
     *
     * @return subcategoría, o {@code null} si no se ha asignado ninguna
     */
    public Categoria getSubcategoria() {
        return this.subCategoria;
    }
}
