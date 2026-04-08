package productos.categoria;

/**
 * Categoría que representa una figura de colección dentro del catálogo.
 *
 * <p>Extiende {@link Categoria} añadiendo los atributos físicos y
 * comerciales propios de una figura: altura, marca y material.
 * El nombre de la categoría se construye con el prefijo {@code "Figura:"}
 * seguido del nombre de la figura.</p>
 *
 * <p>Todos los atributos son inmutables (declarados {@code final}).</p>
 */
public class Figura extends Categoria {

    /** Altura de la figura en centímetros. */
    private final double altura;

    /** Marca fabricante de la figura. */
    private final String marca;

    /** Material principal con el que está fabricada la figura. */
    private final String material;

    /**
     * Construye una figura con todos sus atributos.
     *
     * @param nombre   nombre de la figura (se usa como parte del nombre de categoría)
     * @param altura   altura en centímetros
     * @param marca    marca fabricante
     * @param material material principal de fabricación
     */
    public Figura(String nombre, double altura, String marca, String material) {
        super("Figura:" + nombre);
        this.altura   = altura;
        this.marca    = marca;
        this.material = material;
    }

    /**
     * Devuelve la altura de la figura en centímetros.
     *
     * @return altura en cm
     */
    public double getAltura() { return this.altura; }

    /**
     * Devuelve la marca fabricante de la figura.
     *
     * @return nombre de la marca
     */
    public String getMarca() { return this.marca; }

    /**
     * Devuelve el material principal de la figura.
     *
     * @return descripción del material
     */
    public String getMaterial() { return this.material; }
}