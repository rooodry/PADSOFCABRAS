package descuentos;
import java.util.Date;
import compras.Pedido;

public class DescuentoPorcentaje extends Descuento { 

    private final double porcentaje;

    public DescuentoPorcentaje(Date fechaInicio, Date fechaFin, double porcentaje) {
        super(fechaInicio, fechaFin);
        this.porcentaje = porcentaje;
    }

    public double getPorcentaje() {
        return this.porcentaje;
    }

    @Override
    public boolean esAplicable(Pedido pedido) {
        return true; 
    }

    @Override
    public double aplicarDescuento(double precioBase) {
        return precioBase - (precioBase * (this.porcentaje / 100.0)); // CAMBIO porque hay que dividir el porcentaje entre 100 para aplicarlo correctamente como decimal
    }
}