package descuentos;

import java.io.Serializable;
import java.util.Date;
import compras.Pedido;

/**
 * Clase base para las promociones y descuentos aplicables a pedidos.
 *
 * <p>Cada descuento define un periodo de vigencia y delega en sus subclases
 * las reglas concretas de aplicabilidad y calculo del precio final.</p>
 */
public abstract class Descuento implements Serializable {

    private static final long serialVersionUID = 1L;


    private final Date fechaInicio;


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
