package usuarios;

import notificaciones.Notificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilidades.TipoNotificacion;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario("PepeKite", "password123");
    }

    @Test
    public void testConstructorYGetters() {
        assertEquals("PepeKite", usuario.getNombre());
        assertEquals("password123", usuario.getContraseña());
        assertNotNull(usuario.getNotificaciones());
        assertTrue(usuario.getNotificaciones().isEmpty());
    }

    @Test
    public void testSetters() {
        usuario.setNombreUsuario("KitePepe");
        usuario.setContraseña("newPass");

        assertEquals("KitePepe", usuario.getNombre());
        assertEquals("newPass", usuario.getContraseña());
    }

    @Test
    public void testAddNotificacion() {
        Notificacion notificacion = new Notificacion(TipoNotificacion.PAGO_REALIZADO, "Mensaje de prueba");
        
        usuario.addNotificacion(notificacion);

        assertEquals(1, usuario.getNotificaciones().size());
        assertTrue(usuario.getNotificaciones().contains(notificacion));
    }
}