package usuarios;

import java.util.*;
import utilidades.TiposEmpleado;
import intercambios.Intercambio;
import productos.ProductoSegundaMano;

/**
 * Representa a un empleado de la tienda con un conjunto configurable de permisos.
 *
 * <p>Extiende {@link Usuario} añadiendo:</p>
 * <ul>
 *   <li>Un conjunto de {@link TiposEmpleado permisos} que determinan las
 *       operaciones que el empleado puede realizar (p. ej. gestión de intercambios).</li>
 *   <li>Una lista de {@link ProductoSegundaMano productos de segunda mano}
 *       pendientes de valorar asignados a este empleado.</li>
 *   <li>Una lista de {@link Intercambio intercambios} que gestiona este empleado.</li>
 * </ul>
 *
 * <p>Los permisos se exponen a través de una vista no modificable mediante
 * {@link Collections#unmodifiableSet(Set)}. Los accesores de listas devuelven
 * copias defensivas.</p>
 *
 * @see Gestor#configurarPermisos(Empleado, Set)
 * @see TiposEmpleado
 */
public class Empleado extends Usuario {

    /** Conjunto de permisos asignados al empleado. */
    private Set<TiposEmpleado> permisos;

    /** Productos de segunda mano pendientes de valoración asignados a este empleado. */
    private List<ProductoSegundaMano> productosParaValorar;

    /** Intercambios de segunda mano asignados a este empleado para su gestión. */
    private List<Intercambio> intercambios;

    /**
     * Construye un empleado sin permisos y con listas vacías.
     *
     * @param nombreUsuario nombre de usuario del empleado
     * @param contraseña    contraseña de acceso del empleado
     */
    public Empleado(String nombreUsuario, String contraseña) {
        super(nombreUsuario, contraseña);
        this.permisos              = new HashSet<>();
        this.productosParaValorar  = new ArrayList<>();
        this.intercambios          = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Gestión de permisos
    // -------------------------------------------------------------------------

    /**
     * Añade un permiso al conjunto del empleado.
     *
     * @param tipo tipo de permiso a añadir
     */
    public void addPermiso(TiposEmpleado tipo) {
        permisos.add(tipo);
    }

    /**
     * Elimina un permiso del conjunto del empleado.
     *
     * <p>Si el permiso no estaba asignado, la operación no tiene efecto.</p>
     *
     * @param tipo tipo de permiso a eliminar
     */
    public void removePermiso(TiposEmpleado tipo) {
        permisos.remove(tipo);
    }

    /**
     * Elimina todos los permisos del empleado.
     *
     * <p>Utilizado por {@link Gestor#configurarPermisos(Empleado, Set)} antes
     * de asignar el nuevo conjunto de permisos.</p>
     */
    public void clearPermisos() {
        permisos.clear();
    }

    /**
     * Comprueba si el empleado tiene un permiso concreto.
     *
     * @param tipo tipo de permiso a verificar
     * @return {@code true} si el empleado tiene el permiso; {@code false} en caso contrario
     */
    public boolean tienePermiso(TiposEmpleado tipo) {
        return permisos.contains(tipo);
    }

    /**
     * Devuelve el conjunto de permisos del empleado como vista no modificable.
     *
     * @return vista no modificable del conjunto de permisos
     */
    public Set<TiposEmpleado> getPermisos() {
        return Collections.unmodifiableSet(permisos);
    }

    // -------------------------------------------------------------------------
    // Gestión de valoraciones e intercambios
    // -------------------------------------------------------------------------

    /**
     * Asigna un producto de segunda mano a este empleado para su valoración.
     *
     * @param p producto de segunda mano pendiente de valorar
     */
    public void addProductoParaValorar(ProductoSegundaMano p) {
        this.productosParaValorar.add(p);
    }

    /**
     * Asigna un intercambio a este empleado para que lo gestione.
     *
     * @param i intercambio a gestionar
     */
    public void addIntercambio(Intercambio i) {
        this.intercambios.add(i);
    }

    /**
     * Devuelve la lista de productos de segunda mano pendientes de valorar
     * asignados a este empleado.
     *
     * @return copia defensiva de la lista de productos para valorar
     */
    public List<ProductoSegundaMano> getProductosParaValorar() {
        return new ArrayList<>(this.productosParaValorar);
    }

    /**
     * Devuelve la lista de intercambios asignados a este empleado.
     *
     * @return copia defensiva de la lista de intercambios
     */
    public List<Intercambio> getIntercambios() {
        return new ArrayList<>(this.intercambios);
    }
}