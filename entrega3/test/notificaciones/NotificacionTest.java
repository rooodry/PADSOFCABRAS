package notificaciones;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilidades.TipoNotificacion;

import static org.junit.jupiter.api.Assertions.*;

public class NotificacionTest {

    private Notificacion notificacion;
    private TipoNotificacion tipo;
    private String mensaje;

    @BeforeEach
    public void setUp() {
        tipo = TipoNotificacion.PAGO_REALIZADO; 
        mensaje = "Su pedido ha sido enviado.";
        
        // Instanciamos el objeto real a probar
        notificacion = new Notificacion(tipo, mensaje);
    }

    @Test
    public void testConstructorYGetters() {
        assertEquals(tipo, notificacion.getTipoNotificacion(), "El tipo de notificación no coincide.");
        assertEquals(mensaje, notificacion.getMensaje(), "El mensaje de la notificación no coincide.");
        assertFalse(notificacion.getLeida(), "La notificación debería inicializarse como NO leída.");
        assertFalse(notificacion.getBorrada(), "La notificación debería inicializarse como NO borrada.");
    }

    @Test
    public void testSetLeida() {
        assertFalse(notificacion.getLeida(), "El estado inicial de 'leida' debe ser false.");
        notificacion.setLeida();
        assertTrue(notificacion.getLeida(), "El estado de 'leida' debería ser true tras llamar a setLeida().");
    }

    @Test
    public void testSetBorrada() {
        assertFalse(notificacion.getBorrada(), "El estado inicial de 'borrada' debe ser false.");
        notificacion.setBorrada();
        assertTrue(notificacion.getBorrada(), "El estado de 'borrada' debería ser true tras llamar a setBorrada().");
    }
}