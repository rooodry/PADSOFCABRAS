// ============================================================
// Categoria.java
// ============================================================
package productos.categoria;

/**
 * Clase abstracta que representa la categoría de un producto.
 *
 * <p>Cada producto del catálogo pertenece a una categoría principal
 * ({@link Comic}, {@link Juego} o {@link Figura}) que puede tener a su
 * vez una subcategoría anidada ({@code subCategoria}).</p>
 *
 * <p>En la implementación actual la subcategoría se usa para almacenar
 * el género ({@link Genero}) o tipo de juego ({@link TipoJuego}) de forma
 * polimórfica, aunque estos valores son enums y no instancias de
 * {@code Categoria}. El campo {@code subCategoria} se inicializa a
 * {@code null}.</p>
 */
public abstract class Categoria {

    /** Nombre identificador de la categoría. */
    private String nombre;

    /**
     * Subcategoría anidada; puede ser {@code null} si no aplica.
     *
     * <p>En la práctica se utiliza para almacenar referencias a
     * {@link Genero} o {@link TipoJuego}, aunque el tipo declarado
     * es {@code Categoria}.</p>
     */
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