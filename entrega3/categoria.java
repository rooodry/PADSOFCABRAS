package entrega3;

public class categoria extends producto {
    private String categoria;

    public categoria(String nombre, double precio, String categoria) {
        super(nombre, precio);
        this.categoria = categoria;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
}
