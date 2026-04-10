package descuentos;

import java.util.Date;
import compras.Pedido;

/**
 * Descuento por porcentaje que se activa cuando el total del pedido supera
 * una cantidad mínima de gasto.
 *
 * <p>Combina una condición de aplicabilidad (importe mínimo) con una
 * reducción porcentual sobre el precio base. Solo se aplica si el total
 * calculado del pedido es igual o superior a {@code cantidadMinima}.</p>
 *
 * <p>Ejemplo: con {@code cantidadMinima = 50.0} y {@code porcentaje = 10.0},
 * el descuento se activará en pedidos de 50 € o más y reducirá el precio
 * un 10 %.</p>
 */
public class DescuentoCantidadGastada extends Descuento {

    /** Importe mínimo que debe alcanzar el pedido para que el descuento sea aplicable. */
    private final double cantidadMinima;

    /** Porcentaje de reducción a aplicar cuando se cumple la condición, en el rango [0, 100]. */
    private final double porcentaje;

    /**
     * Construye un descuento por cantidad gastada.
     *
     * @param fechaInicio    fecha de inicio de la vigencia del descuento
     * @param fechaFin       fecha de fin de la vigencia del descuento
     * @param cantidadMinima importe mínimo del pedido para activar el descuento
     * @param porcentaje     porcentaje de reducción a aplicar (entre 0 y 100)
     */
    public DescuentoCantidadGastada(Date fechaInicio, Date fechaFin,
                                     double cantidadMinima, double porcentaje) {
        super(fechaInicio, fechaFin);
        this.cantidadMinima = cantidadMinima;
        this.porcentaje     = porcentaje;
    }

    /**
     * Devuelve el importe mínimo requerido para que el descuento sea aplicable.
     *
     * @return cantidad mínima de gasto en euros
     */
    public double getCantidadMinima() {
        return this.cantidadMinima;
    }

    /**
     * Devuelve el porcentaje de reducción configurado.
     *
     * @return porcentaje de descuento en el rango [0, 100]
     */
    public double getPorcentaje() {
        return this.porcentaje;
    }

    /**
     * Indica si el descuento es aplicable al pedido, comprobando que su total
     * sea igual o superior a la cantidad mínima configurada.
     *
     * @param pedido el pedido sobre el que se evalúa la condición
     * @return {@code true} si {@code pedido.calcularPrecioTotal() >= cantidadMinima};
     *         {@code false} en caso contrario
     */
    @Override
    public boolean esAplicable(Pedido pedido) {
        return pedido.calcularPrecioTotal() >= this.cantidadMinima;
    }

    /**
     * Aplica el descuento porcentual al precio base.
     *
     * <p>Fórmula: {@code precioBase - (precioBase * (porcentaje / 100.0))}</p>
     *
     * @param precioBase precio original antes del descuento
     * @return precio resultante tras restar el porcentaje configurado
     */
    @Override
    public double aplicarDescuento(double precioBase) {
        return precioBase - (precioBase * (this.porcentaje / 100.0));
    }
}