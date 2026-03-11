package intercambios;

import productos.ProductoSegundaMano;
import utilidades.EstadoOferta;
import java.util.ArrayList;
import java.util.List;

public class Oferta {
    
    private EstadoOferta estadoOferta;
    private List<ProductoSegundaMano> productos;

    public Oferta(List<ProductoSegundaMano> productos) {
        this.productos = productos;
        this.estadoOferta = EstadoOferta.PENDIENTE;
    }
    
    //SETTERS//
    public void setEstadoOferta(EstadoOferta e) {this.estadoOferta = e;}

    //GETTERS//
    public EstadoOferta getEstadoOferta() {return this.estadoOferta;}
    public List<ProductoSegundaMano> getProductos() {return this.productos;}

}
