package sistema;

import productos.*;
import usuarios.*;
import descuentos.*;
import notificaciones.Notificacion;
import compras.*;
import utilidades.*;

import java.util.List;
import java.util.ArrayList;

public class Sistema {
    
    private List<Producto> productos;
    private List<Descuento> descuentos;
    private List<Usuario> usuarios;
    private List<Notificacion> notificaciones;
    private List<Pedido> pedidos;
    private Stock stock;


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


    public void darAltaEmpleado(Usuario admin, String nombreEmpleado, String contraseña, TiposEmpleado tipo) {
        
        Empleado e = null;

        if(!(admin instanceof Gestor)) {
            System.err.println("Usuario no es gestor");
            return;
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

    public void darBajaEmpleado(Usuario admin, Empleado e) {
        
        if(!(admin instanceof Gestor)) {
            System.err.println("Usuario no es gestor");
            return;
        }
        this.usuarios.remove(e);
    }

    public Empleado modificarPermiso(Usuario admin, Empleado e, TiposEmpleado tipo) {
        
        Empleado emp;

        if(!(admin instanceof Gestor)) {
            System.err.println("Usuario no es gestor");
            return null;
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

    public Pack crearPack(Usuario admin, String nombre, double precio, List<Producto> productos) {
        
        Pack pack;
        
        if(!(admin instanceof Gestor)) {
            System.err.println("Usuario no es gestor");
            return null;
        }

        return pack = new Pack(nombre, precio, productos);

    }

    public Codigo generarCodigo(ClienteRegistrado c, Pedido p) {
        
    }


}





+ actualizarStock(admin: Usuario, p: Producto, cantidad: int): void
+ registrarPedido(Pedido: p): void
+ generarCodigo(Cliente_Registrado: c, Pedido: p): Codigo
+ enviarCodigo(Cliente_Registrado: c, Codigo cod): void
+ setEstadoPedido(Pedido: p, estadoPedido: e): void
+ notificarUsuario(Usuario: u, Notificacion: n): void
+ asignarValoracion(Producto: p, Empleado: e): void
+ asignarEmpleado(i: Intercambio, e: EmpleadoIntercambio): void
+ añadirProductoCartera(Producto: p, Cliente_Registrado: c): void
+ iniciarGestionIntercambio(i: Intercambio): void
+ cancelarPedido(p: Pedido): void
+ eliminarPedidoDeLista(p: Pedido): void
+ bloquearProductoOfertante(p: ProductoSegundaMano): void
+ bloquearProductoDeseado(p: ProductoSegundaMano): void
+ desbloquearProductoOfertante(p: ProductoSegundaMano): void
+ desbloquearProductoDeseado(p: ProductoSegundaMano): void
