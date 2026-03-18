package excepciones;

public class ExcepcionUsuarios extends Exception{

    private String nombre;

    public ExcepcionUsuarios(String nombre) {this.nombre = nombre;};

    public String toString() {
        return "Excepcion en Usuario " + this.nombre + ": ";
    }
    
}
