package compras;

import java.util.*;

import productos.*;
import usuarios.ClienteRegistrado;
import utilidades.EstadoPedido;
import descuentos.*; 

public class Pedido {
    private final Codigo codigo;
    private final Date fechaRealizacion;
    private Date fechaPago;
    private Date fechaPreparacion;
    private Date fechaRecogida;
    private EstadoPedido estadoPedido;
    private final ClienteRegistrado cliente;
    private Map<ProductoTienda, Integer> productos;
    private Map<ProductoTienda, Integer> valoraciones;
    private Descuento descuento;

    public Pedido(ClienteRegistrado cliente, Map<ProductoTienda, Integer> productos) {
        this.codigo = new Codigo();
        this.fechaRealizacion = new Date();
        this.cliente = cliente;
        this.productos = new HashMap<ProductoTienda, Integer>(productos);
        this.fechaPago = null;
        this.fechaPreparacion = null;
        this.fechaRecogida = null;
        this.estadoPedido = EstadoPedido.EN_CARRITO;
        this.valoraciones = new HashMap<ProductoTienda, Integer>();
        this.descuento = null; 
    }


    //SETTERS//
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
        default:
            break;
        }
    }
    
    public void setFechaRecogida(Date fecha) {this.fechaRecogida = new Date(fecha.getTime());}
    public void setValoracionesProductos(Map<ProductoTienda, Integer> valoraciones) {this.valoraciones = new HashMap<ProductoTienda, Integer>(valoraciones);}
    public void setDescuento(Descuento d) {this.descuento = d;} // NUEVO SETTER


    //GETTERS//
    public Codigo getCodigo() {return this.codigo;}
    public Date getFechaRealizacion() {return new Date(this.fechaRealizacion.getTime());}
    public Date getFechaPago() {return this.fechaPago != null ? new Date(this.fechaPago.getTime()) : null;}
    public Date getFechaPreparacion() {return this.fechaPreparacion != null ? new Date(this.fechaPreparacion.getTime()) : null;}
    public Date getFechaRecogida() {return this.fechaRecogida != null ? new Date(this.fechaRecogida.getTime()) : null;}
    public EstadoPedido getEstadoPedido() {return this.estadoPedido;}
    public ClienteRegistrado getCliente() {return this.cliente;}
    public Map<ProductoTienda, Integer> getProductos() {return new HashMap<ProductoTienda, Integer>(this.productos);}
    public Map<ProductoTienda, Integer> getValoracionesProductos() {return new HashMap<ProductoTienda, Integer>(this.valoraciones);}



    public void cancelar() {
        this.estadoPedido = EstadoPedido.CANCELADO;
    }

    public double calcularPrecioTotal() {

        double total = 0;
    
        // precio base normal (sin descuentos))
        for (Map.Entry<ProductoTienda, Integer> entry : productos.entrySet()) {
            ProductoTienda producto = entry.getKey();
            Integer cantidad = entry.getValue();
            total += producto.getPrecio() * cantidad;
        }

        // descuentos aplicados
        if (this.descuento != null) {
            
            if (this.descuento instanceof DescuentoPorcentaje) {
                DescuentoPorcentaje dp = (DescuentoPorcentaje) this.descuento;
                total = total - (total * (dp.getPorcentaje() / 100.0));
            } 
            else if (this.descuento instanceof DescuentoCantidadGastada) {
                DescuentoCantidadGastada dcg = (DescuentoCantidadGastada) this.descuento;
                if (total >= dcg.getCantidadMinima()) {
                    total = total - (total * (dcg.getPorcentaje() / 100.0));
                }
            }
            else if (this.descuento instanceof DescuentoDosPorUno) {
                // 2X1
                double rebaja = 0;
                for (Map.Entry<ProductoTienda, Integer> entry : productos.entrySet()) {
                    ProductoTienda producto = entry.getKey();
                    Integer cantidad = entry.getValue();
                    int pares = cantidad / 2; 
                    rebaja += pares * producto.getPrecio();
                }
                total -= rebaja;
            }
        }

        return total;
    }
}