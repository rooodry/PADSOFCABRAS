package usuarios;

import java.util.*;
import productos.*;
import compras.*;
import intercambios.*;
import utilidades.*;
import notificaciones.Notificacion;

public class ClienteRegistrado extends Cliente {
    private final String DNI;
    private Cartera cartera;
    private Cesta cesta;
    private List<Pedido> pedidos;
    private List<Notificacion> notificaciones;
    private List<Oferta> ofertasRealizadas;
    private List<Oferta> ofertasRecibidas;
    private List<Intercambio> intercambios;
    private List<Codigo> codigos;


    public ClienteRegistrado(String nombreUsuario, String contraseña, String DNI) {
        super(nombreUsuario, contraseña);
        this.DNI = DNI;
        this.cartera = new Cartera();
        this.cesta = new Cesta();
        this.pedidos = new ArrayList<Pedido>();
        this.notificaciones = new ArrayList<Notificacion>();
        this.ofertasRealizadas = new ArrayList<Oferta>();
        this.ofertasRecibidas = new ArrayList<Oferta>();
        this.intercambios = new ArrayList<Intercambio>();
        this.codigos = new ArrayList<Codigo>();
    }

    //SETTERS//
    public void editarPerfil(String nuevoNombre, String nuevaContraseña) {
        this.setNombreUsuario(nuevoNombre);
        this.setContraseña(nuevaContraseña);
    }
    public void añadirALaCesta(ProductoTienda producto, Stock stock) {
        if(stock.getNumProductos(producto) > 0) {
            this.cesta.añadirProducto(producto, 1);
            stock.reducirStock(producto, 1);
        }
    }
    public void addCodigo(Codigo c) {this.codigos.add(c);}

    public void addNotificacion(Notificacion n) {
        this.notificaciones.add(n);
    }

    //GETTERS//
    public String getDNI() {return this.DNI;}
    public Cartera getCartera() {return this.cartera;}
    public Cesta getCesta() {return this.cesta;}
    public List<Pedido> getPedidos() {return new ArrayList<>(this.pedidos);}
    public List<Notificacion> getNotificaciones() {return new ArrayList<>(this.notificaciones);}
    public List<Oferta> getOfertasRealizadas() {return new ArrayList<>(this.ofertasRealizadas);}
    public List<Oferta> getOfertasRecibidas() {return new ArrayList<>(this.ofertasRecibidas);}
    public List<Intercambio> getIntercambios() {return new ArrayList<>(this.intercambios);}
    public List<Codigo> getCodigos() {return new ArrayList<>(this.codigos);}

    public Status comprar() {
        if(this.cesta.estaVacia()) {
            return Status.ERROR;
        }

        Pedido nuevoPedido = new Pedido(this, this.cesta.getProductos()); 
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


    public void leerNotificacion(Notificacion notificacion) {
        if (this.notificaciones.contains(notificacion)) {
            notificacion.setLeida();
        }
    }

    public void borrarNotificacion(Notificacion notificacion) {
        this.notificaciones.remove(notificacion);
    }

    public Status subirProducto(ProductoSegundaMano p) {
        this.cartera.añadirProducto(p);
        return Status.OK;
    }

    public Status pagarValoracion(ProductoSegundaMano p) {
        if(this.cartera.getProductos().contains(p)) {
            p.pedirValoracion();
            return Status.OK;
        }
        return Status.ERROR;
    }

    public void añadirValoraciones(Pedido p, Map<ProductoTienda, Integer> lista) {p.setValoracionesProductos(lista);}
    


}