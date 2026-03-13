package usuarios;

public abstract class Empleado extends Usuario {

    public Empleado(String nombreUsuario, String contraseña) {
        super(nombreUsuario, contraseña);
    }

    public String getNombre() {return this.getNombre();}
    public String getContraseña() {return this.getContraseña();}

}