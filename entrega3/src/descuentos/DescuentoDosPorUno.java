package descuentos;
import java.util.Date;
import compras.Pedido;

public class DescuentoDosPorUno extends Descuento { 

    public DescuentoDosPorUno(Date fechaInicio, Date fechaFin) {
        super(fechaInicio, fechaFin);
    } 

    @Override
    public boolean esAplicable(Pedido pedido) {
        return true; 
    }

    @Override
    public double aplicarDescuento(double precioBase) {
        return precioBase; 
    }
}