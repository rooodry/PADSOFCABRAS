package utilidades;

/**
 * Estado operativo de un producto de segunda mano.
 */
public enum EstadoProducto {

    /** Producto pendiente de tasacion por un empleado. */
    PENDIENTE_DE_VALORAR,

    /** Producto ya tasado y disponible para su gestion. */
    VALORADO,

    /** Producto bloqueado porque participa en un intercambio aceptado. */
    EN_INTERCAMBIO,

    /** Producto bloqueado temporalmente por una oferta activa. */
    EN_OFERTA
}
