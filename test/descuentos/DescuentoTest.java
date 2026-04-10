package descuentos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import compras.Pedido;

import java.util.Date;

public class DescuentoTest {

    @Test
    public void testConstructorYGetters() {
        Date fechaInicio = new Date(1000000000L); 
        Date fechaFin = new Date(2000000000L);

        Descuento descuento = new Descuento(fechaInicio, fechaFin) {
            @Override
            public boolean esAplicable(Pedido pedido) {
                return true;
            }

            @Override
            public double aplicarDescuento(double precioBase) {
                return precioBase;
            }
        };

        assertEquals(fechaInicio, descuento.getFechaInicio(), "La fecha de inicio no coincide.");
        assertEquals(fechaFin, descuento.getFechaFin(), "La fecha de fin no coincide.");
    }
}