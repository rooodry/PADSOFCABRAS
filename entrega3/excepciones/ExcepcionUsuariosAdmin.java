package excepciones;

public class ExcepcionUsuariosAdmin extends ExcepcionUsuarios{

    public ExcepcionUsuariosAdmin(String nombre) {super(nombre);}

    public String toString() {
        return super.toString() + "Usuario no es admin";
    }
    
}
