package usuarios;

/**
 * Representa a un cliente que accede al sistema sin haberse registrado.
 *
 * <p>Hereda de {@link Cliente} las credenciales básicas y la gestión de
 * notificaciones, pero no dispone de las funcionalidades exclusivas de
 * los clientes registrados como historial de pedidos, cartera de productos
 * de segunda mano, intercambios o códigos de descuento.</p>
 *
 * <p>En la versión actual esta clase no añade comportamiento adicional
 * a su superclase; está pensada para representar el rol de visitante
 * o usuario anónimo del sistema.</p>
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