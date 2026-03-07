package descuentos;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import productos.Producto;
import productos.ProductoTienda;

public class DescuentoRegalo extends Descuento { 

    private double gastoMinimo;
    private List<ProductoTienda> productos = new ArrayList<>();

    public DescuentoRegalo(Date fechaInicio, Date fechaFin, double gastoMinimo, List<ProductoTienda> productos) {
        super(fechaInicio, fechaFin);
        this.gastoMinimo = gastoMinimo;
        this.productos = productos;
    } 
    
}
