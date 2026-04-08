package productos.categoria;

public class Comic extends Categoria {
    private final int numPaginas;
    private final String autor;
    private final String editorial;
    private final Genero genero;
    private final int año;

    public Comic(String comic, int numPaginas, String autor, String editorial, Genero genero, int año) {
        super("Comic:" + comic);
        this.numPaginas = numPaginas;
        this.autor = autor;
        this.editorial = editorial;
        this.genero = genero;
        this.año = año;
    }


    //GETTERS//

    public int getNumPaginas() {return this.numPaginas;}

    public String getAutor() {return this.autor;}

    public String getEditorial() {return this.editorial;}

    public Genero getGenero() {return this.genero;}

    public int getAño() {return this.año;}
}