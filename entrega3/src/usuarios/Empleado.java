package usuarios;

import java.util.*;
import utilidades.TiposEmpleado;

public class Empleado extends Usuario {

    private Set<TiposEmpleado> permisos;

    public Empleado(String nombreUsuario, String contraseña) {
        super(nombreUsuario, contraseña);
        this.permisos = new HashSet<>();
    }

    public void addPermiso(TiposEmpleado tipo) { permisos.add(tipo); }
    
    public void removePermiso(TiposEmpleado tipo) { permisos.remove(tipo); }
    
    public void clearPermisos() { permisos.clear(); } 
    
    public boolean tienePermiso(TiposEmpleado tipo) { return permisos.contains(tipo); }
    
    public Set<TiposEmpleado> getPermisos() { return Collections.unmodifiableSet(permisos); }
}