package notificaciones;

import utilidades.tipoNotificacion;

public class Notificacion {

    private final tipoNotificacion tipoNotifacion;
    private String mensaje;
    private boolean leida;
    private boolean borrada;

    public Notificacion(tipoNotificacion tipo, String mensaje) {
        this.tipoNotifacion = tipo;
        this.mensaje = mensaje;
        this.leida = false;
        this.borrada = false;
    } 

    //SETTERS//
    public void setLeida() {this.leida = true;}
    public void setBorrada() {this.borrada = true;}

    //GETTERS//
    public tipoNotificacion getTipoNotificacion() {return this.tipoNotifacion;}
    public String getMensaje() {return this.mensaje;}
    public boolean getLeida() {return this.leida;}
    public boolean getBorrada() {return this.borrada;}

    

}

