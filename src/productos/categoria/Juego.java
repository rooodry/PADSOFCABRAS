package productos.categoria;


public class Juego extends Categoria {


    private final int numJugadores;


    private final int edadMinima;


    private final TipoJuego tipoJuego;

    /**
     * Construye un juego con todos sus atributos.
     *
     * @param nombre       nombre del juego (se usa como parte del nombre de categoría)
     * @param numJugadores número de jugadores recomendado
     * @param edadMinima   edad mínima recomendada en años
     * @param tipoJuego    tipo de juego
     */
    public Juego(String nombre, int numJugadores, int edadMinima, TipoJuego tipoJuego) {
        super("Juego:" + nombre);
        this.numJugadores = numJugadores;
        this.edadMinima   = edadMinima;
        this.tipoJuego    = tipoJuego;
    }

    /**
     * Devuelve el número de jugadores del juego.
     *
     * @return número de jugadores
     */
    public int getNumJugadores() { return this.numJugadores; }

    /**
     * Devuelve la edad mínima recomendada para jugar.
     *
     * @return edad mínima en años
     */
    public int getEdadMinima() { return this.edadMinima; }

    /**
     * Devuelve el tipo de juego de mesa.
     *
     * @return tipo de juego ({@link TipoJuego})
     */
    public TipoJuego getTipoJuego() { return this.tipoJuego; }
}