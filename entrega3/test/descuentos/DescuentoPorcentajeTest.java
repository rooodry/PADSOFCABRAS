package descuentos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

public class DescuentoPorcentajeTest {

    @Test
    public void testConstructorYGetters() {
        Date fechaInicio = new Date();
        Date fechaFin = new Date();
        double porcentaje = 0.20;

        DescuentoPorcentaje descuento = new DescuentoPorcentaje(fechaInicio, fechaFin, porcentaje);

        assertEquals(fechaInicio, descuento.getFechaInicio());
        assertEquals(fechaFin, descuento.getFechaFin());
        assertEquals(porcentaje, descuento.getPorcentaje(), 0.001);
    }
}