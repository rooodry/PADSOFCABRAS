package usuarios;

/**
 * Cliente invitado o visitante sin cuenta registrada completa.
 */
public class ClienteNoRegistrado extends Cliente {

    /**
     * Construye un cliente no registrado con las credenciales indicadas.
     *
     * @param nombre     nombre de usuario o identificador del visitante
     * @param contraseña contraseña de acceso (puede ser vacía para visitantes anónimos)
     */
    public ClienteNoRegistrado(String nombre, String contraseña) {
        super(nombre, contraseña);
    }
}
