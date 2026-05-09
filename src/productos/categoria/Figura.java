package productos.categoria;


public class Figura extends Categoria {


    private final double altura;


    private final String marca;


    private final String material;

    /**
     * Construye una figura con todos sus atributos.
     *
     * @param nombre   nombre de la figura (se usa como parte del nombre de categoría)
     * @param altura   altura en centímetros
     * @param marca    marca fabricante
     * @param material material principal de fabricación
     */
    public Figura(String nombre, double altura, String marca, String material) {
        super("Figura:" + nombre);
        this.altura   = altura;
        this.marca    = marca;
        this.material = material;
    }

    /**
     * Devuelve la altura de la figura en centímetros.
     *
     * @return altura en cm
     */
    public double getAltura() { return this.altura; }

    /**
     * Devuelve la marca fabricante de la figura.
     *
     * @return nombre de la marca
     */
    public String getMarca() { return this.marca; }

    /**
     * Devuelve el material principal de la figura.
     *
     * @return descripción del material
     */
    public String getMaterial() { return this.material; }
}