package productos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa un producto vendido directamente por la tienda.
 *
 * <p>Extiende {@link Producto} añadiendo precio, promociones (2×1, rebaja
 * porcentual y rebaja fija) y una lista de comentarios de usuarios.</p>
 */
public class ProductoTienda extends Producto {

    /** Precio base del producto en euros. */
    private double precio;

    /** {@code true} si el producto tiene activa la promoción 2×1. */
    private boolean tiene2x1 = false;

    /** Porcentaje de descuento sobre el precio base (0–100). */
    private double rebajaPorcentaje = 0;

    /** Descuento fijo en euros que se resta al precio base. */
    private double rebajaFija = 0;

    /**
     * Lista de comentarios de usuarios.
     * Cada elemento es un array {@code [nombreUsuario, texto]}.
     */
    private final List<String[]> comentarios = new ArrayList<>();

    // ------------------------------------------------------------------ //
    //  Constructor                                                       //
    // ------------------------------------------------------------------ //

    /**
     * Crea un producto de tienda con los datos básicos heredados de {@link Producto}.
     *
     * @param nombre      nombre del producto
     * @param descripcion descripción detallada
     * @param imagen      ruta o URL de la imagen del producto
     */
    public ProductoTienda(String nombre, String descripcion, String imagen) {
        super(nombre, descripcion, imagen);
    }

    // ------------------------------------------------------------------ //
    //  Getters y setters                                                 //
    // ------------------------------------------------------------------ //

    /**
     * Devuelve el precio base del producto.
     *
     * @return precio en euros
     */
    public double getPrecio() { return this.precio; }

    /**
     * Establece el precio base del producto.
     *
     * @param precio precio en euros (debe ser &ge; 0)
     */
    public void setPrecio(double precio) { this.precio = precio; }

    /**
     * Indica si la promoción 2×1 está activa.
     *
     * @return {@code true} si el producto tiene 2×1
     */
    public boolean isTiene2x1() { return tiene2x1; }

    /**
     * Activa o desactiva la promoción 2×1.
     *
     * @param tiene2x1 nuevo estado de la promoción
     */
    public void setTiene2x1(boolean tiene2x1) { this.tiene2x1 = tiene2x1; }

    /**
     * Devuelve el porcentaje de rebaja aplicado.
     *
     * @return porcentaje de descuento (0–100)
     */
    public double getRebajaPorcentaje() { return rebajaPorcentaje; }

    /**
     * Establece un descuento en porcentaje sobre el precio base.
     *
     * @param rebajaPorcentaje porcentaje a descontar (0–100)
     */
    public void setRebajaPorcentaje(double rebajaPorcentaje) {
        this.rebajaPorcentaje = rebajaPorcentaje;
    }

    /**
     * Devuelve la rebaja fija aplicada al producto.
     *
     * @return descuento fijo en euros
     */
    public double getRebajaFija() { return rebajaFija; }

    /**
     * Establece una rebaja de importe fijo sobre el precio base.
     *
     * @param rebajaFija descuento fijo en euros
     */
    public void setRebajaFija(double rebajaFija) { this.rebajaFija = rebajaFija; }

    /**
     * Sobrescribe la valoración para uso en productos de tienda.
     *
     * @param valoracion nueva valoración (0–5)
     */
    @Override
    public void setValoracion(int valoracion) {
        super.setValoracion(valoracion);
    }

    // ------------------------------------------------------------------ //
    //  Comentarios                                                       //
    // ------------------------------------------------------------------ //

    /**
     * Añade un comentario de usuario al producto.
     *
     * @param nombreUsuario nombre del usuario que realiza el comentario
     * @param texto         contenido del comentario
     */
    public void addComentario(String nombreUsuario, String texto) {
        comentarios.add(new String[]{nombreUsuario, texto});
    }

    /**
     * Devuelve los comentarios del producto como lista no modificable.
     * Cada elemento es un array {@code [nombreUsuario, texto]}.
     *
     * @return lista inmutable de comentarios
     */
    public List<String[]> getComentarios() {
        return Collections.unmodifiableList(comentarios);
    }
}
