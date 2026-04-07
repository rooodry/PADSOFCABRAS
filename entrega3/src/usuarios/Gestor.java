package usuarios;

import estadisticas.Estadistica;
import utilidades.TiposEmpleado;

import java.util.*;

public class Gestor extends Usuario {

    private List<Estadistica> estadisticas;
    
    public Gestor(String nombre, String contraseña) {
        super(nombre, contraseña);
        this.estadisticas = new ArrayList<>();
    }

    public void addEstadistica(Estadistica e) { this.estadisticas.add(e); }
    
    public List<Estadistica> getEstadisticas() { return new ArrayList<>(this.estadisticas); }
    
    public void configurarPermisos(Empleado empleado, Set<TiposEmpleado> nuevosPermisos) {
        empleado.clearPermisos();
        for (TiposEmpleado t : nuevosPermisos) {
            empleado.addPermiso(t);
        }
    }
}