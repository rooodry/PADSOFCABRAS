package usuarios;


public abstract class Cliente extends Usuario {

    /**
     * Construye un cliente con las credenciales indicadas.
     *
     * @param nombre    nombre de usuario del cliente
     * @param contraseña contraseña de acceso del cliente
     */
    public Cliente(String nombre, String contraseña) {
        super(nombre, contraseña);
    }

    /**
     * Filtra los productos disponibles para el cliente según criterios
     * específicos.
     *
     * <p><b>Nota:</b> método pendiente de implementación en la versión actual.</p>
     */
    public void filtrarProducto() {

    }
}