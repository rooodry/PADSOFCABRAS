package usuarios;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utilidades.TiposEmpleado;
import productos.ProductoSegundaMano;

import static org.junit.jupiter.api.Assertions.*;

public class EmpleadoTest {

    private Empleado empleado;

    @BeforeEach
    public void setUp() {
        empleado = new Empleado("emp1", "pass123");
    }

    @Test
    public void testConstructorYGetters() {
        assertEquals("emp1", empleado.getNombre());
        assertEquals("pass123", empleado.getContraseña());
        assertTrue(empleado.getPermisos().isEmpty());
        assertTrue(empleado.getProductosParaValorar().isEmpty());
        assertTrue(empleado.getIntercambios().isEmpty());
    }

    @Test
    public void testGestionPermisos() {
        empleado.addPermiso(TiposEmpleado.EMPLEADOS_PRODUCTO);
        assertTrue(empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO));
        assertEquals(1, empleado.getPermisos().size());
        
        empleado.removePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO);
        assertFalse(empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO));
        
        empleado.addPermiso(TiposEmpleado.EMPLEADOS_INTERCAMBIO);
        empleado.clearPermisos();
        assertTrue(empleado.getPermisos().isEmpty());
    }

    @Test
    public void testAddProductoParaValorar() {
        ClienteRegistrado cliente = new ClienteRegistrado("user1", "pass", "12345678A");
        ProductoSegundaMano p = new ProductoSegundaMano("Reloj", "Reloj antiguo", "img.png", cliente);
        
        empleado.addProductoParaValorar(p);
        assertEquals(1, empleado.getProductosParaValorar().size());
        assertTrue(empleado.getProductosParaValorar().contains(p));
    }
}