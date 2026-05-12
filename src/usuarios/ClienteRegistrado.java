package usuarios;

import java.util.*;
import productos.*;
import compras.*;
import intercambios.*;
import utilidades.*;
import notificaciones.Notificacion;

/**
 * Cliente con cuenta registrada, cesta, pedidos, ofertas e intercambios.
 */
public class ClienteRegistrado extends Cliente {
    private final String DNI;
    private Cartera cartera;
    private Cesta cesta;
    private List<Pedido> pedidos;
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

    public String getDNI() { return this.DNI; }
    public Cartera getCartera() { return this.cartera; }
    public Cesta getCesta() { return this.cesta; }
    public List<Pedido> getPedidos() { return new ArrayList<>(this.pedidos); }
    public List<Oferta> getOfertasRealizadas() { return new ArrayList<>(this.ofertasRealizadas); }
    public List<Oferta> getOfertasRecibidas() { return new ArrayList<>(this.ofertasRecibidas); }
    public List<Intercambio> getIntercambios() { return new ArrayList<>(this.intercambios); }
    public List<Codigo> getCodigos() { return new ArrayList<>(this.codigos); }

    public Status comprar() {
        if(this.cesta.estaVacia()) {
            return Status.ERROR;
        }

        Pedido nuevoPedido = new Pedido(this, productosPedidoDesdeCesta());
        this.pedidos.add(nuevoPedido);
        this.cesta.limpiarCesta();

        return Status.OK;
    }

    private Map<ProductoTienda, Integer> productosPedidoDesdeCesta() {
        Map<ProductoTienda, Integer> productosPedido = this.cesta.getProductos();

        for (Map.Entry<Pack, Integer> entry : this.cesta.getPacks().entrySet()) {
            Pack pack = entry.getKey();
            ProductoTienda lineaPack = new ProductoTienda(pack.getNombre(), resumenPack(pack), "");
            lineaPack.setPrecio(pack.getPrecio());
            productosPedido.put(lineaPack, entry.getValue());
        }

        return productosPedido;
    }

    private String resumenPack(Pack pack) {
        StringBuilder texto = new StringBuilder("Pack: ");
        for (ProductoTienda producto : pack.getProductos()) {
            if (texto.length() > "Pack: ".length()) {
                texto.append(" + ");
            }
            texto.append(producto.getNombre());
        }
        for (Pack subpack : pack.getSubpacks()) {
            if (texto.length() > "Pack: ".length()) {
                texto.append(" + ");
            }
            texto.append(subpack.getNombre());
        }
        return texto.toString();
    }


    public void leerNotificacion(Notificacion notificacion) {
        if (this.getNotificaciones().contains(notificacion)) {
        	notificacion.setLeida();
        }
    }

    public void borrarNotificacion(Notificacion notificacion) {
        this.removeNotificacion(notificacion);
    }

    public Status subirProducto(ProductoSegundaMano p) {
        this.cartera.añadirProducto(p);
        return Status.OK;
    }

    public void añadirValoraciones(Pedido p, Map<ProductoTienda, Integer> lista) {
        p.setValoracionesProductos(lista);
    }
}
