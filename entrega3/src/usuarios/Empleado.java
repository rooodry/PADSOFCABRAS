package usuarios;

import java.util.*;
import utilidades.TiposEmpleado;
import intercambios.Intercambio;
import productos.ProductoSegundaMano;

public class Empleado extends Usuario {

    private Set<TiposEmpleado> permisos;
    private List<ProductoSegundaMano> productosParaValorar;
    private List<Intercambio> intercambios;

    public Empleado(String nombreUsuario, String contraseña) {
        super(nombreUsuario, contraseña);
        this.permisos = new HashSet<>();
        
        this.productosParaValorar = new ArrayList<>();
        this.intercambios = new ArrayList<>();
    }

    public void addPermiso(TiposEmpleado tipo) { permisos.add(tipo); }
    public void removePermiso(TiposEmpleado tipo) { permisos.remove(tipo); }
    public void clearPermisos() { permisos.clear(); } 
    public boolean tienePermiso(TiposEmpleado tipo) { return permisos.contains(tipo); }
    public Set<TiposEmpleado> getPermisos() { return Collections.unmodifiableSet(permisos); }
    
    public void addProductoParaValorar(ProductoSegundaMano p) { 
        this.productosParaValorar.add(p); 
    }
    
    public void addIntercambio(Intercambio i) { 
        this.intercambios.add(i); 
    }
    
    public List<ProductoSegundaMano> getProductosParaValorar() { 
        return new ArrayList<>(this.productosParaValorar); 
    }
    
    public List<Intercambio> getIntercambios() { 
        return new ArrayList<>(this.intercambios); 
    }
}