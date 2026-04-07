package descuentos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

public class DescuentoDosPorUnoTest {

    @Test
    public void testConstructorYGetters() {
        Date fechaInicio = new Date();
        Date fechaFin = new Date();

        DescuentoDosPorUno descuento = new DescuentoDosPorUno(fechaInicio, fechaFin);

        assertEquals(fechaInicio, descuento.getFechaInicio());
        assertEquals(fechaFin, descuento.getFechaFin());
    }
    
    @Test
    public void testAplicarDescuento() {
        DescuentoDosPorUno descuento = new DescuentoDosPorUno(new Date(), new Date());
        
        assertEquals(100.0, descuento.aplicarDescuento(100.0), 0.001);
    }
}