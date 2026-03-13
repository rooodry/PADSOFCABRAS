package usuarios;

import notificaciones.Notificacion;
import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private final String nombreUsuario;
    private String contraseña;
    private List<Notificacion> notificaciones;

    public Usuario(String nombreUsuario, String contraseña) {
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.notificaciones = null;
    }


    //SETTERS//
    public void addNotificacion(Notificacion n) {this.notificaciones.add(n);}


    //GETTERS//
    public String getNombreUsuario() {return nombreUsuario;}
    public String getContraseña() {return contraseña;}
    public List<Notificacion> getNotificaciones() {return this.notificaciones;}
}