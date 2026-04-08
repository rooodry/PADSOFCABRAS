package descuentos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import productos.ProductoTienda;

public class DescuentoRegaloTest {

    @Test
    public void testConstructorYGetters() {
        Date fechaInicio = new Date();
        Date fechaFin = new Date();
        double gastoMinimo = 120.0;
        
        ProductoTienda p1 = new ProductoTienda("Funko Iron Man", "Figura", "iron.jpg");
        List<ProductoTienda> productos = new ArrayList<>();
        productos.add(p1);

        DescuentoRegalo descuento = new DescuentoRegalo(fechaInicio, fechaFin, gastoMinimo, productos);

        assertEquals(fechaInicio, descuento.getFechaInicio());
        assertEquals(fechaFin, descuento.getFechaFin());
        assertEquals(gastoMinimo, descuento.getGastoMinimo(), 0.001);
        
        assertEquals(productos, descuento.getProductos());
    }
    
    @Test
    public void testAplicarDescuento() {
        List<ProductoTienda> productos = new ArrayList<>();
        DescuentoRegalo descuento = new DescuentoRegalo(new Date(), new Date(), 100.0, productos);
        
        assertEquals(500.0, descuento.aplicarDescuento(500.0), 0.001);
    }
}