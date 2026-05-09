package excepciones;


public class ExcepcionUsuariosAdmin extends ExcepcionUsuarios {

    /**
     * Constructor de la excepción.
     * * @param nombre Nombre del usuario que provocó la excepción.
     */
    public ExcepcionUsuariosAdmin(String nombre) {
        super(nombre);
    }

    /**
     * Sobrescribe el método toString para especificar la causa del fallo de permisos.
     * * @return Cadena formateada indicando que el usuario no tiene rol de administrador.
     */
    @Override
    public String toString() {
        return super.toString() + "Usuario no es admin";
    }
}