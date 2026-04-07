package descuentos;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import productos.ProductoTienda;

public class DescuentoRegalo extends Descuento { 

    private final double gastoMinimo;
    private final List<ProductoTienda> productos;

    public DescuentoRegalo(Date fechaInicio, Date fechaFin, double gastoMinimo, List<ProductoTienda> productos) {
        super(fechaInicio, fechaFin);
        this.gastoMinimo = gastoMinimo;
        this.productos = new ArrayList<>(productos);
    }

    public double getGastoMinimo() {
        return this.gastoMinimo;
    }
    
    public List<ProductoTienda> getProductos() {
        return new ArrayList<>(this.productos);
    }
}