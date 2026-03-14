package compras;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import productos.ProductoTienda;
import usuarios.ClienteRegistrado;
import utilidades.EstadoPedido;

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
        this.fechaPago = null;
        this.fechaPreparacion = null;
        this.fechaRecogida = null;
        this.estadoPedido = EstadoPedido.EN_PREPARACION;
    }


    //SETTER//
    public void setEstadoPedido(EstadoPedido estadoPedido) {
        this.estadoPedido = estadoPedido;

        switch (estadoPedido) {
        
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
        default:
            break;
        }
    }

    //GETTERS//
    public Codigo getCodigo() {return this.codigo;}

    public Date getFechaRealizacion() {return new Date(this.fechaRealizacion.getTime());}

    public Date getFechaPago() {return new Date(this.fechaPago.getTime());}

    public Date getFechaPreparacion() {return new Date(this.fechaPreparacion.getTime());}

    public Date getFechaRecogida() {return new Date(this.fechaRecogida.getTime());}

    public EstadoPedido getEstadoPedido() {return this.estadoPedido;}

    public ClienteRegistrado getCliente() {return this.cliente;}

    public List<ProductoTienda> getProductos() {return new ArrayList<>(productos);}


    public void cancelar() {
        this.estadoPedido = EstadoPedido.CANCELADO;
    }

    public double calcularPrecioTotal() {
        double total = 0.0;
        for (ProductoTienda p : productos) {
            total += p.getPrecio();
        }
        return total;
    }



}