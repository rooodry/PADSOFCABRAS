package usuarios;

import intercambios.Intercambio;
import productos.ProductoSegundaMano;

public class EmpleadoIntercambio extends Empleado {
    
    public EmpleadoIntercambio(String nombre, String contraseña) {
        super(nombre, contraseña);
    }

    public boolean confirmarIntercambio(Intercambio i) {}

    public void valorarProducto(ProductoSegundaMano p) {}

    public void marcarIntercambioListo(Intercambio i) {}

    public void transferirPropiedad(Intercambio i) {} 

    public void reportarFallo(Intercambio i) {}
}


