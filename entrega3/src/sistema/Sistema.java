package sistema;

import productos.*;
import usuarios.*;
import descuentos.*;
import excepciones.*;
import notificaciones.Notificacion;
import compras.*;
import utilidades.*;
import intercambios.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Sistema {
    
    private List<Producto> productos;
    private List<Descuento> descuentos;
    private List<Usuario> usuarios;
    private List<Notificacion> notificaciones;
    private List<Pedido> pedidos;
    private Stock stock;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public Sistema() {
        this.productos = new ArrayList<Producto>();
        this.descuentos = new ArrayList<Descuento>();
        this.usuarios = new ArrayList<Usuario>();
        this.notificaciones = new ArrayList<Notificacion>();
        this.pedidos = new ArrayList<Pedido>();
        this.stock = null;
    }


    //SETTERS//
    public void addProducto(Producto p) {this.productos.add(p);}
    public void addDescuento(Descuento d) {this.descuentos.add(d);}
    public void addUsuario(Usuario u) {this.usuarios.add(u);}
    public void addNotificacion(Notificacion n) {this.notificaciones.add(n);}
    public void addPedido(Pedido p) {this.pedidos.add(p);}


    public void darAltaEmpleado(Usuario admin, String nombreEmpleado, String contraseña, TiposEmpleado tipo) throws ExcepcionUsuariosAdmin {
        
        Empleado e = null;

        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }

        if(tipo == TiposEmpleado.EMPLEADOS_INTERCAMBIO) {
            e = new EmpleadoIntercambio(nombreEmpleado, contraseña);
        } else if (tipo == TiposEmpleado.EMPLEADOS_PEDIDO) {
            e = new EmpleadoPedido(nombreEmpleado, contraseña);
        } else {
            e = new EmpleadoProducto(nombreEmpleado, contraseña, this.stock);
        }

        this.usuarios.add(e);

    }

    public void darBajaEmpleado(Usuario admin, Empleado e) throws ExcepcionUsuariosAdmin {
        
        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        this.usuarios.remove(e);
    }

    public Empleado modificarPermiso(Usuario admin, Empleado e, TiposEmpleado tipo) throws ExcepcionUsuariosAdmin {
        
        Empleado emp;

        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }

        if(tipo == TiposEmpleado.EMPLEADOS_INTERCAMBIO) {
            emp = new EmpleadoIntercambio(e.getNombre(), e.getContraseña());
            this.usuarios.remove(e);
        } else if (tipo == TiposEmpleado.EMPLEADOS_PEDIDO) {
            emp = new EmpleadoPedido(e.getNombre(), e.getContraseña());
            this.usuarios.remove(e);
        } else {
            emp = new EmpleadoProducto(e.getNombre(), e.getContraseña(), this.stock);
            this.usuarios.remove(e);
        }

        this.usuarios.add(emp);

        return emp;
    }

    public Pack crearPack(Usuario admin, String nombre, double precio, List<Producto> productos) throws ExcepcionUsuariosAdmin {
        
        Pack pack;
        
        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }

        return pack = new Pack(nombre, precio, productos);

    }

    public Codigo generarCodigo() {

        Codigo codigo;

        return codigo = new Codigo();
    }

    public void actualizarStock(Usuario admin, ProductoTienda p, int cantidad) throws ExcepcionUsuariosAdmin {

        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        
        if(cantidad > 0) {
            this.stock.añadirProducto(p, cantidad);
        } else if (cantidad < 0) {
            this.stock.reducirStock(p, cantidad);
        } else {
            this.stock.retirarProducto(p);
        }
        
    }

    public void registrarPedido(Pedido p) {
        this.pedidos.add(p);
        
        scheduler.schedule(() -> {
            if (p.getEstadoPedido() == EstadoPedido.EN_PREPARACION) {
                cancelarPedido(p);
            }
        }, 15, TimeUnit.MINUTES);    
    }

    public void cancelarPedido(Pedido p) {

        for(Map.Entry<ProductoTienda, Integer> entry : p.getProductos().entrySet()) {
            ProductoTienda producto = entry.getKey();
            
            this.stock.retirarProducto(producto);
        }
        this.pedidos.remove(p);
        p.cancelar();
    }

    public void enviarCodigo(ClienteRegistrado c, Codigo cod) {
        c.addCodigo(cod);
    }

    public void setEstadoPedido(Pedido p, EstadoPedido e) {
        p.setEstadoPedido(e);
    }

    public void notificarUsuario(Usuario u, Notificacion n) {
        u.addNotificacion(n);
    }

    public void asignarValoracion(ProductoSegundaMano p, EmpleadoIntercambio e) {
        e.addProductoParaValorar(p);
    }

    public void asignarIntercambio(Intercambio i, EmpleadoIntercambio e) {
        e.addIntercambio(i);
    }

    public void añadirProductoCartera(ProductoSegundaMano p, ClienteRegistrado c) {
        c.getCartera().añadirProducto(p);
    }

    public void bloquearProductoOfertante(ProductoSegundaMano p) {
        p.setDisponibilidad(false);
    }

    
}





