package productos.categoria;

/**
 * Categoría que representa un juego de mesa dentro del catálogo de productos.
 *
 * <p>Extiende {@link Categoria} añadiendo los atributos propios de un juego:
 * número de jugadores, edad mínima recomendada y tipo de juego.
 * El nombre de la categoría se construye con el prefijo {@code "Juego:"}
 * seguido del nombre del juego.</p>
 *
 * <p>Todos los atributos son inmutables (declarados {@code final}).</p>
 *
 * @see TipoJuego
 */
public class Juego extends Categoria {

    /** Número máximo (o recomendado) de jugadores. */
    private final int numJugadores;

    /** Edad mínima recomendada para jugar. */
    private final int edadMinima;

    /** Tipo de juego de mesa ({@link TipoJuego#JUEGO_MESA}, {@link TipoJuego#CARTAS} o {@link TipoJuego#DADOS}). */
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