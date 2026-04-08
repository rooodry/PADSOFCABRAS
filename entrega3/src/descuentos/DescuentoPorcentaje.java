package descuentos;

import java.util.Date;
import compras.Pedido;

/**
 * Descuento que reduce el precio base en un porcentaje fijo.
 *
 * <p>Este tipo de descuento es siempre aplicable a cualquier pedido,
 * independientemente de su importe o contenido. El porcentaje se
 * almacena como un valor entre 0 y 100 y se convierte a decimal
 * en el momento de aplicar el descuento.</p>
 *
 * <p>Ejemplo: un porcentaje de {@code 20.0} reduce el precio un 20 %.</p>
 */
public class DescuentoPorcentaje extends Descuento {

    /** Porcentaje de reducción, expresado en el rango [0, 100]. */
    private final double porcentaje;

    /**
     * Construye un descuento por porcentaje con el período de vigencia y el
     * porcentaje de reducción indicados.
     *
     * @param fechaInicio fecha de inicio de la vigencia del descuento
     * @param fechaFin    fecha de fin de la vigencia del descuento
     * @param porcentaje  porcentaje de descuento a aplicar (entre 0 y 100)
     */
    public DescuentoPorcentaje(Date fechaInicio, Date fechaFin, double porcentaje) {
        super(fechaInicio, fechaFin);
        this.porcentaje = porcentaje;
    }

    /**
     * Devuelve el porcentaje de descuento configurado.
     *
     * @return porcentaje de reducción en el rango [0, 100]
     */
    public double getPorcentaje() {
        return this.porcentaje;
    }

    /**
     * Indica que este descuento es aplicable a cualquier pedido sin condiciones.
     *
     * @param pedido el pedido evaluado (no se utiliza en esta implementación)
     * @return siempre {@code true}
     */
    @Override
    public boolean esAplicable(Pedido pedido) {
        return true;
    }

    /**
     * Aplica el descuento porcentual al precio base.
     *
     * <p>Fórmula: {@code precioBase - (precioBase * (porcentaje / 100.0))}</p>
     *
     * @param precioBase precio original antes del descuento
     * @return precio resultante tras restar el porcentaje indicado
     */
    @Override
    public double aplicarDescuento(double precioBase) {
        return precioBase - (precioBase * (this.porcentaje / 100.0));
    }
}