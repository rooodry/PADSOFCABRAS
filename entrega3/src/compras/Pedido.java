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
    private ProductoTienda regalo; 

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
    public void setRegalo(ProductoTienda regalo) { this.regalo = regalo; }

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
    public ProductoTienda getRegalo() { return this.regalo; }


    public void cancelar() {
        this.estadoPedido = EstadoPedido.CANCELADO;
    }

    public double calcularPrecioTotal() {
        double subtotal = 0;

        for (Map.Entry<ProductoTienda, Integer> entry : productos.entrySet()) {
            ProductoTienda p = entry.getKey();
            int cantidad = entry.getValue();
            
            // Rebaja de precio del producto (del Gestor)
            double precioUnitario = p.getPrecio();
            if (p.getRebajaPorcentaje() > 0) {
                precioUnitario -= precioUnitario * p.getRebajaPorcentaje();
            } else if (p.getRebajaFija() > 0) {
                precioUnitario -= p.getRebajaFija();
            }
            
            // 2x1 si está activo (del Gestor)
            // Si hay 3 productos en 2x1, solo pagas 2.
            int unidadesAPagar = p.isTiene2x1() ? (cantidad - (cantidad / 2)) : cantidad;
            
            subtotal += unidadesAPagar * precioUnitario;
        }

        // Descuentos Automáticos por Volumen de Gasto
        if (subtotal >= 150) {
            subtotal = subtotal * 0.85; // 15% de descuento
        } else if (subtotal >= 100) {
            subtotal = subtotal * 0.90; // 10% de descuento
        } else if (subtotal >= 50) {
            subtotal = subtotal * 0.95;  // 5% de descuento
        }

        return subtotal;
    }
}