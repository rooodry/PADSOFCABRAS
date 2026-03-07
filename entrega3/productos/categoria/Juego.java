package productos.categoria;
public class Juego extends Categoria {
    private final int numJugadores;
    private final int edadMinima;
    private final TipoJuego tipoJuego; 

    public Juego(int numJugadores, int edadMinima, TipoJuego tipoJuego) {
        super();
        this.numJugadores = numJugadores;
        this.edadMinima = edadMinima;
        this.tipoJuego = tipoJuego;
    }

    public int getNumJugadores() {
        return numJugadores;
    }

    public int getEdadMinima() {
        return edadMinima;
    }

    public TipoJuego getTipoJuego() {
        return tipoJuego;
    }
    
}