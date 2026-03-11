package productos;

import java.util.ArrayList;
import java.util.List;

public class Pack {
    private final String nombre;
    private final List<Producto> productos;
    private final List<Pack> subpacks;
    private double precio;

    public Pack(String nombre, double precio, List<Producto> productos) {
        this.nombre = nombre;
        this.precio = precio;
        this.productos = new ArrayList<>(productos); 
        this.subpacks = new ArrayList<>();

    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    public List<Pack> getSubpacks() {
        return new ArrayList<>(subpacks);
    }

    public void addSubpack(Pack subpack) {
        subpacks.add(subpack);
    }

    public void removeSubpack(Pack subpack) {
        subpacks.remove(subpack);
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}