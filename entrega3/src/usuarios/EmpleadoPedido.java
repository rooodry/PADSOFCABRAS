package usuarios;
 
import java.util.Date;
 
import compras.*;
import utilidades.*;
 
public class EmpleadoPedido extends Empleado {
 
    public EmpleadoPedido(String nombreUsuario, String contraseña) {
        super(nombreUsuario, contraseña);
    }
 
    public void editarPedido(Pedido p, Date nuevaFechaRecogida) {
        p.setFechaRecogida(nuevaFechaRecogida);
    }
 
    public void marcarComoListo(Pedido p) {
        p.setEstadoPedido(EstadoPedido.LISTO);
    }
 
    public void entregarPedido(Pedido p) {
        p.setEstadoPedido(EstadoPedido.ENTREGADO);
    }
}
