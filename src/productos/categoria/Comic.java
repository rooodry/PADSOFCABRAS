package productos.categoria;

/**
 * Categoria especializada para comics, con datos editoriales y de genero.
 */
public class Comic extends Categoria {


    private final int numPaginas;


    private final String autor;


    private final String editorial;


    private final Genero genero;


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
