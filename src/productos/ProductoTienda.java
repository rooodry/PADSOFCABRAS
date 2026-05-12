package productos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Producto vendido directamente por la tienda, con precio, rebajas y comentarios.
 */
public class ProductoTienda extends Producto {


    private double precio;


    private boolean tiene2x1 = false;


    private double rebajaPorcentaje = 0;


    private double rebajaFija = 0;


    private final List<String[]> comentarios = new ArrayList<>();


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
    public void setTiene2x1(boolean tiene2x1) {
        this.tiene2x1 = tiene2x1;
        if (tiene2x1) {
            this.rebajaPorcentaje = 0;
            this.rebajaFija = 0;
        }
    }

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
        if (rebajaPorcentaje > 0) {
            this.rebajaFija = 0;
            this.tiene2x1 = false;
        }
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
    public void setRebajaFija(double rebajaFija) {
        this.rebajaFija = rebajaFija;
        if (rebajaFija > 0) {
            this.rebajaPorcentaje = 0;
            this.tiene2x1 = false;
        }
    }

    /**
     * Sobrescribe la valoración para uso en productos de tienda.
     *
     * @param valoracion nueva valoración (0–5)
     */
    @Override
    public void setValoracion(int valoracion) {
        super.setValoracion(valoracion);
    }


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
