package utilidades;

/**
 * Define los posibles estados físicos y de conservación en los que se puede 
 * encontrar un producto de segunda mano tras su evaluación.
 */
public enum EstadoConservacion {
    /**
     * El producto se encuentra como nuevo o en condiciones idénticas a las de fábrica, 
     * sin ningún tipo de marca o desgaste.
     */
    PERFECTO,

    /**
     * El producto ha sido utilizado, pero se mantiene en excelentes condiciones 
     * con señales de uso prácticamente imperceptibles.
     */
    MUY_BUENO,

    /**
     * El producto presenta marcas superficiales o un desgaste menor 
     * derivado de un uso normal y cuidadoso.
     */
    USO_LIGERO,

    /**
     * El producto tiene señales visibles, claras y evidentes de haber sido 
     * utilizado de forma habitual.
     */
    USO_EVIDENTE,

    /**
     * El producto muestra un desgaste estético considerable debido a un 
     * uso prolongado o intenso, aunque sigue siendo funcional.
     */
    MUY_USADO,

    /**
     * El producto presenta roturas, fallos funcionales o desperfectos graves 
     * que afectan a su uso normal o estética general.
     */
    DAÑADO;
}