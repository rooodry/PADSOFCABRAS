package productos;

import java.util.Date;


public abstract class Producto {
    private String nombre;
    private String descripcion;
    private double precio;
    private int valoracion;
    private String imagen;
    private Date fechaPublicacion;

    public Producto(String nombre, String descripcion, double precio, int valoracion, String imagen, Date fechaPublicacion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.valoracion = valoracion;
        this.imagen = imagen;
        this.fechaPublicacion = fechaPublicacion;
    }
}
