package productos;

import java.util.Date;


public abstract class Producto {
    private String nombre;
    private String descripcion;
    private double precio;
    private int valoracion;
    private String imagen;
    private Date fechaPublicacion;

    public Producto(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = 0.0;
        this.valoracion = 0;
        this.imagen = "";
        this.fechaPublicacion = new Date();
    }
}
