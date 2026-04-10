package utilidades;

/**
 * Enumera las distintas fases por las que puede pasar un producto de segunda mano.
 */
public enum EstadoProducto {
    /** El producto ha sido subido pero requiere revisión de un empleado. */
    PENDIENTE_DE_VALORAR, 
    
    /** El producto ha sido revisado, tasado y está listo para ser listado. */
    VALORADO, 
    
    /** El producto se encuentra bloqueado debido a un proceso de intercambio en curso. */
    EN_INTERCAMBIO, 
    
    /** El producto forma parte de una propuesta de oferta pendiente de resolución. */
    EN_OFERTA
}