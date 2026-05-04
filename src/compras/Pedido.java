package compras;

import java.io.Serializable;
import java.util.*;

import productos.*;
import usuarios.ClienteRegistrado;
import utilidades.EstadoPedido;
import descuentos.*; 

/**
 * Representa un pedido formalizado por un cliente en el sistema.
 * Gestiona el ciclo de vida del pedido, sus productos, descuentos aplicables y el cálculo del importe.
 */
public class Pedido implements Serializable {

    private static final long serialVersionUID = 1L;
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

    /**
     * Constructor de la clase Pedido.
     * Genera automáticamente un código único y la fecha de realización, estableciendo el estado inicial.
     * * @param cliente   Cliente registrado que realiza el pedido.
     * @param productos Mapa con los productos adquiridos y sus cantidades.
     */
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

    /**
     * Establece el nuevo estado del pedido y actualiza las fechas correspondientes
     * de pago, preparación o recogida según el estado asignado.
     * * @param estadoPedido Nuevo estado del pedido.
     */
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
    
    /**
     * Establece manualmente la fecha en la que el pedido fue recogido.
     * * @param fecha Fecha de recogida.
     */
    public void setFechaRecogida(Date fecha) {
        if (fecha != null) {
            this.fechaRecogida = new Date(fecha.getTime());
        }
    }

    /**
     * Asigna las valoraciones realizadas por el cliente a los productos de este pedido.
     * * @param valoraciones Mapa que relaciona cada producto con su valoración numérica.
     */
    public void setValoracionesProductos(Map<ProductoTienda, Integer> valoraciones) {
        this.valoraciones = new HashMap<ProductoTienda, Integer>(valoraciones);
    }

    /**
     * Aplica un descuento general al pedido.
     * * @param d Objeto Descuento a aplicar.
     */
    public void setDescuento(Descuento d) {
        this.descuento = d;
    }

    /**
     * Asigna un producto adicional como regalo dentro del pedido.
     * * @param regalo Objeto ProductoTienda entregado como regalo.
     */
    public void setRegalo(ProductoTienda regalo) { 
        this.regalo = regalo; 
    }

    /**
     * Obtiene el código identificador único del pedido.
     * * @return Objeto Codigo asociado.
     */
    public Codigo getCodigo() {return this.codigo;}

    /**
     * Obtiene la fecha en la que se generó el pedido.
     * * @return Copia de la fecha de realización.
     */
    public Date getFechaRealizacion() {return new Date(this.fechaRealizacion.getTime());}

    /**
     * Obtiene la fecha en la que se abonó el pedido.
     * * @return Copia de la fecha de pago, o null si aún no se ha pagado.
     */
    public Date getFechaPago() {return this.fechaPago != null ? new Date(this.fechaPago.getTime()) : null;}

    /**
     * Obtiene la fecha en la que el pedido fue preparado.
     * * @return Copia de la fecha de preparación, o null si no está preparado.
     */
    public Date getFechaPreparacion() {return this.fechaPreparacion != null ? new Date(this.fechaPreparacion.getTime()) : null;}

    /**
     * Obtiene la fecha en la que el cliente recogió el pedido.
     * * @return Copia de la fecha de recogida, o null si no ha sido recogido.
     */
    public Date getFechaRecogida() {return this.fechaRecogida != null ? new Date(this.fechaRecogida.getTime()) : null;}

    /**
     * Obtiene el estado actual en el flujo del pedido.
     * * @return Enum EstadoPedido.
     */
    public EstadoPedido getEstadoPedido() {return this.estadoPedido;}

    /**
     * Obtiene el cliente que realizó la compra.
     * * @return Objeto ClienteRegistrado.
     */
    public ClienteRegistrado getCliente() {return this.cliente;}

    /**
     * Obtiene los productos y sus cantidades asociados al pedido.
     * * @return Copia del mapa de productos.
     */
    public Map<ProductoTienda, Integer> getProductos() {return new HashMap<ProductoTienda, Integer>(this.productos);}

    /**
     * Obtiene las valoraciones realizadas a los productos del pedido.
     * * @return Copia del mapa de valoraciones.
     */
    public Map<ProductoTienda, Integer> getValoracionesProductos() {return new HashMap<ProductoTienda, Integer>(this.valoraciones);}

    /**
     * Obtiene el producto asignado como regalo, si lo hubiera.
     * * @return Objeto ProductoTienda o null.
     */
    public ProductoTienda getRegalo() { return this.regalo; }

    /**
     * Cancela el pedido, modificando su estado a CANCELADO.
     */
    public void cancelar() {
        this.estadoPedido = EstadoPedido.CANCELADO;
    }

    /**
     * Calcula el importe total del pedido teniendo en cuenta los precios base, 
     * las rebajas individuales de los productos y el descuento global aplicado.
     * * @return Valor double con el precio total a pagar.
     */
    public double calcularPrecioTotal() {
        double subtotal = 0;

        for (Map.Entry<ProductoTienda, Integer> entry : productos.entrySet()) {
            ProductoTienda p = entry.getKey();
            int cantidad = entry.getValue();
            
            double precioUnitario = p.getPrecio();
            if (p.getRebajaPorcentaje() > 0) {
                precioUnitario -= precioUnitario * p.getRebajaPorcentaje();
            } else if (p.getRebajaFija() > 0) {
                precioUnitario -= p.getRebajaFija();
            }
            
            int unidadesAPagar = p.isTiene2x1() ? cantidad - (cantidad / 2) : cantidad;
            subtotal += unidadesAPagar * precioUnitario;
        }

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
        }

        return subtotal;
    }
}
