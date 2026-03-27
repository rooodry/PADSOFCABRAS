package productos.categoria;

import java.util.ArrayList;
import java.util.List;

public abstract class Categoria {
    private String nombre;
    private Categoria subCategoria;

    public Categoria(String nombre) {
        this.nombre = nombre;
        this.subCategoria = null;
    }

    //SETTER//
    public void setSubCategoria(Categoria subcategoria) {this.subCategoria = subcategoria;}

    //GETTERS//
    public String getNombre() {return this.nombre;}

    public Categoria getSubcategoria() {return this.subCategoria;}


    
}
