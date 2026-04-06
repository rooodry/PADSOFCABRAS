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
            if(this.fechaRecogida == null) {
                this.fechaRecogida = new Date();
            }
            break;
        default:
            break;
        }
    }
    
    public void setFechaRecogida(Date fecha) {
        if (fecha != null) {
            this.fechaRecogida = new Date(fecha.getTime());
        }
    }
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
            
            subtotal += cantidad * precioUnitario;
        }

        // Aplicar descuento explícito si está asignado
        if (descuento != null) {
            if (descuento instanceof DescuentoPorcentaje) {
                double pct = ((DescuentoPorcentaje) descuento).getPorcentaje();
                subtotal = subtotal * (1.0 - pct / 100.0);

            } else if (descuento instanceof DescuentoCantidadGastada) {
                DescuentoCantidadGastada dg = (DescuentoCantidadGastada) descuento;
                if (subtotal >= dg.getCantidadMinima()) {
                    subtotal = subtotal * (1.0 - dg.getPorcentaje() / 100.0);
                }

            } else if (descuento instanceof DescuentoDosPorUno) {
                // 2x1: por cada 2 unidades del mismo producto, una es gratis.
                // Se recalcula el subtotal aplicando la lógica 2x1 globalmente.
                double subtotal2x1 = 0;
                for (Map.Entry<ProductoTienda, Integer> entry : productos.entrySet()) {
                    ProductoTienda p = entry.getKey();
                    int cantidad = entry.getValue();
                    double precioUnitario = p.getPrecio();
                    if (p.getRebajaPorcentaje() > 0) {
                        precioUnitario -= precioUnitario * p.getRebajaPorcentaje();
                    } else if (p.getRebajaFija() > 0) {
                        precioUnitario -= p.getRebajaFija();
                    }
                    int unidadesAPagar = cantidad - (cantidad / 2);
                    subtotal2x1 += unidadesAPagar * precioUnitario;
                }
                subtotal = subtotal2x1;

            }
               // DescuentoRegalo: no modifica el precio total (el regalo lo gestiona el sistema)
        }

        return subtotal;
    }
}