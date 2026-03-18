package usuarios;

import notificaciones.Notificacion;
import java.util.ArrayList;

public class Usuario {
    private String nombreUsuario;
    private String contraseña;
    private ArrayList<Notificacion> notificaciones;

    public Usuario(String nombreUsuario, String contraseña) {
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.notificaciones = null;
    }

    //SETTERS//
    public void setNombreUsuario(String nombreUsuario) {this.nombreUsuario = nombreUsuario;}
    public void setContraseña(String contraseña) {this.contraseña = contraseña;}
    public void addNotificacion(Notificacion n) {this.notificaciones.add(n);}


    //GETERRS//
    public String getNombre() {return this.nombreUsuario;}
    public String getContraseña() {return this.contraseña;}
    public ArrayList<Notificacion> getNotificaciones() {return new ArrayList<Notificacion>(this.notificaciones);}
    

    

    
}