package usuarios;

import compras.Pedido;
import compras.EstadoPedido;

public class EmpleadoPedido extends Empleado {

    public EmpleadoPedido(String nombreUsuario, String contraseña) {
        super(nombreUsuario, contraseña);
    }

    public void editarPedido(Pedido p) {
        /* necesito que hagamos la clase Pedido por completo antes de poder terminar la 
         función esta. */
    }

    public void marcarComoListo(Pedido p) {
        p.setEstadoPedido(EstadoPedido.LISTO);
    }

    public void entregarPedido(Pedido p) {
        p.setEstadoPedido(EstadoPedido.ENTREGADO);
    }
}
