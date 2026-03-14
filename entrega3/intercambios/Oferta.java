package intercambios;

import productos.ProductoSegundaMano;
import usuarios.ClienteRegistrado;
import utilidades.EstadoOferta;
import java.util.List;

public class Oferta {
    
    private EstadoOferta estadoOferta;
    private List<ProductoSegundaMano> productos;
    private ClienteRegistrado usuarioReceptor;
    private ClienteRegistrado usuarioLanzador;

    public Oferta(List<ProductoSegundaMano> productos, ClienteRegistrado uRec, ClienteRegistrado uLanz) {
        this.productos = productos;
        this.estadoOferta = EstadoOferta.PENDIENTE;
        this.usuarioReceptor = uRec;
        this.usuarioLanzador = uLanz;
    }
    
    //SETTERS//
    public void setEstadoOferta(EstadoOferta e) {this.estadoOferta = e;}

    //GETTERS//
    public EstadoOferta getEstadoOferta() {return this.estadoOferta;}
    public List<ProductoSegundaMano> getProductos() {return this.productos;}
    public ClienteRegistrado getUsuarioReceptor() {return this.usuarioReceptor;}
    public ClienteRegistrado getUsuarioLanzador() {return this.usuarioLanzador;}


}
