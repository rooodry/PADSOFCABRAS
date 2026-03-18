package descuentos;
import java.util.Date;
import java.util.List;


import productos.ProductoTienda;

public class DescuentoRegalo extends Descuento { 

    private double gastoMinimo;
    private List<ProductoTienda> productos;

    public DescuentoRegalo(Date fechaInicio, Date fechaFin, double gastoMinimo, List<ProductoTienda> productos) {
        super(fechaInicio, fechaFin);
        this.gastoMinimo = gastoMinimo;
        this.productos = productos;
    }

    //GETTER//
    public double getGastoMinimo() {return this.gastoMinimo;}
    public List<ProductoTienda> getProductos() {return this.productos;}
    
}
