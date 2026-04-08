package descuentos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import compras.Pedido;
import productos.ProductoTienda;

/**
 * Descuento que incluye un producto de regalo cuando el pedido supera un
 * gasto mínimo.
 *
 * <p>A diferencia de los descuentos numéricos, este tipo no reduce el precio
 * monetario del pedido: {@link #aplicarDescuento(double)} devuelve el precio
 * base sin modificar. El beneficio consiste en añadir uno de los productos
 * de la lista {@code productos} como regalo, lógica que debe gestionarse
 * externamente (por ejemplo, en {@code Sistema.registrarPedido}).</p>
 *
 * <p>Los accesores devuelven copias defensivas de la lista de productos para
 * evitar modificaciones externas.</p>
 */
public class DescuentoRegalo extends Descuento {

    /** Importe mínimo que debe alcanzar el pedido para activar el regalo. */
    private final double gastoMinimo;

    /** Lista de productos candidatos a ser entregados como regalo. */
    private final List<ProductoTienda> productos;

    /**
     * Construye un descuento de regalo con el período de vigencia, el gasto
     * mínimo y los productos regalo indicados.
     *
     * @param fechaInicio fecha de inicio de la vigencia del descuento
     * @param fechaFin    fecha de fin de la vigencia del descuento
     * @param gastoMinimo importe mínimo del pedido para activar el regalo
     * @param productos   lista de productos candidatos a entregar como regalo;
     *                    se almacena una copia defensiva
     */
    public DescuentoRegalo(Date fechaInicio, Date fechaFin,
                            double gastoMinimo, List<ProductoTienda> productos) {
        super(fechaInicio, fechaFin);
        this.gastoMinimo = gastoMinimo;
        this.productos   = new ArrayList<>(productos);
    }

    /**
     * Devuelve el importe mínimo requerido para activar el regalo.
     *
     * @return gasto mínimo en euros
     */
    public double getGastoMinimo() {
        return this.gastoMinimo;
    }

    /**
     * Devuelve la lista de productos candidatos a ser entregados como regalo.
     *
     * @return copia defensiva de la lista de productos regalo
     */
    public List<ProductoTienda> getProductos() {
        return new ArrayList<>(this.productos);
    }

    /**
     * Indica si el descuento es aplicable al pedido, comprobando que su total
     * sea igual o superior al gasto mínimo configurado.
     *
     * @param pedido el pedido sobre el que se evalúa la condición
     * @return {@code true} si {@code pedido.calcularPrecioTotal() >= gastoMinimo};
     *         {@code false} en caso contrario
     */
    @Override
    public boolean esAplicable(Pedido pedido) {
        return pedido.calcularPrecioTotal() >= this.gastoMinimo;
    }

    /**
     * No modifica el precio base, ya que el beneficio de este descuento es
     * un producto físico de regalo y no una reducción monetaria.
     *
     * @param precioBase precio original antes del descuento
     * @return el mismo {@code precioBase} recibido, sin modificar
     */
    @Override
    public double aplicarDescuento(double precioBase) {
        return precioBase;
    }
}