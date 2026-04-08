package intercambios;

import productos.ProductoSegundaMano;
import usuarios.ClienteRegistrado;
import utilidades.EstadoOferta;

/**
 * Representa una propuesta de intercambio de productos de segunda mano 
 * lanzada por un usuario hacia otro.
 */
public class Oferta {
    
    private EstadoOferta estadoOferta;
    private final ProductoSegundaMano productoOfertado;  
    private final ProductoSegundaMano productoDeseado;     
    private ClienteRegistrado usuarioReceptor;
    private ClienteRegistrado usuarioLanzador;

    /**
     * Constructor de la clase Oferta.
     * Inicializa la oferta en estado pendiente.
     *
     * @param productoOfertado El producto que el lanzador ofrece entregar.
     * @param productoDeseado  El producto del receptor que el lanzador desea obtener.
     * @param uRec             Usuario que recibe y debe responder a la oferta.
     * @param uLanz            Usuario que inicia la oferta.
     */
    public Oferta(ProductoSegundaMano productoOfertado, ProductoSegundaMano productoDeseado, ClienteRegistrado uRec, ClienteRegistrado uLanz) {
        this.productoOfertado = productoOfertado;
        this.productoDeseado  = productoDeseado;        
        this.estadoOferta = EstadoOferta.PENDIENTE;
        this.usuarioReceptor = uRec;
        this.usuarioLanzador = uLanz;
    }
    
    /**
     * Cambia el estado en el que se encuentra la oferta.
     * @param e Nuevo estado de la oferta.
     */
    public void setEstadoOferta(EstadoOferta e) {
        this.estadoOferta = e;
    }

    /**
     * Obtiene el estado actual de la oferta.
     * @return Enum EstadoOferta indicando si está pendiente, aceptada, etc.
     */
    public EstadoOferta getEstadoOferta() {
        return this.estadoOferta;
    }

    /**
     * Obtiene el producto que se está ofreciendo en el intercambio.
     * @return Objeto ProductoSegundaMano ofrecido.
     */
    public ProductoSegundaMano getProductoOfertado() {
        return this.productoOfertado;
    }

    /**
     * Obtiene el producto que se desea conseguir.
     * @return Objeto ProductoSegundaMano deseado.
     */
    public ProductoSegundaMano getProductoDeseado() {
        return this.productoDeseado;
    }

    /**
     * Obtiene el cliente que recibe la propuesta de intercambio.
     * @return Objeto ClienteRegistrado receptor.
     */
    public ClienteRegistrado getUsuarioReceptor() {
        return this.usuarioReceptor;
    }

    /**
     * Obtiene el cliente que inició la propuesta de intercambio.
     * @return Objeto ClienteRegistrado lanzador.
     */
    public ClienteRegistrado getUsuarioLanzador() {
        return this.usuarioLanzador;
    }
}