package entrega3;

public class comic extends categoria {
    private final int numPaginas;
    private final String autor;
    private final String editorial;

    public Comic(int numPaginas, String autor, String editorial) {
        super();
        this.numPaginas = numPaginas;
        this.autor = autor;
        this.editorial = editorial;
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
}