package productos;

import java.util.Date;
import java.util.UUID;
import productos.categoria.Categoria;

/**
 * Clase abstracta que representa la base de un producto en el sistema.
 * Contiene los atributos comunes a cualquier tipo de producto.
 */
public abstract class Producto {
    protected String id; 
    private String nombre;
    private String descripcion;
    private int valoracion;
    private String imagen;
    private final Date fechaPublicacion;
    private Categoria categoria;

    /**
     * Constructor de la clase Producto.
     * Genera automáticamente un identificador único y establece la fecha de publicación actual.
     *
     * @param nombre      Nombre del producto.
     * @param descripcion Breve descripción del producto.
     * @param imagen      Ruta o enlace a la imagen del producto.
     */
    public Producto(String nombre, String descripcion, String imagen) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.valoracion = 0;
        this.imagen = imagen;
        this.fechaPublicacion = new Date();
        this.categoria = null;
    }

    /**
     * Establece el nombre del producto.
     * @param nombre Nuevo nombre del producto.
     */
    public void setNombre(String nombre) {this.nombre = nombre;}

    /**
     * Establece la descripción del producto.
     * @param descripcion Nueva descripción.
     */
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    /**
     * Establece la valoración numérica del producto.
     * @param valoracion Valor entero que representa la valoración.
     */
    public void setValoracion(int valoracion) {this.valoracion = valoracion;}

    /**
     * Establece la imagen del producto.
     * @param imagen Ruta o URL de la nueva imagen.
     */
    public void setImagen(String imagen) {this.imagen = imagen;}

    /**
     * Establece la categoría a la que pertenece el producto.
     * @param categoria Objeto Categoria asociado.
     */
    public void setCategoria(Categoria categoria) {this.categoria = categoria;}

    /**
     * Establece un identificador específico para el producto.
     * @param id Nuevo identificador.
     */
    public void setId(String id) {this.id = id;}

    /**
     * Obtiene el identificador único del producto.
     * @return Identificador del producto.
     */
    public String getId() {return this.id;}

    /**
     * Obtiene el nombre del producto.
     * @return Nombre del producto.
     */
    public String getNombre() {return this.nombre;}

    /**
     * Obtiene la descripción del producto.
     * @return Descripción del producto.
     */
    public String getDescripcion() {return this.descripcion;}

    /**
     * Obtiene la valoración actual del producto.
     * @return Valoración entera.
     */
    public int getValoracion() {return this.valoracion;}

    /**
     * Obtiene la ruta de la imagen del producto.
     * @return Cadena con la ruta o URL de la imagen.
     */
    public String getImagen() {return this.imagen;}

    /**
     * Obtiene la fecha en la que se publicó el producto.
     * @return Copia defensiva de la fecha de publicación.
     */
    public Date getFechaPublicacion() {return new Date(fechaPublicacion.getTime());}

    /**
     * Obtiene la categoría del producto.
     * @return Objeto Categoria asociado al producto.
     */
    public Categoria getCategoria() {return categoria;}
}