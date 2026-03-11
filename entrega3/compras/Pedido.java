package compras;

public class Pedido {
    private EstadoPedido estadoPedido;

    public Pedido() {
    }

    public EstadoPedido getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(EstadoPedido estadoPedido) {
        this.estadoPedido = estadoPedido;
    }
}