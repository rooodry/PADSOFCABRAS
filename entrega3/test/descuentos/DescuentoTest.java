package descuentos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

public class DescuentoTest {

    @Test
    public void testConstructorYGetters() {
        Date fechaInicio = new Date(1000000000L); 
        Date fechaFin = new Date(2000000000L);

        Descuento descuento = new Descuento(fechaInicio, fechaFin);

        assertEquals(fechaInicio, descuento.getFechaInicio(), "La fecha de inicio no coincide.");
        assertEquals(fechaFin, descuento.getFechaFin(), "La fecha de fin no coincide.");
    }
}