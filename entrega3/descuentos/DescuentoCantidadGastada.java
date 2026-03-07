package descuentos;
import java.util.Date;

public class DescuentoCantidadGastada extends Descuento { 

    private double cantidadMinima;
    private double porcentaje;

    public DescuentoCantidadGastada(Date fechaInicio, Date fechaFin, double cantidadMinima, double porcentaje) {
        super(fechaInicio, fechaFin);
        this.cantidadMinima = cantidadMinima;
        this.porcentaje = porcentaje;
    } 
    
}
