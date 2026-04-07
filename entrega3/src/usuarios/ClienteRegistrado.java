package usuarios;

import java.util.*;
import productos.*;
import compras.*;
import intercambios.*;
import utilidades.*;
import notificaciones.Notificacion;
import es.uam.eps.padsof.telecard.*;

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
        this.pedidos = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
        this.ofertasRealizadas = new ArrayList<>();
        this.ofertasRecibidas = new ArrayList<>();
        this.intercambios = new ArrayList<>();
        this.codigos = new ArrayList<>();
    }

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

    public void addCodigo(Codigo c) { this.codigos.add(c); }

    public void addNotificacion(Notificacion n) {
        this.notificaciones.add(n);
    }

    public String getDNI() { return this.DNI; }
    public Cartera getCartera() { return this.cartera; }
    public Cesta getCesta() { return this.cesta; }
    public List<Pedido> getPedidos() { return new ArrayList<>(this.pedidos); }
    public List<Notificacion> getNotificaciones() { return new ArrayList<>(this.notificaciones); }
    public List<Oferta> getOfertasRealizadas() { return new ArrayList<>(this.ofertasRealizadas); }
    public List<Oferta> getOfertasRecibidas() { return new ArrayList<>(this.ofertasRecibidas); }
    public List<Intercambio> getIntercambios() { return new ArrayList<>(this.intercambios); }
    public List<Codigo> getCodigos() { return new ArrayList<>(this.codigos); }

    public Status comprar() {
        if(this.cesta.estaVacia()) {
            return Status.ERROR;
        }

        Pedido nuevoPedido = new Pedido(this, this.cesta.getProductos()); 
        this.pedidos.add(nuevoPedido);
        this.cesta.limpiarCesta();

        return Status.OK;
    }

    public Status pagarPedido(Pedido pedido, String numeroTarjeta) {
        if(!this.pedidos.contains(pedido)) {
            return Status.ERROR;
        }

        try {
            if(!TeleChargeAndPaySystem.isValidCardNumber(numeroTarjeta)) {
                System.out.println("El número de tarjeta no es válido.");
                return Status.ERROR;
            }

            TeleChargeAndPaySystem.charge(
                numeroTarjeta, 
                "Pago de pedido", 
                pedido.calcularPrecioTotal(), 
                true 
            );

            pedido.setEstadoPedido(EstadoPedido.EN_PREPARACION);
            return Status.OK;

        } catch (InvalidCardNumberException e) {
            System.err.println("Error: El número de tarjeta es inválido.");
            return Status.ERROR;
        } catch (FailedInternetConnectionException e) {
            System.err.println("Error: Fallo de conexión a internet al pagar.");
            return Status.ERROR;
        } catch (OrderRejectedException e) {
            System.err.println("Error: El pago del pedido ha sido rechazado.");
            return Status.ERROR;
        }
    }

    public Status pagarValoracion(ProductoSegundaMano p, String numeroTarjeta) {
        if(!this.cartera.getProductos().contains(p)) {
            return Status.ERROR;
        }

        try {
            if(!TeleChargeAndPaySystem.isValidCardNumber(numeroTarjeta)) {
                System.out.println("El número de tarjeta no es válido.");
                return Status.ERROR;
            }

            TeleChargeAndPaySystem.charge(
                numeroTarjeta, 
                "Pago por tasacion de producto", 
                10.0, 
                true 
            );

            p.pedirValoracion();
            return Status.OK;

        } catch (InvalidCardNumberException e) {
            System.err.println("Error: El número de tarjeta es inválido.");
            return Status.ERROR;
        } catch (FailedInternetConnectionException e) {
            System.err.println("Error: Fallo de conexión a internet al pagar.");
            return Status.ERROR;
        } catch (OrderRejectedException e) {
            System.err.println("Error: El pago de la tasación ha sido rechazado.");
            return Status.ERROR;
        }
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

    public void añadirValoraciones(Pedido p, Map<ProductoTienda, Integer> lista) {
        p.setValoracionesProductos(lista);
    }
}