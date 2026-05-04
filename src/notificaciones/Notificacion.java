package notificaciones;

import java.io.Serializable;
import utilidades.TipoNotificacion;

/**
 * Representa un aviso o alerta dirigida a un usuario del sistema.
 * Contiene el estado de lectura, visibilidad y el tipo de mensaje.
 */
public class Notificacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final TipoNotificacion tipoNotificacion;
    private String mensaje;
    private boolean leida;
    private boolean borrada;

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
