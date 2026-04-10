package descuentos;

import java.util.Date;
import compras.Pedido;

/**
 * Clase abstracta que representa un descuento aplicable a un pedido.
 *
 * <p>Define el período de vigencia del descuento (fecha de inicio y fin)
 * y declara los métodos abstractos que cada tipo concreto de descuento
 * debe implementar: la comprobación de aplicabilidad y el cálculo del
 * precio resultante.</p>
 *
 * <p>Las fechas se almacenan como copias defensivas para evitar mutaciones
 * externas.</p>
 *
 * @see DescuentoPorcentaje
 * @see DescuentoCantidadGastada
 * @see DescuentoRegalo
 * @see DescuentoDosPorUno
 */
public abstract class Descuento {

    /** Fecha a partir de la cual el descuento es válido. */
    private final Date fechaInicio;

    /** Fecha a partir de la cual el descuento deja de ser válido. */
    private final Date fechaFin;

    /**
     * Construye un descuento con el período de vigencia indicado.
     *
     * @param fechaInicio fecha de inicio de la validez del descuento; no debe ser {@code null}
     * @param fechaFin    fecha de fin de la validez del descuento; no debe ser {@code null}
     */
    public Descuento(Date fechaInicio, Date fechaFin) {
        this.fechaInicio = new Date(fechaInicio.getTime());
        this.fechaFin    = new Date(fechaFin.getTime());
    }

    /**
     * Devuelve la fecha de inicio de la vigencia del descuento.
     *
     * @return copia defensiva de la fecha de inicio
     */
    public Date getFechaInicio() {
        return new Date(this.fechaInicio.getTime());
    }

    /**
     * Devuelve la fecha de fin de la vigencia del descuento.
     *
     * @return copia defensiva de la fecha de fin
     */
    public Date getFechaFin() {
        return new Date(this.fechaFin.getTime());
    }

    /**
     * Determina si este descuento puede aplicarse al pedido indicado.
     *
     * @param pedido el pedido sobre el que se evalúa la aplicabilidad
     * @return {@code true} si el descuento es aplicable; {@code false} en caso contrario
     */
    public abstract boolean esAplicable(Pedido pedido);

    /**
     * Aplica el descuento sobre un precio base y devuelve el precio resultante.
     *
     * @param precioBase precio original antes del descuento
     * @return precio final tras aplicar el descuento; nunca negativo en
     *         implementaciones correctas
     */
    public abstract double aplicarDescuento(double precioBase);
}