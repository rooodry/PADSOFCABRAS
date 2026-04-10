package excepciones;

/**
 * Excepción personalizada base para manejar errores relacionados con los usuarios del sistema.
 */
public class ExcepcionUsuarios extends Exception {

    private String nombre;

    /**
     * Constructor de la excepción.
     * * @param nombre Nombre o identificador del usuario que provocó la excepción.
     */
    public ExcepcionUsuarios(String nombre) {
        super("Excepcion en Usuario " + nombre);
        this.nombre = nombre;
    }

    /**
     * Sobrescribe el método toString para proporcionar un formato detallado del error.
     * * @return Cadena formateada con el nombre del usuario.
     */
    @Override
    public String toString() {
        return "Excepcion en Usuario " + this.nombre + ": ";
    }
}