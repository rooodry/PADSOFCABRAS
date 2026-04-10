package productos;

import java.util.*;

import usuarios.ClienteRegistrado;
import utilidades.*;

/**
 * Representa un producto subido por un usuario registrado para ser intercambiado o vendido.
 * Incluye estados de conservación, valoraciones por empleados y detalles del propietario.
 */
public class ProductoSegundaMano extends Producto {
    private boolean disponibilidad;
    private final ClienteRegistrado propietario;
    private int valoracionEmpleado;
    private double valorEstimado;
    private boolean estaValorado;
    private EstadoConservacion estadoConservacion;
    private EstadoProducto estadoProducto;
    private Date fechaValoracion;

    /**
     * Constructor de la clase ProductoSegundaMano.
     * Inicializa el producto como pendiente de valoración y no disponible.
     *
     * @param nombre      Nombre del producto.
     * @param descripcion Descripción del estado o detalles del producto.
     * @param imagen      Imagen subida por el cliente.
     * @param propietario Cliente registrado que posee el producto.
     */
    public ProductoSegundaMano(String nombre, String descripcion, String imagen, ClienteRegistrado propietario) {
        super(nombre, descripcion, imagen);
        this.propietario = propietario;
        this.disponibilidad = false;
        this.estaValorado = false;
        this.valoracionEmpleado = 0;
        this.valorEstimado = 0.0;
        this.estadoProducto = EstadoProducto.PENDIENTE_DE_VALORAR;
        this.fechaValoracion = null;
    }

    /**
     * Establece la disponibilidad pública del producto.
     * @param flag true para hacerlo disponible, false para ocultarlo.
     */
    public void setDisponibilidad(boolean flag) {this.disponibilidad = flag;}

    /**
     * Establece la nota asignada por un empleado tras la revisión.
     * @param valoracion Puntuación asignada por el empleado.
     */
    public void setValoracionEmpleado(int valoracion) {this.valoracionEmpleado = valoracion;}

    /**
     * Registra la valoración completa realizada por un empleado.
     * @param valoracion         Puntuación asignada.
     * @param valorEstimado      Valor monetario estimado del producto.
     * @param estadoConservacion Estado físico del producto.
     */
    public void setValoracion(int valoracion, double valorEstimado, EstadoConservacion estadoConservacion) {
        this.valoracionEmpleado = valoracion;
        this.valorEstimado = valorEstimado;
        this.estadoConservacion = estadoConservacion;
        this.estaValorado = true;
        this.estadoProducto = EstadoProducto.VALORADO;
    }

    /**
     * Indica manualmente si el producto ha sido valorado.
     * @param flag true si está valorado, false si no.
     */
    public void setEstaValorado(boolean flag) {this.estaValorado = flag;}

    /**
     * Establece el estado de conservación del producto.
     * @param e Estado de conservación.
     */
    public void setEstadoConservacion(EstadoConservacion e) {this.estadoConservacion = e;}

    /**
     * Establece el estado lógico del producto dentro del sistema.
     * @param e Estado de producto.
     */
    public void setEstadoProducto(EstadoProducto e) {this.estadoProducto = e;}

    /**
     * Establece la fecha en la que se realizó la valoración del producto.
     * @param fecha Fecha de la valoración.
     */
    public void setFechaValoracion(Date fecha) {this.fechaValoracion = fecha != null ? new Date(fecha.getTime()) : null;}

    /**
     * Comprueba si el producto está disponible.
     * @return true si está disponible, false en caso contrario.
     */
    public boolean getDisponibilidad() {return this.disponibilidad;}

    /**
     * Obtiene el propietario original del producto.
     * @return ClienteRegistrado propietario del producto.
     */
    public ClienteRegistrado getPropietario() {return this.propietario;}

    /**
     * Obtiene la valoración dada por el empleado.
     * @return Entero con la valoración.
     */
    public int getValoracionEmpleado() {return this.valoracionEmpleado;}

    /**
     * Obtiene el valor económico estimado del producto.
     * @return Valor estimado.
     */
    public double getValorEstimado() {return this.valorEstimado;}

    /**
     * Indica si el producto ya pasó el proceso de valoración.
     * @return true si está valorado.
     */
    public boolean getEstaValorado() {return this.estaValorado;}

    /**
     * Obtiene el estado físico de conservación del producto.
     * @return Enum EstadoConservacion.
     */
    public EstadoConservacion getEstadoConservacion() {return this.estadoConservacion;}

    /**
     * Obtiene el estado actual del producto en el flujo de la aplicación.
     * @return Enum EstadoProducto.
     */
    public EstadoProducto getEstadoProducto() {return this.estadoProducto;}

    /**
     * Obtiene la fecha en la que fue valorado.
     * @return Copia de la fecha de valoración, o null si no se ha valorado.
     */
    public Date getFechaValoracion() {return this.fechaValoracion != null ? new Date(this.fechaValoracion.getTime()) : null;}

    /**
     * Reinicia el estado del producto para solicitar una nueva valoración.
     */
    public void pedirValoracion() {
        this.estadoProducto = EstadoProducto.PENDIENTE_DE_VALORAR;
    }

    /**
     * Publica el producto haciéndolo disponible, siempre y cuando ya esté valorado.
     */
    public void subirProducto() {
        if(this.estaValorado) {
            this.disponibilidad = true;
            this.estadoProducto = EstadoProducto.VALORADO;
        }
    }

    /**
     * Oculta el producto quitando su disponibilidad.
     */
    public void borrarProducto() {
        this.disponibilidad = false;
    }

}