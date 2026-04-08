package productos.categoria;
public class Figura extends Categoria {
    private final double altura;
    private final String marca;
    private final String material;

    public Figura(String nombre, double altura, String marca, String material) {
        super("Figura:" + nombre); 
        this.altura = altura;
        this.marca = marca;
        this.material = material;
    }


    //GETTERS//
    public double getAltura() {return this.altura;}
    public String getMarca() {return this.marca;}
    public String getMaterial() {return this.material;}
    
}