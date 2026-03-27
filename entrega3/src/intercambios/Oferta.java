package intercambios;

import productos.ProductoSegundaMano;
import usuarios.ClienteRegistrado;
import utilidades.EstadoOferta;
import java.util.*;

public class Oferta {
    
    private EstadoOferta estadoOferta;
    private Map<ProductoSegundaMano, Integer> productos;
    private ClienteRegistrado usuarioReceptor;
    private ClienteRegistrado usuarioLanzador;

    public Oferta(Map<ProductoSegundaMano, Integer> productos, ClienteRegistrado uRec, ClienteRegistrado uLanz) {
        this.productos = new HashMap<ProductoSegundaMano, Integer>(productos);
        this.estadoOferta = EstadoOferta.PENDIENTE;
        this.usuarioReceptor = uRec;
        this.usuarioLanzador = uLanz;
    }
    
    //SETTERS//
    public void setEstadoOferta(EstadoOferta e) {this.estadoOferta = e;}

    //GETTERS//
    public EstadoOferta getEstadoOferta() {return this.estadoOferta;}
    public Map<ProductoSegundaMano, Integer> getProductos() {return new HashMap<ProductoSegundaMano, Integer>(this.productos);}
    public ClienteRegistrado getUsuarioReceptor() {return this.usuarioReceptor;}
    public ClienteRegistrado getUsuarioLanzador() {return this.usuarioLanzador;}


}
