package usuarios;

import notificaciones.Notificacion;
import java.io.Serializable;
import java.util.*;


public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;


    private String nombreUsuario;


    private String contraseña;


    private List<Notificacion> notificaciones;


    private String fotoPerfil;


    /**
     * Construye un usuario con las credenciales indicadas y sin notificaciones.
     *
     * @param nombreUsuario nombre de usuario; no debe ser {@code null}
     * @param contraseña    contraseña de acceso; no debe ser {@code null}
     */
    public Usuario(String nombreUsuario, String contraseña) {
        this.nombreUsuario  = nombreUsuario;
        this.contraseña     = contraseña;
        this.notificaciones = new ArrayList<>();
        this.fotoPerfil = "lib/fotos/fotousuario.jpg";
    }

    /**
     * Actualiza el nombre de usuario.
     *
     * @param nombreUsuario nuevo nombre de usuario
     */
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    /**
     * Actualiza la contraseña del usuario.
     *
     * @param contraseña nueva contraseña
     */
    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    /**
     * Añade una notificación a la lista del usuario.
     *
     * @param n notificación a añadir; no debe ser {@code null}
     */
    public void addNotificacion(Notificacion n) {
        this.notificaciones.add(n);
    }

    /**
     * Elimina una notificación de la lista del usuario.
     *
     * <p>Si la notificación no existe en la lista, la operación no tiene efecto.</p>
     *
     * @param n notificación a eliminar
     */
    public void removeNotificacion(Notificacion n) {
        this.notificaciones.remove(n);
    }

    /**
     * Devuelve el nombre de usuario.
     *
     * @return nombre de usuario
     */
    public String getNombre() {
        return this.nombreUsuario;
    }

    /**
     * Devuelve la contraseña del usuario.
     *
     * @return contraseña en texto plano
     */
    public String getContraseña() {
        return this.contraseña;
    }

    /**
     * Devuelve la lista de notificaciones del usuario.
     *
     * @return copia defensiva de la lista de notificaciones; nunca {@code null}
     */
    public List<Notificacion> getNotificaciones() {
        return new ArrayList<>(this.notificaciones);
    }

    /**
     * Actualiza la ruta de la foto de perfil.
     *
     * @param fotoPerfil ruta local a la imagen (o {@code null} para quitarla)
     */
    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    /**
     * Devuelve la ruta de la foto de perfil.
     *
     * @return ruta local de la foto, o {@code null} si no tiene
     */
    public String getFotoPerfil() {
        return this.fotoPerfil;
    }
}
