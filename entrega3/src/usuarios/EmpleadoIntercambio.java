package usuarios;

import productos.*;
import notificaciones.Notificacion;
import utilidades.TipoNotificacion;
import intercambios.*;

import java.util.ArrayList;
import java.util.List;

public class EmpleadoIntercambio extends Empleado {

    private List<ProductoSegundaMano> productosPorValorar;
    private List<Intercambio> intercambiosPendientes;
    
    public EmpleadoIntercambio(String nombre, String contraseña) {
        super(nombre, contraseña);
        this.productosPorValorar = new ArrayList<>();
        this.intercambiosPendientes = new ArrayList<>(); 
    }

    //SETTERS//
    public void addProductoParaValorar(ProductoSegundaMano p) {this.productosPorValorar.add(p);}
    public void addIntercambio(Intercambio i) {this.intercambiosPendientes.add(i);}

    //GETTERS//
    public List<ProductoSegundaMano> getProductosPorValorar() {return new ArrayList<>(this.productosPorValorar);}
    public List<Intercambio> getIntercambiosPendientes() {return new ArrayList<>(this.intercambiosPendientes);}


    public void confirmarIntercambio(Intercambio i) { 
        i.setIntercambiado(true);
    }

    public void valorarProducto(ProductoSegundaMano p, int valoracion, double valorEstimado,
            utilidades.EstadoConservacion estadoConservacion) {
        p.setValoracion(valoracion, valorEstimado, estadoConservacion);
        this.productosPorValorar.remove(p);
    }

    public void marcarIntercambioListo(Intercambio i) {
        Notificacion n = new Notificacion(
            TipoNotificacion.INTERCAMBIO_REALIZADO,
            "El intercambio está listo para ser realizado"
        );
        i.getOferta().getUsuarioLanzador().addNotificacion(n);
        i.getOferta().getUsuarioReceptor().addNotificacion(n);
    }

    public void transferirPropiedad(Intercambio i) {
        Oferta oferta = i.getOferta();
        ClienteRegistrado lanzador = oferta.getUsuarioLanzador();
        ClienteRegistrado receptor = oferta.getUsuarioReceptor();
 
        // El lanzador da su productoOfertado al receptor
        receptor.getCartera().añadirProducto(oferta.getProductoOfertado());
        lanzador.getCartera().retirarProducto(oferta.getProductoOfertado());

        // El receptor da su productoDeseado al lanzador
        lanzador.getCartera().añadirProducto(oferta.getProductoDeseado());
        receptor.getCartera().retirarProducto(oferta.getProductoDeseado());
 
        i.setIntercambiado(true);
    } 

    public void reportarFallo(Intercambio i) {
        Oferta oferta = i.getOferta();
 
        // Restaurar disponibilidad de ambos productos individuales
        ProductoSegundaMano p1 = oferta.getProductoOfertado();
        p1.setDisponibilidad(true);
        p1.setEstadoProducto(utilidades.EstadoProducto.VALORADO);

        ProductoSegundaMano p2 = oferta.getProductoDeseado();
        p2.setDisponibilidad(true);
        p2.setEstadoProducto(utilidades.EstadoProducto.VALORADO);
 
        this.intercambiosPendientes.remove(i);
    }
}


