package productos;
import usuarios.ClienteRegistrado;
import utilidades.*;

public class ProductoSegundaMano extends Producto {
    private boolean disponibilidad;
    private final ClienteRegistrado propietario;
    private int valoracionEmpleado;
    private double valorEstimado;
    private boolean estaValorado;
    private EstadoConservacion estadoConservacion;
    private EstadoProducto estadoProducto;

    public ProductoSegundaMano(String nombre, String descripcion, String imagen, ClienteRegistrado propietario) {
        super(nombre, descripcion, imagen);
        this.propietario = propietario;
        this.disponibilidad = false;
        this.estaValorado = false;
        this.valoracionEmpleado = 0;
        this.valorEstimado = 0.0;
        this.estadoProducto = EstadoProducto.PENDIENTE_DE_VALORAR;
    }

    //SETTERS//
    public void setDisponibilidad(boolean flag) {this.disponibilidad = flag;}
    public void setValoracionEmpleado(int valoracion) {this.valoracionEmpleado = valoracion;}
    public void setValoracion(int valoracion, double valorEstimado, EstadoConservacion estadoConservacion) {
        this.valoracionEmpleado = valoracion;
        this.valorEstimado = valorEstimado;
        this.estadoConservacion = estadoConservacion;
        this.estaValorado = true;
        this.estadoProducto = EstadoProducto.VALORADO;
    }
    public void setEstaValorado(boolean flag) {this.estaValorado = flag;}
    public void setEstadoConservacion(EstadoConservacion e) {this.estadoConservacion = e;}
    public void setEstadoProducto(EstadoProducto e) {this.estadoProducto = e;}


    //GETTERS//
    public boolean getDisponibilidad() {return this.disponibilidad;}
    public ClienteRegistrado getPropietario() {return this.propietario;}
    public int getValoracionEmpleado() {return this.valoracionEmpleado;}
    public double getValorEstimado() {return this.valorEstimado;}
    public boolean getEstaValorado() {return this.estaValorado;}
    public EstadoConservacion getEstadoConservacion() { return this.estadoConservacion;}
    public EstadoProducto getEstadoProducto() {return this.estadoProducto;}






    public void pedirValoracion() {
        this.estadoProducto = EstadoProducto.PENDIENTE_DE_VALORAR;
    }

    public void subirProducto() {
        if(this.estaValorado) {
            this.disponibilidad = true;
            this.estadoProducto = EstadoProducto.VALORADO;
        }
    }

    public void borrarProducto() {
        this.disponibilidad = false;
    }



   
} 
