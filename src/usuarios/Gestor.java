package usuarios;

import estadisticas.Estadistica;
import utilidades.TiposEmpleado;
import java.util.*;

/**
 * Usuario administrador capaz de consultar estadisticas y configurar empleados.
 */
public class Gestor extends Usuario {


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
