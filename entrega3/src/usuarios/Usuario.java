package usuarios;

import notificaciones.Notificacion;
import java.util.*;

/**
 * Clase base que representa a cualquier usuario del sistema.
 *
 * <p>Almacena las credenciales de acceso (nombre de usuario y contraseña)
 * y la lista de notificaciones recibidas. Es la raíz de la jerarquía de
 * usuarios: de ella heredan {@link Cliente}, {@link Empleado} y
 * {@link Gestor}.</p>
 *
 * <p>El accesor de notificaciones devuelve una copia defensiva para
 * evitar modificaciones externas de la lista interna.</p>
 */
public class Usuario {

    /** Nombre de usuario utilizado para identificarse en el sistema. */
    private String nombreUsuario;

    /** Contraseña de acceso del usuario. */
    private String contraseña;

    /** Lista de notificaciones recibidas por el usuario. */
    private List<Notificacion> notificaciones;

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
}