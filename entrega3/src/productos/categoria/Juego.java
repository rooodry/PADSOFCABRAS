package productos.categoria;

public class Juego extends Categoria {
    private final int numJugadores;
    private final int edadMinima;
    private final TipoJuego tipoJuego; 

    public Juego(String nombre, int numJugadores, int edadMinima, TipoJuego tipoJuego) {
        super("Juego:" + nombre);
        this.numJugadores = numJugadores;
        this.edadMinima = edadMinima;
        this.tipoJuego = tipoJuego;
    }
    
    //GETTERS//
    public int getNumJugadores() {return this.numJugadores;}

    public int getEdadMinima() {return this.edadMinima;}

    public TipoJuego getTipoJuego() {return this.tipoJuego;}
    
}