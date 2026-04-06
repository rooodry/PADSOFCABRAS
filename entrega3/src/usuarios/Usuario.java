package usuarios;

import notificaciones.Notificacion;
import java.util.*;

public class Usuario {
    private String nombreUsuario;
    private String contraseña;
    private ArrayList<Notificacion> notificaciones;

    public Usuario(String nombreUsuario, String contraseña) {
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.notificaciones = new ArrayList<>();
    }

    //SETTERS//
    public void setNombreUsuario(String nombreUsuario) {this.nombreUsuario = nombreUsuario;}
    public void setContraseña(String contraseña) {this.contraseña = contraseña;}
    public void addNotificacion(Notificacion n) {this.notificaciones.add(n);}


    //GETERRS//
    public String getNombre() {return this.nombreUsuario;}
    public String getContraseña() {return this.contraseña;}
    public List<Notificacion> getNotificaciones() {return new ArrayList<>(this.notificaciones);}    
}