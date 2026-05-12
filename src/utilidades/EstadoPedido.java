package utilidades;

/**
 * Estado de un pedido dentro del proceso de compra y recogida.
 */
public enum EstadoPedido {

    /** Pedido creado pero aun no pagado. */
    EN_CARRITO,

    /** Pedido pagado y en preparacion. */
    EN_PREPARACION,

    /** Pedido preparado y listo para recogida. */
    LISTO,

    /** Pedido entregado al cliente. */
    ENTREGADO,

    /** Pedido cancelado antes de completarse. */
    CANCELADO
}
