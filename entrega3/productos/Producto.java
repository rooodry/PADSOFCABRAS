package productos;

import java.util.Date;
import java.util.UUID;
import productos.categoria.Categoria;

public abstract class Producto {
    private final String id; 
    private String nombre;
    private String descripcion;
    private double precio;
    private int valoracion;
    private String imagen;
    private Date fechaPublicacion;
    private Categoria categoria;

    public Producto(String nombre, String descripcion, double precio, String imagen) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.valoracion = 0;
        this.imagen = imagen;
        this.fechaPublicacion = new Date();
        this.categoria = null;
    }

    public Producto(String nombre, String descripcion) {
        this(nombre, descripcion, 0.0, "");
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public String getImagen() {
        return imagen;
    }

    public Date getFechaPublicacion() {
        return new Date(fechaPublicacion.getTime());
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setCategoria(Categoria categoria) {
    this.categoria = categoria;
    }
}
