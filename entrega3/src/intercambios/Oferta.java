package intercambios;

import productos.ProductoSegundaMano;
import usuarios.ClienteRegistrado;
import utilidades.EstadoOferta;
import java.util.*;

public class Oferta {
    
    private EstadoOferta estadoOferta;
    private final ProductoSegundaMano productoOfertado;  
    private final ProductoSegundaMano productoDeseado;     
    private ClienteRegistrado usuarioReceptor;
    private ClienteRegistrado usuarioLanzador;

    public Oferta(ProductoSegundaMano productoOfertado, ProductoSegundaMano productoDeseado, ClienteRegistrado uRec, ClienteRegistrado uLanz) {
        this.productoOfertado = productoOfertado;
        this.productoDeseado  = productoDeseado;        
        this.estadoOferta = EstadoOferta.PENDIENTE;
        this.usuarioReceptor = uRec;
        this.usuarioLanzador = uLanz;
    }
    
    //SETTERS//
    public void setEstadoOferta(EstadoOferta e) {this.estadoOferta = e;}

    //GETTERS//
    public EstadoOferta getEstadoOferta() {return this.estadoOferta;}
    public ProductoSegundaMano getProductoOfertado() {return this.productoOfertado;}
    public ProductoSegundaMano getProductoDeseado() {return this.productoDeseado;}
    public ClienteRegistrado getUsuarioReceptor() {return this.usuarioReceptor;}
    public ClienteRegistrado getUsuarioLanzador() {return this.usuarioLanzador;}


}
