package productos.categoria;

/**
 * Enumeración de los géneros literarios disponibles para los {@link Comic cómics}.
 *
 * <ul>
 *   <li>{@link #AVENTURA} – cómics de acción y aventura.</li>
 *   <li>{@link #ROMANCE}  – cómics de romance y drama sentimental.</li>
 *   <li>{@link #COMEDIA}  – cómics de humor y comedia.</li>
 * </ul>
 *
 * <p>Se utiliza en el sistema de recomendaciones para calcular el perfil
 * de intereses de cada cliente.</p>
 */
public enum Genero {
    /** Cómics de acción y aventura. */
    AVENTURA,

    /** Cómics de romance y drama sentimental. */
    ROMANCE,

    /** Cómics de humor y comedia. */
    COMEDIA
}