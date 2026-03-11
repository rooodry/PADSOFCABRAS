package productos;
import usuarios.ClienteRegistrado;

public class ProductoSegundaMano extends Producto {
    private boolean estaDisponible;
    private ClienteRegistrado propietario;
    private int valoracionEmpleado;
    private double valorEstimado;
    private boolean estaValorado;
    private EstadoConservacion estadoConservacion;
    private EstadoProducto estadoProducto;

    public ProductoSegundaMano(String nombre, String descripcion, String imagen, ClienteRegistrado propietario) {
        super(nombre, descripcion, imagen);
        this.propietario = propietario;
        this.estaDisponible = false;
        this.estaValorado = false;
        this.valoracionEmpleado = 0;
        this.valorEstimado = 0.0;
        this.estadoProducto = EstadoProducto.PENDIENTE_DE_VALORAR;
    }

    public void pedirValoracion() {
        this.estadoProducto = EstadoProducto.PENDIENTE_DE_VALORAR;
    }

    public void subirProducto() {
        if(this.estaValorado) {
            this.estaDisponible = true;
            this.estadoProducto = EstadoProducto.VALORADO;
        }
    }

    public void borrarProducto() {
        this.estaDisponible = false;
    }

    public void setValoracionEmpleado(int valoracion) {
        this.valoracionEmpleado = valoracion;
    }

    public void setValoracion(int valoracion, double valorEstimado, EstadoConservacion estadoConservacion) {
        this.valoracionEmpleado = valoracion;
        this.valorEstimado = valorEstimado;
        this.estadoConservacion = estadoConservacion;
        this.estaValorado = true;
        this.estadoProducto = EstadoProducto.VALORADO;
    }

    public int getValoracionEmpleado() {
        return valoracionEmpleado;
    }

    public boolean estaDisponible() {
        return estaDisponible;
    }

    public EstadoProducto getEstadoProducto() {
        return estadoProducto;
    }

    public ClienteRegistrado getPropietario() {
        return propietario;
    }

    public double getValorEstimado() { 
        return valorEstimado; 
    }

    public EstadoConservacion getEstadoConservacion() { 
        return estadoConservacion; 
    }
} 
