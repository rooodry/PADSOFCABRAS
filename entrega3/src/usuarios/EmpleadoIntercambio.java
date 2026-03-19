package usuarios;

import intercambios.Intercambio;
import productos.ProductoSegundaMano;


import java.util.ArrayList;
import java.util.List;
import java.util.InputMismatchException;
import java.util.Scanner;

public class EmpleadoIntercambio extends Empleado {

    private List<ProductoSegundaMano> productosPorValorar;
    private List<Intercambio> intercambiosPendientes;
    
    public EmpleadoIntercambio(String nombre, String contraseña) {
        super(nombre, contraseña);
        this.productosPorValorar = new ArrayList<>();
        this.intercambiosPendientes = new ArrayList<>(); 
    }

    //SETTERS//
    public void addProductoParaValorar(ProductoSegundaMano p) {this.productosPorValorar.add(p);}
    public void addIntercambio(Intercambio i) {this.intercambiosPendientes.add(i);}

    //GETTERS//
    public List<ProductoSegundaMano> getProductosPorValorar() {return this.productosPorValorar;}
    public List<Intercambio> getIntercambiosPendientes() {return this.intercambiosPendientes;}


    public void confirmarIntercambio(Intercambio i) { 
        i.setIntercambiado(true);
    }

    public void valorarProducto(ProductoSegundaMano p) {
        
        Scanner teclado = new Scanner(System.in);
        int valoracionEmpleado = 0;

        while(valoracionEmpleado == 0) {
            try {
                valoracionEmpleado = teclado.nextInt();
                p.setValoracionEmpleado(valoracionEmpleado);
            } catch (InputMismatchException e) {
                System.err.println("Solo se admiten números");
            }
        }
        teclado.close();
    }

    public void marcarIntercambioListo(Intercambio i) {


    }

    public void transferirPropiedad(Intercambio i) {

    } 

    public void reportarFallo(Intercambio i) {}
}


