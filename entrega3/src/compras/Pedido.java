package compras;

import java.util.*;

import productos.*;
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
    private Map<ProductoTienda, Integer> productos;

    public Pedido(ClienteRegistrado cliente, Map<ProductoTienda, Integer> productos) {
        this.codigo = new Codigo();
        this.fechaRealizacion = new Date();
        this.cliente = cliente;
        this.productos = new HashMap<ProductoTienda, Integer>(productos);
        this.fechaPago = null;
        this.fechaPreparacion = null;
        this.fechaRecogida = null;
        this.estadoPedido = EstadoPedido.EN_CARRITO;
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
            break;
        default:
            break;
        }
    }

    public void setFechaRecogida(Date fecha) {this.fechaRecogida = fecha;}

    //GETTERS//
    public Codigo getCodigo() {return this.codigo;}
    public Date getFechaRealizacion() {return new Date(this.fechaRealizacion.getTime());}
    /* DEVOLVEMOS LA FECHA DE PAGO O NULL SI EL PEDIDO AÚN NO SE HA PAGADO */
    public Date getFechaPago() {return this.fechaPago != null ? new Date(this.fechaPago.getTime()) : null;}
    /* HACEMOS LO MISMO CON FECHA PREPARACIÓN Y RECOGIDA */
    public Date getFechaPreparacion() {return this.fechaPreparacion != null ? new Date(this.fechaPreparacion.getTime()) : null;}
    public Date getFechaRecogida() {return this.fechaRecogida != null ? new Date(this.fechaRecogida.getTime()) : null;}
    public EstadoPedido getEstadoPedido() {return this.estadoPedido;}
    public ClienteRegistrado getCliente() {return this.cliente;}
    public Map<ProductoTienda, Integer> getProductos() {return new HashMap<ProductoTienda, Integer>(productos);}


    public void cancelar() {
        this.estadoPedido = EstadoPedido.CANCELADO;
    }

    public double calcularPrecioTotal() {

        double total = 0;
    
        for (Map.Entry<ProductoTienda, Integer> entry : productos.entrySet()) {
            ProductoTienda producto = entry.getKey();
            Integer cantidad = entry.getValue();
            
            total += producto.getPrecio() * cantidad;
        }
        
        return total;
    }



}