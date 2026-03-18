package productos.categoria;
public class Figura extends Categoria {
    private final double altura;
    private final double anchura;
    private final String marca;

    public Figura(String nombre, double altura, double anchura, String marca) {
        super("Figura:" + nombre); 
        this.altura = altura;
        this.anchura = anchura;
        this.marca = marca;
    }


    //GETTERS//
    public double getAltura() {return this.altura;}

    public double getAnchura() {return this.anchura;}

    public String getMarca() {return this.marca;}
    
}