package usuarios;

import notificaciones.Notificacion;
import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String nombreUsuario;
    private String contraseña;
    private List<Notificacion> notificaciones;

    public Usuario(String nombreUsuario, String contraseña) {
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.notificaciones = null;
    }


    public String getContraseña() {
        return contraseña;
    }

    public String getNombre() { return this.nombreUsuario;}

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}