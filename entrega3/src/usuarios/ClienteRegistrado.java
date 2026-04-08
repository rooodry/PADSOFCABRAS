package usuarios;

import java.util.*;
import productos.*;
import compras.*;
import intercambios.*;
import utilidades.*;
import notificaciones.Notificacion;

/**
 * Representa a un cliente registrado en el sistema con acceso a todas las
 * funcionalidades de compra, valoración e intercambio.
 *
 * <p>Extiende {@link Cliente} añadiendo:</p>
 * <ul>
 *   <li>Identificación por DNI (validado en el constructor).</li>
 *   <li>{@link Cartera} de productos de segunda mano propios.</li>
 *   <li>{@link Cesta} de compra activa.</li>
 *   <li>Historial de {@link Pedido pedidos} realizados.</li>
 *   <li>Listas de {@link Oferta ofertas} enviadas y recibidas.</li>
 *   <li>Lista de {@link Intercambio intercambios} en los que participa.</li>
 *   <li>Lista de {@link Codigo códigos} de descuento disponibles.</li>
 * </ul>
 *
 * <p>Todos los accesores de colecciones devuelven copias defensivas.</p>
 */
public class ClienteRegistrado extends Cliente {

    /** DNI del cliente, validado al construir el objeto. */
    private String DNI;

    /** Cartera con los productos de segunda mano del cliente. */
    private Cartera cartera;

    /** Cesta de compra activa con los productos pendientes de pago. */
    private Cesta cesta;

    /** Historial de pedidos realizados por el cliente. */
    private List<Pedido> pedidos;

    /** Ofertas de intercambio enviadas por el cliente a otros usuarios. */
    private List<Oferta> ofertasRealizadas;

    /** Ofertas de intercambio recibidas de otros usuarios. */
    private List<Oferta> ofertasRecibidas;

    /** Intercambios de productos de segunda mano en los que participa el cliente. */
    private List<Intercambio> intercambios;

    /** Códigos promocionales o de descuento disponibles para el cliente. */
    private List<Codigo> codigos;

    /**
     * Construye un cliente registrado con nombre de usuario, contraseña y DNI.
     *
     * <p>Si el DNI no supera la validación ({@link #validarDNI(String)}),
     * se almacena un espacio en blanco como valor de sustitución.</p>
     *
     * @param nombreUsuario nombre de usuario del cliente
     * @param contraseña    contraseña de acceso
     * @param DNI           documento nacional de identidad del cliente (9 caracteres)
     */
    public ClienteRegistrado(String nombreUsuario, String contraseña, String DNI) {
        super(nombreUsuario, contraseña);
        this.DNI = validarDNI(DNI) ? DNI : " ";
        this.cartera          = new Cartera();
        this.cesta            = new Cesta();
        this.pedidos          = new ArrayList<>();
        this.ofertasRealizadas = new ArrayList<>();
        this.ofertasRecibidas  = new ArrayList<>();
        this.intercambios     = new ArrayList<>();
        this.codigos          = new ArrayList<>();
    }

    /**
     * Actualiza el nombre de usuario y la contraseña del cliente.
     *
     * @param nuevoNombre    nuevo nombre de usuario
     * @param nuevaContraseña nueva contraseña
     */
    public void editarPerfil(String nuevoNombre, String nuevaContraseña) {
        this.setNombreUsuario(nuevoNombre);
        this.setContraseña(nuevaContraseña);
    }

    /**
     * Añade un producto de tienda a la cesta si hay unidades disponibles en
     * el stock, y reduce el stock en una unidad.
     *
     * @param producto producto de tienda a añadir a la cesta
     * @param stock    almacén del que se descontará la unidad reservada
     */
    public void añadirALaCesta(ProductoTienda producto, Stock stock) {
        if (stock.getNumProductos(producto) > 0) {
            this.cesta.añadirProducto(producto, 1);
            stock.reducirStock(producto, 1);
        }
    }

    /**
     * Añade un código promocional a la lista del cliente.
     *
     * @param c código a añadir
     */
    public void addCodigo(Codigo c) {
        this.codigos.add(c);
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /** @return DNI del cliente */
    public String getDNI() { return this.DNI; }

    /** @return cartera de productos de segunda mano del cliente */
    public Cartera getCartera() { return this.cartera; }

    /** @return cesta de compra activa del cliente */
    public Cesta getCesta() { return this.cesta; }

    /** @return copia defensiva del historial de pedidos */
    public List<Pedido> getPedidos() { return new ArrayList<>(this.pedidos); }

    /** @return copia defensiva de las ofertas realizadas por el cliente */
    public List<Oferta> getOfertasRealizadas() { return new ArrayList<>(this.ofertasRealizadas); }

    /** @return copia defensiva de las ofertas recibidas por el cliente */
    public List<Oferta> getOfertasRecibidas() { return new ArrayList<>(this.ofertasRecibidas); }

    /** @return copia defensiva de los intercambios del cliente */
    public List<Intercambio> getIntercambios() { return new ArrayList<>(this.intercambios); }

    /** @return copia defensiva de los códigos promocionales disponibles */
    public List<Codigo> getCodigos() { return new ArrayList<>(this.codigos); }

    // -------------------------------------------------------------------------
    // Operaciones de compra
    // -------------------------------------------------------------------------

    /**
     * Crea un pedido a partir del contenido actual de la cesta y la vacía.
     *
     * <p>Si la cesta está vacía, la operación no crea ningún pedido y devuelve
     * {@link Status#ERROR}.</p>
     *
     * @return {@link Status#OK} si el pedido se creó correctamente;
     *         {@link Status#ERROR} si la cesta estaba vacía
     */
    public Status comprar() {
        if (this.cesta.estaVacia()) {
            return Status.ERROR;
        }
        Pedido nuevoPedido = new Pedido(this, this.cesta.getProductos());
        this.pedidos.add(nuevoPedido);
        this.cesta.limpiarCesta();
        return Status.OK;
    }

    /**
     * Realiza el pago de un pedido mediante el sistema de pagos externo
     * {@link TeleChargeAndPaySystem}.
     *
     * <p>Pasos:</p>
     * <ol>
     *   <li>Comprueba que el pedido pertenece al cliente.</li>
     *   <li>Valida el número de tarjeta.</li>
     *   <li>Realiza el cargo por el total del pedido.</li>
     *   <li>Si todo es correcto, cambia el estado del pedido a
     *       {@link EstadoPedido#EN_PREPARACION}.</li>
     * </ol>
     *
     * @param pedido        pedido a pagar; debe pertenecer al cliente
     * @param numeroTarjeta número de tarjeta de crédito/débito con la que pagar
     * @return {@link Status#OK} si el pago se procesó correctamente;
     *         {@link Status#ERROR} si el pedido no pertenece al cliente,
     *         el número de tarjeta no es válido, hay un fallo de conexión
     *         o el pago es rechazado
     */
    public Status pagarPedido(Pedido pedido, String numeroTarjeta) {
        if (!this.pedidos.contains(pedido)) {
            return Status.ERROR;
        }
        try {
            if (!TeleChargeAndPaySystem.isValidCardNumber(numeroTarjeta)) {
                System.out.println("El número de tarjeta no es válido.");
                return Status.ERROR;
            }
            TeleChargeAndPaySystem.charge(
                numeroTarjeta,
                "Pago de pedido",
                pedido.calcularPrecioTotal(),
                true
            );
            pedido.setEstadoPedido(EstadoPedido.EN_PREPARACION);
            return Status.OK;
        } catch (InvalidCardNumberException e) {
            System.err.println("Error: El número de tarjeta es inválido.");
            return Status.ERROR;
        } catch (FailedInternetConnectionException e) {
            System.err.println("Error: Fallo de conexión a internet al pagar.");
            return Status.ERROR;
        } catch (OrderRejectedException e) {
            System.err.println("Error: El pago del pedido ha sido rechazado.");
            return Status.ERROR;
        }
    }

    /**
     * Paga la tasación de un producto de segunda mano de la cartera del cliente.
     *
     * <p>El coste fijo de la tasación es de <strong>10,00 €</strong>. Si el
     * pago se realiza correctamente, se solicita la valoración del producto
     * mediante {@link ProductoSegundaMano#pedirValoracion()}.</p>
     *
     * @param p             producto de segunda mano a tasar; debe estar en la cartera
     * @param numeroTarjeta número de tarjeta con la que realizar el pago
     * @return {@link Status#OK} si el pago de la tasación fue correcto;
     *         {@link Status#ERROR} si el producto no está en la cartera,
     *         la tarjeta no es válida, hay fallo de conexión o el pago es rechazado
     */
    public Status pagarValoracion(ProductoSegundaMano p, String numeroTarjeta) {
        if (!this.cartera.getProductos().contains(p)) {
            return Status.ERROR;
        }
        try {
            if (!TeleChargeAndPaySystem.isValidCardNumber(numeroTarjeta)) {
                System.out.println("El número de tarjeta no es válido.");
                return Status.ERROR;
            }
            TeleChargeAndPaySystem.charge(
                numeroTarjeta,
                "Pago por tasacion de producto",
                10.0,
                true
            );
            p.pedirValoracion();
            return Status.OK;
        } catch (InvalidCardNumberException e) {
            System.err.println("Error: El número de tarjeta es inválido.");
            return Status.ERROR;
        } catch (FailedInternetConnectionException e) {
            System.err.println("Error: Fallo de conexión a internet al pagar.");
            return Status.ERROR;
        } catch (OrderRejectedException e) {
            System.err.println("Error: El pago de la tasación ha sido rechazado.");
            return Status.ERROR;
        }
    }

    // -------------------------------------------------------------------------
    // Gestión de notificaciones
    // -------------------------------------------------------------------------

    /**
     * Marca una notificación como leída si pertenece al cliente.
     *
     * @param notificacion notificación a marcar como leída
     */
    public void leerNotificacion(Notificacion notificacion) {
        if (this.getNotificaciones().contains(notificacion)) {
            notificacion.setLeida();
        }
    }

    /**
     * Elimina una notificación de la lista del cliente.
     *
     * @param notificacion notificación a eliminar
     */
    public void borrarNotificacion(Notificacion notificacion) {
        this.removeNotificacion(notificacion);
    }

    // -------------------------------------------------------------------------
    // Gestión de productos de segunda mano
    // -------------------------------------------------------------------------

    /**
     * Sube un producto de segunda mano a la cartera del cliente para
     * ponerlo en venta o intercambio.
     *
     * @param p producto de segunda mano a añadir a la cartera
     * @return {@link Status#OK} siempre (operación sin condiciones de error)
     */
    public Status subirProducto(ProductoSegundaMano p) {
        this.cartera.añadirProducto(p);
        return Status.OK;
    }

    /**
     * Asocia un mapa de valoraciones (producto → puntuación) a un pedido del cliente.
     *
     * @param p     pedido al que se añaden las valoraciones
     * @param lista mapa con los productos del pedido y su valoración (1-5 típicamente)
     */
    public void añadirValoraciones(Pedido p, Map<ProductoTienda, Integer> lista) {
        p.setValoracionesProductos(lista);
    }

    // -------------------------------------------------------------------------
    // Validación
    // -------------------------------------------------------------------------

    /**
     * Valida que el DNI tenga exactamente 9 caracteres.
     *
     * <p><b>Nota:</b> la validación actual es superficial (solo comprueba longitud).
     * No verifica el formato NIF completo (letra de control, dígitos, etc.).</p>
     *
     * @param dni cadena con el DNI a validar
     * @return {@code true} si tiene 9 caracteres; {@code false} en caso contrario
     */
    public boolean validarDNI(String dni) {
        return dni.length() == 9;
    }
}