package usuarios;

import estadisticas.Estadistica;
import utilidades.TiposEmpleado;
import java.util.*;

/**
 * Representa al gestor (administrador) del sistema con privilegios para
 * gestionar empleados y acceder a estadísticas.
 *
 * <p>Extiende {@link Usuario} y es el único rol autorizado para realizar
 * operaciones de administración a través de {@code Sistema}, tales como
 * dar de alta/baja empleados, modificar permisos o actualizar el stock.</p>
 *
 * <p>Además, mantiene una lista de {@link Estadistica estadísticas} que
 * ha generado o tiene asignadas.</p>
 *
 * <p>Los accesores devuelven copias defensivas de las colecciones internas.</p>
 *
 * @see sistema.Sistema#darAltaEmpleado
 * @see sistema.Sistema#modificarPermisos
 */
public class Gestor extends Usuario {

    /** Lista de estadísticas generadas o asignadas al gestor. */
    private List<Estadistica> estadisticas;

    /**
     * Construye un gestor con las credenciales indicadas y sin estadísticas.
     *
     * @param nombre     nombre de usuario del gestor
     * @param contraseña contraseña de acceso del gestor
     */
    public Gestor(String nombre, String contraseña) {
        super(nombre, contraseña);
        this.estadisticas = new ArrayList<>();
    }

    /**
     * Añade una estadística a la lista del gestor.
     *
     * @param e estadística a añadir; no debe ser {@code null}
     */
    public void addEstadistica(Estadistica e) {
        this.estadisticas.add(e);
    }

    /**
     * Devuelve la lista de estadísticas del gestor.
     *
     * @return copia defensiva de la lista de estadísticas; nunca {@code null}
     */
    public List<Estadistica> getEstadisticas() {
        return new ArrayList<>(this.estadisticas);
    }

    /**
     * Reemplaza el conjunto completo de permisos de un empleado por el
     * nuevo conjunto proporcionado.
     *
     * <p>Primero elimina todos los permisos actuales del empleado mediante
     * {@link Empleado#clearPermisos()} y a continuación asigna uno a uno
     * los permisos del nuevo conjunto.</p>
     *
     * @param empleado       empleado cuyos permisos se van a reconfigurar
     * @param nuevosPermisos nuevo conjunto de tipos de permiso a asignar
     */
    public void configurarPermisos(Empleado empleado, Set<TiposEmpleado> nuevosPermisos) {
        empleado.clearPermisos();
        for (TiposEmpleado t : nuevosPermisos) {
            empleado.addPermiso(t);
        }
    }
}