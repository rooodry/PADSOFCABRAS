package descuentos;
import compras.Pedido;
import java.util.Date;

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