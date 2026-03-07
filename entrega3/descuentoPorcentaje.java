import java.util.Date;

public class descuentoPorcentaje extends descuento { 

    private double porcentaje;

    public descuentoPorcentaje(Date fechaInicio, Date fechaFin, double porcentaje) {
        super(fechaInicio, fechaFin);
        this.porcentaje = porcentaje;
    } 
    
}
