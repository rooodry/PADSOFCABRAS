package compras;

import utilidades.EstadoPedido;

public class Pedido {
    private EstadoPedido estadoPedido;

    public Pedido(Cesta cesta) {
    }

    public EstadoPedido getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(EstadoPedido estadoPedido) {
        this.estadoPedido = estadoPedido;
    }
}