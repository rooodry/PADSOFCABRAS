package usuarios;

import java.util.ArrayList;
import java.util.List;
import productos.Producto;
import productos.ProductoSegundaMano;
import productos.ProductoTienda;
import productos.Stock;
import compras.Cesta;
import compras.Pedido;
import intercambios.Oferta;
import intercambios.Intercambio;
import utilidades.Status;
import notificaciones.Notificacion;

public class ClienteRegistrado extends Cliente {
    private String DNI;
    private Cartera cartera;
    private Cesta cesta;
    private List<Pedido> pedidos;
    private List<Notificacion> notificaciones;
    private List<Oferta> ofertasRealizadas;
    private List<Oferta> ofertasRecibidas;
    private List<Intercambio> intercambiosPendientes;


    public ClienteRegistrado(String nombreUsuario, String contraseña, String DNI) {
        super(nombreUsuario, contraseña);
        this.DNI = DNI;
        this.cartera = new Cartera();
        this.cesta = new Cesta();
        this.pedidos = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
        this.ofertasRealizadas = new ArrayList<>();
        this.ofertasRecibidas = new ArrayList<>();
        this.intercambiosPendientes = new ArrayList<>();

    }

    public void añadirALaCesta(ProductoTienda producto, Stock stock) {
        if(stock.getNumProductos(producto) > 0) {
            this.cesta.añadirProducto(producto, 1);
            stock.reducirStock(producto, 1);
        }
    }

    public Status comprar() {
        if(this.cesta.estaVacia()) {
            return Status.ERROR;
        }

        Pedido nuevoPedido = new Pedido(this.cesta);
        this.pedidos.add(nuevoPedido);
        this.cesta.limpiarCesta();

        return Status.OK;
    }

    public Status pagarPedido(Pedido pedido) {
        if(this.pedidos.contains(pedido)) {
            pedido.setEstadoPedido(EstadoPedido.EN_PREPARACION);
            return Status.OK;
        }

        return Status.ERROR;
    }

    public void editarPerfil(String nuevoNombre, String nuevaContraseña) {
        this.setNombreUsuario(nuevoNombre);
        this.setContraseña(nuevaContraseña);
    }

    public void leerNotificaicion(Notificacion notificacion) {
        if (this.notificaciones.contains(notificacion)) {
            notificacion.marcarComoLeida();
        }
    }

    public void borrarNotificacion(Notificacion notificacion) {
        this.notificaciones.remove(notificacion);
    }

    public Status subirProducto(Producto p) {
        if (p instanceof ProductoSegundaMano) {
            ProductoSegundaMano productoSegundaMano = (ProductoSegundaMano) p;
            this.cartera.añadirProducto(productoSegundaMano);
            return Status.OK;
        }
        System.out.println("Solo se pueden subir productos de segunda mano a la cartera.");
        return Status.ERROR;
    }

    public Status pagarValoracion(Producto p) {
        if (p instanceof ProductoSegundaMano) {
            ProductoSegundaMano productoSegundaMano = (ProductoSegundaMano) p;
            
            if(this.cartera.getProductos().contains(productoSegundaMano)) {
                productoSegundaMano.pedirValoracion();
                return Status.OK;
            }
        }
        return Status.ERROR;
    }
    
    public String getDNI() { return DNI; }
    public Cartera getCartera() { return cartera; }
    public Cesta getCarrito() { return cesta; }
    public List<Pedido> getPedidos() { return pedidos; }
    public List<Notificacion> getNotificaciones() { return notificaciones; }

}
