package utilidades;

/**
 * Define los estados posibles de una oferta de intercambio en el sistema.
 * Se utiliza para realizar el seguimiento de las propuestas entre el usuario lanzador 
 * y el usuario receptor.
 */
public enum EstadoOferta {
    /** * La oferta ha sido creada y enviada, pero aún no ha sido procesada por el receptor.
     * Es el estado asignado por defecto al instanciar una nueva oferta.
     */
    PENDIENTE,

    /** * El usuario receptor ha aceptado la propuesta de intercambio.
     * Este estado permite avanzar en el proceso de intercambio físico.
     */
    ACEPTADA,

    /** * El usuario receptor ha declinado formalmente la propuesta de intercambio.
     */
    RECHAZADA;
}