package entrega3;

public class figura extends categoria {
    private final double altura;
    private final double anchura;
    private final String marca;

    public Figura(double altura, double anchura, String marca) {
        super(); 
        this.altura = altura;
        this.anchura = anchura;
        this.marca = marca;
    }

    public double getAltura() {
        return altura;
    }

    public double getAnchura() {
        return anchura;
    }

    public String getMarca() {
        return marca;
    }
    
}