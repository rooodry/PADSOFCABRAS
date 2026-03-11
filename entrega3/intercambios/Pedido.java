package intercambios;

public class Pedido {
    private EstadoPedido estadoPedido;

    public Pedido() {
        this.estadoPedido = EstadoPedido.EN_CARRITO;
    }

    public EstadoPedido getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(EstadoPedido estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    public void setDatos(String datos) {
        /** faltan cosas cuando hagamos pedido bien hecho*/
    }
}