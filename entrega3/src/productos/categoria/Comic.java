package productos.categoria;

/**
 * Categoría que representa un cómic dentro del catálogo de productos.
 *
 * <p>Extiende {@link Categoria} añadiendo los atributos propios de un cómic:
 * número de páginas, autor, editorial, género literario y año de publicación.
 * El nombre de la categoría se construye con el prefijo {@code "Comic:"} seguido
 * del título.</p>
 *
 * <p>Todos los atributos son inmutables (declarados {@code final}).</p>
 *
 * @see Genero
 */
public class Comic extends Categoria {

    /** Número total de páginas del cómic. */
    private final int numPaginas;

    /** Autor o autores del cómic. */
    private final String autor;

    /** Editorial que publicó el cómic. */
    private final String editorial;

    /** Género literario del cómic ({@link Genero#AVENTURA}, {@link Genero#ROMANCE} o {@link Genero#COMEDIA}). */
    private final Genero genero;

    /** Año de publicación del cómic. */
    private final int año;

    /**
     * Construye un cómic con todos sus atributos.
     *
     * @param comic      título del cómic (se usa como parte del nombre de categoría)
     * @param numPaginas número de páginas
     * @param autor      autor o autores
     * @param editorial  editorial publicadora
     * @param genero     género literario
     * @param año        año de publicación
     */
    public Comic(String comic, int numPaginas, String autor, String editorial,
                 Genero genero, int año) {
        super("Comic:" + comic);
        this.numPaginas = numPaginas;
        this.autor      = autor;
        this.editorial  = editorial;
        this.genero     = genero;
        this.año        = año;
    }

    /**
     * Devuelve el número de páginas del cómic.
     *
     * @return número de páginas
     */
    public int getNumPaginas() { return this.numPaginas; }

    /**
     * Devuelve el autor del cómic.
     *
     * @return nombre del autor
     */
    public String getAutor() { return this.autor; }

    /**
     * Devuelve la editorial del cómic.
     *
     * @return nombre de la editorial
     */
    public String getEditorial() { return this.editorial; }

    /**
     * Devuelve el género literario del cómic.
     *
     * @return género ({@link Genero#AVENTURA}, {@link Genero#ROMANCE} o {@link Genero#COMEDIA})
     */
    public Genero getGenero() { return this.genero; }

    /**
     * Devuelve el año de publicación del cómic.
     *
     * @return año de publicación
     */
    public int getAño() { return this.año; }
}