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

    //SETTERS//
    public void addSubpack(Pack subpack) {this.subpacks.add(subpack);}
    public void setPrecio(double precio) {this.precio = precio;}


    //GETTERS//
    public String getNombre() {return this.nombre;}

    public double getPrecio() {return this.precio;}

    public List<Producto> getProductos() {return new ArrayList<>(this.productos);}

    public List<Pack> getSubpacks() {return new ArrayList<>(this.subpacks);}



    public void removeSubpack(Pack subpack) {
        subpacks.remove(subpack);
    }


}