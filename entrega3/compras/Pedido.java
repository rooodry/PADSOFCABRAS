package compras;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import productos.ProductoTienda;
import usuarios.ClienteRegistrado;

public class Pedido {
    private final Codigo codigo;
    private final Date fechaRealizacion;
    private Date fechaPago;
    private Date fechaPreparacion;
    private Date fechaRecogida;
    private EstadoPedido estadoPedido;
    private final ClienteRegistrado cliente;
    private final List<ProductoTienda> productos;

    public Pedido(ClienteRegistrado cliente, List<ProductoTienda> productos) {
        this.codigo = new Codigo();
        this.fechaRealizacion = new Date();
        this.cliente = cliente;
        this.productos = new ArrayList<>(productos);
        this.estadoPedido = EstadoPedido.EN_PREPARACION;
    }

    public double calcularPrecioTotal() {
        double total = 0.0;
        for (ProductoTienda p : productos) {
            total += p.getPrecio();
        }
        return total;
    }

    /* revisar esto bien */
    public void setEstadoPedido(EstadoPedido estadoPedido) {
        this.estadoPedido = estadoPedido;

        case EN_PREPARACION:
            this.fechaPago = new Date();
            break;
        case LISTO:
            this.fechaPreparacion = new Date();
            break;
        case ENTREGADO:
            this.fechaRecogida = new Date();
            break;
        case CANCELADO:
            /* falta */            
            break;
    }

    public void cancelar() {
        this.estadoPedido = EstadoPedido.CANCELADO;
    }

    public Codigo getCodigo() {
        return codigo;
    }

    public Date getFechaRealizacion() {
        return new Date(fechaRealizacion.getTime());
    }

    public Date getFechaPago() {
        return fechaPago != null ? new Date(fechaPago.getTime()) : null;
    }

    public Date getFechaPreparacion() {
        return fechaPreparacion != null ? new Date(fechaPreparacion.getTime()) : null;
    }

    public Date getFechaRecogida() {
        return fechaRecogida != null ? new Date(fechaRecogida.getTime()) : null;
    }

    public EstadoPedido getEstadoPedido() {
        return estadoPedido;
    }

    public ClienteRegistrado getCliente() {
        return cliente;
    }

    public List<ProductoTienda> getProductos() {
        return new ArrayList<>(productos);
    }
}