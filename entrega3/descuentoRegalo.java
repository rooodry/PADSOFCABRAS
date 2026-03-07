import java.util.Date;

public class descuentoRegalo extends descuento { 

    private double gastoMinimo;
    private producto productos[];

    public descuentoRegalo(Date fechaInicio, Date fechaFin, double gastoMinimo, producto productos[]) {
        super(fechaInicio, fechaFin);
        this.gastoMinimo = gastoMinimo;
        this.productos = productos;
    } 
    
}
