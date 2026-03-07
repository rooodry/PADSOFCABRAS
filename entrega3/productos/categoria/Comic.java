package productos.categoria;
public class Comic extends Categoria {
    private final int numPaginas;
    private final String autor;
    private final String editorial;
    private final Genero genero;

    public Comic(int numPaginas, String autor, String editorial, Genero genero) {
        super();
        this.numPaginas = numPaginas;
        this.autor = autor;
        this.editorial = editorial;
        this.genero = genero;
    }


    public int getNumPaginas() {
        return numPaginas;
    }

    public String getAutor() {
        return autor;
    }

    public String getEditorial() {
        return editorial;
    }

    public Genero getGenero() {
        return genero;
    }
}