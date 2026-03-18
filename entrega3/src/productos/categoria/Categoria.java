package productos.categoria;

import java.util.ArrayList;
import java.util.List;

public abstract class Categoria {
    private String nombre;
    private List<Categoria> subcategorias;

    public Categoria(String nombre) {
        this.nombre = nombre;
        this.subcategorias = new ArrayList<>();
    }

    //SETTER//
    public void addSubcategoria(Categoria subcategoria) {this.subcategorias.add(subcategoria);}

    //GETTERS//
    public String getNombre() {return this.nombre;}

    public List<Categoria> getSubcategorias() {return new ArrayList<>(subcategorias);}





    public void removeSubcategoria(Categoria subcategoria) {
        this.subcategorias.remove(subcategoria);
    }
    
}
