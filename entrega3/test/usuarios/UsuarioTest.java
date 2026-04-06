package usuarios;

import notificaciones.Notificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        usuario = new Usuario("PepeKite", "password123");
    }

    @Test
    public void testConstructorYGetters() {
        assertEquals("PepeKite", usuario.getNombre(), "El nombre de usuario no coincide.");
        assertEquals("password123", usuario.getContraseña(), "La contraseña no coincide.");
        assertNotNull(usuario.getNotificaciones(), "La lista de notificaciones no debe ser null.");
        assertTrue(usuario.getNotificaciones().isEmpty(), "La lista de notificaciones debe inicializarse vacía.");
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
        // Pasamos null como tipo porque no tengo tu Enum TipoNotificacion real
        Notificacion notificacion = new Notificacion(null, "Mensaje de prueba");
        
        usuario.addNotificacion(notificacion);

        assertEquals(1, usuario.getNotificaciones().size(), "Debería haber 1 notificación.");
        assertTrue(usuario.getNotificaciones().contains(notificacion), "La lista no contiene la notificación añadida.");
    }
}