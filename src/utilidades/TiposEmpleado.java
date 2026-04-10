package utilidades;

/**
 * Enumera los distintos roles o departamentos a los que puede pertenecer un empleado.
 */
public enum TiposEmpleado {
    /** Empleado dedicado a gestionar intercambios de segunda mano. */
    EMPLEADOS_INTERCAMBIO,
    
    /** Empleado encargado de la gestión del catálogo y valoración de productos. */
    EMPLEADOS_PRODUCTO,
    
    /** Empleado encargado de la preparación y seguimiento de las compras. */
    EMPLEADOS_PEDIDO;
}