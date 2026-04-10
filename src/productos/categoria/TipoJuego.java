package productos.categoria;

/**
 * Enumeración de los tipos de juego de mesa disponibles para los {@link Juego juegos}.
 *
 * <ul>
 *   <li>{@link #JUEGO_MESA} – juego de tablero tradicional.</li>
 *   <li>{@link #CARTAS}     – juego basado en cartas.</li>
 *   <li>{@link #DADOS}      – juego basado en dados.</li>
 * </ul>
 *
 * <p>Se utiliza en el sistema de recomendaciones para calcular el perfil
 * de intereses de cada cliente.</p>
 */
public enum TipoJuego {
    /** Juego de tablero tradicional. */
    JUEGO_MESA,

    /** Juego basado en cartas. */
    CARTAS,

    /** Juego basado en dados. */
    DADOS
}