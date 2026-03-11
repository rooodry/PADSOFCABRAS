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

    public String getNombre() {
        return nombre;
    }

    public List<Categoria> getSubcategorias() {
        return new ArrayList<>(subcategorias);
    }

    public void addSubcategoria(Categoria subcategoria) {
        subcategorias.add(subcategoria);
    }

    public void removeSubcategoria(Categoria subcategoria) {
        subcategorias.remove(subcategoria);
    }
    
}
