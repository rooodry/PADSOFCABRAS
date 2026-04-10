package utilidades;

/**
 * Enumera los estados posibles en el ciclo de vida de un pedido.
 */
public enum EstadoPedido {
    /** El pedido aún no se ha formalizado y los artículos siguen en la cesta. */
    EN_CARRITO, 
    
    /** El pedido ha sido pagado y el personal lo está organizando. */
    EN_PREPARACION, 
    
    /** El pedido está empaquetado y a la espera de recogida o envío. */
    LISTO, 
    
    /** El pedido ha llegado satisfactoriamente al cliente. */
    ENTREGADO, 
    
    /** El pedido ha sido anulado y no continuará su flujo. */
    CANCELADO
}