package compras;

import java.util.ArrayList;
import java.util.List;
import productos.Producto;
import productos.ProductoSegundaMano;
import compras.Cesta;
import compras.Pedido;
import intercambios.Oferta;
import intercambios.Intercambio;
import utilidades.Status;
import notificaciones.Notificacion;

public class Cesta {
    private List<Producto> productos;
    private int numProductos;


    public Status comprobarStock(Producto producto, int cantidad) {
        if(producto.getStock() >= cantidad) {
            return Status.OK;
        }

        return Status.ERROR;
    }

    public boolean estaVacia() {
        if(numProductos <= 0) {
            return true;
        }

        return false;
    }

    public void limpiarCesta() {
    }

    public void añadirProducto(Producto producto, int cantidad) {
        this.productos.add(producto);
        /*TERMINARKDJAFJASDKLFJLKSADJFKLASJFÑKLJ */
    }

    public void eliminarProducto(Producto producto) {
        this.productos.remove(producto);
    }

}
