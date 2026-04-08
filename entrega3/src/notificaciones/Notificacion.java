package notificaciones;

import utilidades.TipoNotificacion;

public class Notificacion {

    private final TipoNotificacion tipoNotificacion;
    private String mensaje;
    private boolean leida;
    private boolean borrada;

    public Notificacion(TipoNotificacion tipo, String mensaje) {
        this.tipoNotificacion = tipo;
        this.mensaje = mensaje;
        this.leida = false;
        this.borrada = false;
    } 

    public void setLeida() {this.leida = true;}
    public void setBorrada() {this.borrada = true;}

    public TipoNotificacion getTipoNotificacion() {return this.tipoNotificacion;} 
    public String getMensaje() {return this.mensaje;}
    public boolean getLeida() {return this.leida;}
    public boolean getBorrada() {return this.borrada;}

}