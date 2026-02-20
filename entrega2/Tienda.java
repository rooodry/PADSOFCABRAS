import java.util.*;

public class Tienda {
    public static void main(String[] args) {
        ArrayList<Producto> inventario = new ArrayList<>();

        inventario.add(new Producto("Figura Batman", "F001", 25.50, "Figura articulada", 4.5, "batman.jpg", 10, "FIGURAS", 1, EstadoProducto.PERFECTO));
        inventario.add(new Producto("Catan", "J001", 45.00, "Juego de mesa", 4.8, "catan.jpg", 5, "JUEGOS", 2, EstadoProducto.USADO));

        for(Producto p: inventario) {
            if(p.estado == EstadoProducto.PERFECTO) {
                System.out.println(p.nombre + " está en perfecto estado.");
            }
        }
    }
    
}
