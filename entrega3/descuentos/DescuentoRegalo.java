package descuentos;
import java.util.Date;

import productos.Producto;

public class DescuentoRegalo extends Descuento { 

    private double gastoMinimo;
    private Producto productos[];

    public DescuentoRegalo(Date fechaInicio, Date fechaFin, double gastoMinimo, Producto productos[]) {
        super(fechaInicio, fechaFin);
        this.gastoMinimo = gastoMinimo;
        this.productos = productos;
    } 
    
}
