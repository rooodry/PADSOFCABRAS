package utilidades;

/**
 * Estado de una oferta dentro del flujo de intercambio.
 */
public enum EstadoOferta {

    /** Oferta enviada y pendiente de respuesta. */
    PENDIENTE,

    /** Oferta aceptada por el receptor. */
    ACEPTADA,

    /** Oferta rechazada por el receptor. */
    RECHAZADA,

    /** Oferta vencida por superar su fecha limite. */
    CADUCADA;
}
