package productos;

import java.util.Date;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import productos.categoria.Categoria;

public abstract class Producto {
    private final String id; 
    private String nombre;
    private String descripcion;
    private int valoracion;
    private String imagen;
    private final Date fechaPublicacion;
    private Categoria categoria;
    // Nueva lista para almacenar comentarios [cite: 350]
    private Map<String, String> comentarios;

    public Producto(String nombre, String descripcion, String imagen) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.valoracion = 0;
        this.imagen = imagen;
        this.fechaPublicacion = new Date();
        this.categoria = null;
        this.comentarios = new HashMap<>(); // Inicialización [cite: 241]
    }

    // --- SETTERS ---
    public void setNombre(String nombre) {this.nombre = nombre;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}
    public void setValoracion(int valoracion) {this.valoracion = valoracion;}
    public void setImagen(String imagen) {this.imagen = imagen;}
    public void setCategoria(Categoria categoria) {this.categoria = categoria;}

    // Nuevo método para añadir comentarios [cite: 347, 350]
    public void addComentario(String usuario, String texto) {
        this.comentarios.put(usuario, texto);
    }

    // --- GETTERS ---
    public String getId() {return this.id;}
    public String getNombre() {return this.nombre;}
    public String getDescripcion() {return this.descripcion;}
    public int getValoracion() {return this.valoracion;}
    public String getImagen() {return this.imagen;}
    public Date getFechaPublicacion() {return new Date(fechaPublicacion.getTime());}
    public Categoria getCategoria() {return categoria;}
    
    // Getter para los comentarios [cite: 143]
    public Map<String, String> getComentarios() {
        return new HashMap<>(this.comentarios);
    }
}
