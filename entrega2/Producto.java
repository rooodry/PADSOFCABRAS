
enum EstadoProducto {
    PERFECTO,
    MUY_BIEN,
    BIEN,
    USADO,
    MAL,
    MUY_MAL,
    FATAL
}

enum CategoriaProducto {
    JUEGO,
    FIGURA,
    COMIC
}

public class Producto { 
    private String nombre;
    private String codigo;
    private double precio;
    private String descripcion;
    private int valoracion;
    private String foto;
    private int stock;
    private String categoria;
    private int ordenLlegada;
    private EstadoProducto estado;

    public Producto(String nombre, String codigo, double precio, String descripcion, int valoracion, String foto, int stock, CategoriaProducto categoria, int ordenLlegada, EstadoProducto estado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.valoracion = valoracion;
        this.foto = foto;
        this.estado = estado;
        this.categoria = categoria;
        this.stock = stock;
        this.ordenLlegada = ordenLlegada;
    }

    @Override
    public String toString() {
        return "=== Detalle del Producto ===\n" +
               "Código: " + codigo + "\n" +
               "Nombre: " + nombre + "\n" +
               "Categoría: " + categoria + "\n" +
               "Precio: " + precio + "€\n" +
               "Stock disponible: " + stock + "\n" +
               "Estado: " + estado + "\n" +
               "Valoración: " + valoracion + "/5\n" +
               "Orden de llegada: " + ordenLlegada + "\n" +
               "Descripción: " + descripcion + "\n" +
               "Foto: " + foto + "\n" +
               "============================";
    }


    





}
