package descuentos;
import java.util.Date;

public class DescuentoPorcentaje extends Descuento { 

    private double porcentaje;

    public DescuentoPorcentaje(Date fechaInicio, Date fechaFin, double porcentaje) {
        super(fechaInicio, fechaFin);
        this.porcentaje = porcentaje;
    } 
    
}
