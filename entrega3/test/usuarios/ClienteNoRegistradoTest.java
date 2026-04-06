package usuarios;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClienteNoRegistradoTest {

    @Test
    public void testCreacionClienteNoRegistrado() {
        ClienteNoRegistrado anonimo = new ClienteNoRegistrado("invitado", "nada");
        
        assertEquals("invitado", anonimo.getNombre());
        assertEquals("nada", anonimo.getContraseña());
    }
}