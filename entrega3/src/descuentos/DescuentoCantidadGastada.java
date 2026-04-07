package descuentos;
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
}