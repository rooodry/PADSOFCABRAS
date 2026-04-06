package descuentos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

public class DescuentoCantidadGastadaTest {

    @Test
    public void testConstructorYGetters() {
        Date fechaInicio = new Date();
        Date fechaFin = new Date();
        double cantidadMinima = 50.5;
        double porcentaje = 0.15;

        DescuentoCantidadGastada descuento = new DescuentoCantidadGastada(fechaInicio, fechaFin, cantidadMinima, porcentaje);

        assertEquals(fechaInicio, descuento.getFechaInicio());
        assertEquals(fechaFin, descuento.getFechaFin());
        
        assertEquals(cantidadMinima, descuento.getCantidadMinima(), 0.001);
        assertEquals(porcentaje, descuento.getPorcentaje(), 0.001);
    }
}