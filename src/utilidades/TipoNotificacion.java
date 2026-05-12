package utilidades;

/**
 * Clasifica las notificaciones mostradas a los usuarios.
 */
public enum TipoNotificacion {

    /** El pago de un pedido ha sido registrado. */
    PAGO_REALIZADO,

    /** Un pedido ha sido entregado. */
    PEDIDO_ENTREGADO,

    /** Un pedido esta listo para recoger. */
    PEDIDO_LISTO,

    /** Un pedido ha expirado o no se ha completado a tiempo. */
    PEDIDO_EXPIRADO,

    /** El usuario ha recibido una nueva oferta de intercambio. */
    NUEVA_OFERTA,

    /** Una oferta enviada o recibida ha sido aceptada. */
    OFERTA_ACEPTADA,

    /** Una oferta enviada o recibida ha sido rechazada. */
    OFERTA_RECHAZADA,

    /** Una oferta ha caducado. */
    OFERTA_CADUCA,

    /** Se ha publicado o aplicado un nuevo descuento. */
    NUEVO_DESCUENTO,

    /** Un intercambio se ha marcado como realizado. */
    INTERCAMBIO_REALIZADO,

    /** Un producto se ha subido correctamente. */
    PRODUCTO_SUBIDO,

    /** Un producto de segunda mano ha recibido valoracion. */
    VALORACION_REALIZADA;
}
