package descuentos;
import compras.Pedido;
import java.util.Date;

public class DescuentoCantidadGastada extends Descuento { 

    private final double cantidadMinima;
    private final double porcentaje;

    public DescuentoCantidadGastada(Date fechaInicio, Date fechaFin, double cantidadMinima, double porcentaje) {
        super(fechaInicio, fechaFin);
        this.cantidadMinima = cantidadMinima;
        this.porcentaje = porcentaje;
    } 
    
    public double getCantidadMinima() {
        return this.cantidadMinima;
    }
    
    public double getPorcentaje() {
        return this.porcentaje;
    }

    @Override
    public boolean esAplicable(Pedido pedido) {
        return pedido.calcularPrecioTotal() >= this.cantidadMinima;
    }

    @Override
    public double aplicarDescuento(double precioBase) {
        return precioBase - (precioBase * (this.porcentaje / 100.0)); // CAMBIO porque hay que dividir el porcentaje entre 100 para aplicarlo correctamente como decimal
    }
}