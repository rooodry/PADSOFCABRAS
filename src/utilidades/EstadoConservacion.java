package utilidades;

/**
 * Nivel de conservacion declarado para un producto de segunda mano.
 */
public enum EstadoConservacion {

    /** Producto sin desgaste apreciable. */
    PERFECTO,

    /** Producto en muy buen estado, con marcas minimas. */
    MUY_BUENO,

    /** Producto con senales leves de uso. */
    USO_LIGERO,

    /** Producto funcional con desgaste visible. */
    USO_EVIDENTE,

    /** Producto muy usado, pero todavia aprovechable. */
    MUY_USADO,

    /** Producto con danos o deterioro relevante. */
    DAÑADO;
}
