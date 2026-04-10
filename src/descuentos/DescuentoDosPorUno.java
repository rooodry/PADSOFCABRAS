package descuentos;

import java.util.Date;
import compras.Pedido;

/**
 * Descuento de tipo "2×1" aplicable a cualquier pedido.
 *
 * <p>Este descuento representa una promoción dos por uno. La lógica de
 * reducción del precio (seleccionar el producto más barato como gratuito,
 * dividir el total a la mitad, etc.) <strong>no está implementada</strong>
 * en la versión actual: {@link #aplicarDescuento(double)} devuelve el
 * precio base sin modificar.</p>
 *
 * <p>Al igual que {@link DescuentoPorcentaje}, no impone ninguna condición
 * sobre el pedido y siempre se considera aplicable.</p>
 */
public class DescuentoDosPorUno extends Descuento {

    /**
     * Construye un descuento dos por uno con el período de vigencia indicado.
     *
     * @param fechaInicio fecha de inicio de la vigencia del descuento
     * @param fechaFin    fecha de fin de la vigencia del descuento
     */
    public DescuentoDosPorUno(Date fechaInicio, Date fechaFin) {
        super(fechaInicio, fechaFin);
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
     * Aplica el descuento dos por uno al precio base.
     *
     * <p><b>Nota:</b> la lógica del 2×1 no está implementada actualmente;
     * este método devuelve el {@code precioBase} sin ninguna modificación.</p>
     *
     * @param precioBase precio original antes del descuento
     * @return el mismo {@code precioBase} recibido (pendiente de implementación)
     */
    @Override
    public double aplicarDescuento(double precioBase) {
        return precioBase;
    }
}