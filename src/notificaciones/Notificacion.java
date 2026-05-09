package notificaciones;

import java.io.Serializable;
import java.util.Date;
import utilidades.TipoNotificacion;


public class Notificacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final TipoNotificacion tipoNotificacion;
    private String mensaje;
    private boolean leida;
    private boolean borrada;
    private Date fechaCreacion;

    /**
     * Constructor de la clase Notificacion.
     * Se inicializa como no leída y no borrada.
     *
     * @param tipo    Categoría de la notificación.
     * @param mensaje Cuerpo de texto del aviso.
     */
    public Notificacion(TipoNotificacion tipo, String mensaje) {
        this.tipoNotificacion = tipo;
        this.mensaje = mensaje;
        this.leida = false;
        this.borrada = false;
        this.fechaCreacion = new Date();
    }

    /**
     * Marca la notificación como leída por el usuario.
     */
    public void setLeida() {this.leida = true;}

    /**
     * Marca la notificación como borrada para ocultarla al usuario.
     */
    public void setBorrada() {this.borrada = true;}

    /**
     * Obtiene el tipo o categoría de la notificación.
     * @return Enum TipoNotificacion.
     */
    public TipoNotificacion getTipoNotificacion() {return this.tipoNotificacion;}

    /**
     * Obtiene el contenido del mensaje.
     * @return Texto de la notificación.
     */
    public String getMensaje() {return this.mensaje;}

    /**
     * Obtiene la fecha en la que se genero la notificacion.
     * @return Copia de la fecha de creacion, o una fecha antigua para datos previos.
     */
    public Date getFechaCreacion() {
        return fechaCreacion != null ? new Date(fechaCreacion.getTime()) : new Date(0);
    }

    /**
     * Comprueba si el usuario ha leído la notificación.
     * @return true si ha sido leída, false en caso contrario.
     */
    public boolean getLeida() {return this.leida;}

    /**
     * Comprueba si la notificación fue marcada como borrada.
     * @return true si está borrada, false si sigue visible.
     */
    public boolean getBorrada() {return this.borrada;}

}
