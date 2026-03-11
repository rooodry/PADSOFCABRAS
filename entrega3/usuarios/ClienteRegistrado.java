package usuarios;

import java.util.ArrayList;
import java.util.List;
import productos.Producto;
import productos.ProductoSegundaMano;
import compras.Cesta;
import compras.Pedido;
import intercambios.Oferta;
import intercambios.Intercambio;
import utilidades.Status; // El enum de status { ERROR, OK }
import notificaciones.Notificacion;

/**
 * CLIENTE_REGISTRADO
--
-DNI: String
-
+ añadirALaCesta(p: Producto): void
+ comprar(): status
+ pagarPedido(p): status
+ editarPerfil(): void
+ leerNotificaicion(n: Notificacion): void
+ borrarNotificacion(n: Notificacion): void
+ subirProducto(Producto: p): status
+ pagarValoracion(Producto: p): status
 */
public class ClienteRegistrado {
    private String DNI;

    public ClienteRegistrado(String DNI) {
        this.DNI = DNI;
    }

    public void añadirALaCesta(Producto producto) {
        this.productos.add(producto);
    }

    public status comprar() {

    }

    public status pagarPedido(Pedido pedido) {

    }

    public void editarPerfil() {

    }

    public void leerNotificaicion(Notificacion notificacion) {

    }

    public void borrarNotificaicion(Notificacion notificacion) {

    }

    public status subirProducto(Producto producto) {

    }

    public status pagarValoracion(Producto producto) {

    }


}
