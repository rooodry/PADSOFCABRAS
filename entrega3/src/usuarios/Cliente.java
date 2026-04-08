package usuarios;

import productos.Producto;

/**
 * Clase abstracta que representa a un cliente del sistema.
 *
 * <p>Extiende {@link Usuario} añadiendo la semántica de cliente (comprador).
 * Sus subclases concretas son {@link ClienteRegistrado} y
 * {@link ClienteNoRegistrado}, que difieren en las funcionalidades
 * disponibles según el estado de registro.</p>
 *
 * <p>Incluye el método {@link #filtrarProducto()}, actualmente sin
 * implementación, destinado a la lógica de filtrado de productos visible
 * para el cliente.</p>
 */
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
        // Pendiente de implementación
    }
}