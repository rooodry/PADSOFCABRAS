package descuentos;
import java.util.Date;

public class DescuentoPorcentaje extends Descuento { 

    private final double porcentaje;

    public DescuentoPorcentaje(Date fechaInicio, Date fechaFin, double porcentaje) {
        super(fechaInicio, fechaFin);
        this.porcentaje = porcentaje;
    }

    public double getPorcentaje() {
        return this.porcentaje;
    }
}