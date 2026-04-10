package utilidades;

/**
 * Enumera los diferentes tipos de eventos que pueden generar una notificación en el sistema.
 */
public enum TipoNotificacion {
    /** Aviso de que se ha completado el pago de un pedido. */
    PAGO_REALIZADO,
    
    /** Aviso de que un pedido está preparado para ser recogido o enviado. */
    PEDIDO_LISTO,
    
    /** Aviso de que el plazo de un pedido o reserva ha vencido. */
    PEDIDO_EXPIRADO,
    
    /** Aviso de una nueva propuesta de intercambio recibida. */
    NUEVA_OFERTA,
    
    /** Aviso de que una propuesta de intercambio enviada fue aceptada. */
    OFERTA_ACEPTADA,
    
    /** Aviso de que una propuesta de intercambio enviada fue rechazada. */
    OFERTA_RECHAZADA,
    
    /** Aviso de que un nuevo descuento promocional está disponible. */
    NUEVO_DESCUENTO,
    
    /** Aviso de que el proceso físico de intercambio se ha materializado. */
    INTERCAMBIO_REALIZADO,
    
    /** Aviso de que un producto de segunda mano ha terminado de ser revisado por un empleado. */
    VALORACION_REALIZADA;
}