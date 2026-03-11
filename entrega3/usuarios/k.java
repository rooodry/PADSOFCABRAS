package usuarios;

import java.util.ArrayList;
import java.util.List;
import productos.Producto;
import productos.ProductoSegundaMano;
// Asume que tienes estos paquetes creados según tu arquitectura:
import compras.Cesta;
import compras.Pedido;
import intercambios.Oferta;
import intercambios.Intercambio;
import utilidades.status; // El enum de status { ERROR, OK }
import notificaciones.Notificacion;

/**
 * Clase que representa a un cliente registrado en el sistema.
 * Hereda de Cliente (que a su vez hereda de Usuario).
 */
public class ClienteRegistrado extends Cliente {
    
    // Atributos propios
    private String DNI;
    
    // Relaciones (Asociaciones y Composiciones del diagrama de clases)
    private Cartera cartera; // "m1=cartera 1"
    private Cesta carrito;   // "m2=1 carrito"
    private List<Pedido> pedidos;
    private List<Notificacion> notificaciones;
    private List<Oferta> ofertasHechas;
    private List<Oferta> ofertasRecibidas;
    private List<Intercambio> intercambiosPendientes;

    /**
     * Constructor de ClienteRegistrado
     */
    public ClienteRegistrado(String nombreUsuario, String contraseña, String DNI) {
        super(nombreUsuario, contraseña); // Llama al constructor de Cliente/Usuario
        this.DNI = DNI;
        
        // Inicialización de sus componentes
        this.cartera = new Cartera();
        this.carrito = new Cesta();
        this.pedidos = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
        this.ofertasHechas = new ArrayList<>();
        this.ofertasRecibidas = new ArrayList<>();
        this.intercambiosPendientes = new ArrayList<>();
    }

    // --- MÉTODOS DEFINIDOS EN EL DIAGRAMA ---

    public void añadirALaCesta(Producto p) {
        // Delega la acción a su clase Cesta, pidiendo 1 unidad por defecto
        this.carrito.comprobarStock(p, 1);
        System.out.println(p.getNombre() + " añadido a la cesta.");
    }

    public status comprar() {
        if (this.carrito.estaVacio()) { // Asumiendo que Cesta tiene este método
            return status.ERROR;
        }
        // Se genera el pedido con los productos de la cesta
        Pedido nuevoPedido = new Pedido(this.carrito);
        this.pedidos.add(nuevoPedido);
        
        // La cesta se vacía al procesar la compra
        this.carrito.limpiarCarrito();
        return status.OK;
    }

    public status pagarPedido(Pedido p) {
        if (this.pedidos.contains(p)) {
            // Lógica de pago. Si es exitoso, cambia el estado del pedido
            p.setEstadoPedido(estadoPedido.EN_PREPARACION);
            return status.OK;
        }
        return status.ERROR;
    }

    public void editarPerfil(String nuevoNombre, String nuevaContraseña) {
        // Asumiendo setters en la clase padre Usuario
        this.setNombreUsuario(nuevoNombre);
        this.setContraseña(nuevaContraseña);
        System.out.println("Perfil actualizado con éxito.");
    }

    public void leerNotificaicion(Notificacion n) {
        if (this.notificaciones.contains(n)) {
            n.marcarComoLeida(); // Asumiendo este método en la clase Notificacion
        }
    }

    public void borrarNotificacion(Notificacion n) {
        this.notificaciones.remove(n);
        System.out.println("Notificación borrada.");
    }

    public status subirProducto(Producto p) {
        if (p instanceof ProductoSegundaMano) {
            ProductoSegundaMano p2m = (ProductoSegundaMano) p;
            this.cartera.añadirProducto(p2m);
            return status.OK;
        }
        System.out.println("Solo se pueden subir productos de segunda mano a la cartera.");
        return status.ERROR;
    }

    public status pagarValoracion(Producto p) {
        if (p instanceof ProductoSegundaMano) {
            ProductoSegundaMano p2m = (ProductoSegundaMano) p;
            // Verifica que el producto esté en su cartera antes de pagar
            if (this.cartera.getProductosPrivados().contains(p2m)) {
                p2m.pedirValoracion();
                return status.OK;
            }
        }
        return status.ERROR;
    }

    // --- GETTERS Y SETTERS BÁSICOS ---
    
    public String getDNI() { return DNI; }
    public Cartera getCartera() { return cartera; }
    public Cesta getCarrito() { return carrito; }
    public List<Pedido> getPedidos() { return pedidos; }
    public List<Notificacion> getNotificaciones() { return notificaciones; }
}