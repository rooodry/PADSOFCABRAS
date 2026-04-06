package excepciones;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExcepcionesTest {

    @Test
    public void testExcepcionUsuarios() {
        String nombreUsuario = "JuanPerez";
        ExcepcionUsuarios excepcion = new ExcepcionUsuarios(nombreUsuario);
        
        assertTrue(excepcion.toString().contains(nombreUsuario), "El toString debe contener el nombre del usuario.");
    }

    @Test
    public void testExcepcionUsuariosAdmin() {
        String nombreUsuario = "PedroMalo";
        ExcepcionUsuariosAdmin excepcion = new ExcepcionUsuariosAdmin(nombreUsuario);
        
        assertTrue(excepcion.toString().contains(nombreUsuario));
        assertTrue(excepcion.toString().contains("Usuario no es admin"), "Debe indicar que no es admin.");
    }
}