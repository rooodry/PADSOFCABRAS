package usuarios;

import intercambios.Intercambio;
import productos.ProductoSegundaMano;
import java.util.Scanner;

public class EmpleadoIntercambio extends Empleado {
    
    public EmpleadoIntercambio(String nombre, String contraseña) {
        super(nombre, contraseña);
    }

    public void confirmarIntercambio(Intercambio i) { 
        i.setIntercambiado(true);
    }

    public void valorarProducto(ProductoSegundaMano p) {
        
        Scanner teclado = new Scanner(System.in);
        String buffer;
        int valoracionEmpleado = 0;

        while(valoracionEmpleado == 0) {
            try {
                buffer = teclado.nextLine();
                valoracionEmpleado = Integer.parseInt(buffer);
                p.setValoracionEmpleado(valoracionEmpleado);
            } catch (NumberFormatException e) {
                System.err.println("Solo se admiten números");
        }
        }
        

 
    }

    public void marcarIntercambioListo(Intercambio i) {}

    public void transferirPropiedad(Intercambio i) {} 

    public void reportarFallo(Intercambio i) {}
}


