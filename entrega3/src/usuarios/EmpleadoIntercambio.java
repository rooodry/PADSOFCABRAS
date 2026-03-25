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
    public List<ProductoSegundaMano> getProductosPorValorar() {return this.productosPorValorar;}
    public List<Intercambio> getIntercambiosPendientes() {return this.intercambiosPendientes;}


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
 
        for (ProductoSegundaMano p : oferta.getProductos()) {
            receptor.getCartera().añadirProducto(p);
            lanzador.getCartera().getProductos().remove(p);
        }
 
        i.setIntercambiado(true);
        i.setFechaAceptada(new java.util.Date());
    } 

    public void reportarFallo(Intercambio i) {
        Oferta oferta = i.getOferta();
 
        for (ProductoSegundaMano p : oferta.getProductos()) {
            p.setDisponibilidad(true);
            p.setEstadoProducto(utilidades.EstadoProducto.VALORADO);
        }
 
        this.intercambiosPendientes.remove(i);
    }
}


