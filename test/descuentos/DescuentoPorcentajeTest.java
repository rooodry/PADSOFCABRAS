package descuentos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

public class DescuentoPorcentajeTest {

    @Test
    public void testConstructorYGetters() {
        Date fechaInicio = new Date();
        Date fechaFin = new Date();
        double porcentaje = 20.0;
        DescuentoPorcentaje descuento = new DescuentoPorcentaje(fechaInicio, fechaFin, porcentaje);

        assertEquals(fechaInicio, descuento.getFechaInicio());
        assertEquals(fechaFin, descuento.getFechaFin());
        assertEquals(porcentaje, descuento.getPorcentaje(), 0.001);
    }
    
    @Test
    public void testAplicarDescuento() {
        DescuentoPorcentaje descuento = new DescuentoPorcentaje(new Date(), new Date(), 20.0); // CAMBIO: de 0.20 a 20.0
        assertEquals(80.0, descuento.aplicarDescuento(100.0), 0.001);
    }
}