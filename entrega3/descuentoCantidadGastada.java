import java.util.Date;

public class descuentoCantidadGastada extends descuento { 

    private double cantidadMinima;
    private double porcentaje;

    public descuentoCantidadGastada(Date fechaInicio, Date fechaFin, double cantidadMinima, double porcentaje) {
        super(fechaInicio, fechaFin);
        this.cantidadMinima = cantidadMinima;
        this.porcentaje = porcentaje;
    } 
    
}
