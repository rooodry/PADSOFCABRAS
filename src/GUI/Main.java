package GUI;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import intercambios.Intercambio;
import intercambios.Oferta;
import notificaciones.Notificacion;
import compras.Pedido;
import productos.Pack;
import productos.Producto;
import productos.ProductoSegundaMano;
import productos.ProductoTienda;
import productos.Stock;
import productos.categoria.Comic;
import productos.categoria.Figura;
import productos.categoria.Genero;
import productos.categoria.Juego;
import productos.categoria.TipoJuego;
import sistema.Sistema;
import usuarios.ClienteRegistrado;
import usuarios.ClienteNoRegistrado;
import utilidades.EstadoConservacion;
import utilidades.EstadoPedido;
import utilidades.EstadoProducto;
import utilidades.TipoNotificacion;

/**
 * Main Swing window for the registered-customer flow.
 *
 * <p>This class works as a small GUI controller. It owns the active customer,
 * the system, the shop stock and the screens shown in the card layout.</p>
 */
public class Main extends JFrame {

    private static final long serialVersionUID = 1L;

    /** Customer login screen. */
    public static final String PANTALLA_CLIENTE = "PANTALLA_CLIENTE";

    /** Customer registration screen. */
    public static final String PANTALLA_REGISTRO = "PANTALLA_REGISTRO";

    /** Shop catalogue screen. */
    public static final String PANTALLA_HOME = "PANTALLA_HOME";

    /** Customer basket screen. */
    public static final String PANTALLA_CESTA = "PANTALLA_CESTA";

    /** Second-hand product wallet screen. */
    public static final String PANTALLA_MIS_PRODUCTOS = "PANTALLA_MIS_PRODUCTOS";

    /** Second-hand product upload form. */
    public static final String PANTALLA_SUBIR = "PANTALLA_SUBIR";

    /** Customer profile screen. */
    public static final String PANTALLA_PERFIL = "PANTALLA_PERFIL";

    /** Product pack screen. */
    public static final String PANTALLA_PACKS = "PANTALLA_PACKS";

    /** Exchange screen. */
    public static final String PANTALLA_INTERCAMBIOS = "PANTALLA_INTERCAMBIOS";

    /** Notification inbox screen. */
    public static final String PANTALLA_NOTIFICACIONES = "PANTALLA_NOTIFICACIONES";

    /** Customer order history screen. */
    public static final String PANTALLA_PEDIDOS = "PANTALLA_PEDIDOS";

    /** Basic employee/manager management screen. */
    public static final String PANTALLA_GESTION = "PANTALLA_GESTION";

    private final Sistema sistema;
    private final Stock stock;
    private final List<ProductoTienda> productosTienda;
    private final List<Pack> packs;
    private final List<Intercambio> intercambios;
    private final List<ProductoSegundaMano> productosSegundaMano;
    private final CardLayout cardLayout;
    private final JPanel panelContenedor;

    private ClienteRegistrado clienteActual;
    private HomePanel homePanel;
    private PanelCesta panelCesta;
    private PanelMisProductos panelMisProductos;
    private PanelPerfil panelPerfil;
    private PanelPacks panelPacks;
    private PanelIntercambios panelIntercambios;
    private PanelNotificaciones panelNotificaciones;
    private PanelPedidos panelPedidos;
    private PanelGestion panelGestion;
    private ClienteNoRegistrado clienteInvitado;
    private boolean sesionRegistrada;

    /**
     * Builds the window, seed data and the registered-customer screens.
     */
    public Main() {
        setTitle("GOAT & GET");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 760);
        setLocationRelativeTo(null);

        this.sistema = new Sistema();
        this.stock = new Stock();
        this.productosTienda = new ArrayList<>();
        this.packs = new ArrayList<>();
        this.intercambios = new ArrayList<>();
        this.productosSegundaMano = new ArrayList<>();
        this.cardLayout = new CardLayout();
        this.panelContenedor = new JPanel(cardLayout);
        this.sesionRegistrada = false;

        inicializarDatos();
        construirPantallas();
        add(panelContenedor);
    }

    /**
     * Shows one screen from the card layout.
     *
     * @param nombrePantalla card name to show
     */
    public void cambiarPantalla(String nombrePantalla) {
        if (!sesionRegistrada && esPantallaSoloRegistrado(nombrePantalla)) {
            JOptionPane.showMessageDialog(this,
                    "Para acceder a esta seccion necesitas iniciar sesion o crear una cuenta.",
                    "Cliente no registrado", JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(panelContenedor, PANTALLA_CLIENTE);
            return;
        }
        refrescarPantallasConDatos();
        cardLayout.show(panelContenedor, nombrePantalla);
    }

    /**
     * Indicates whether the current session belongs to a registered customer.
     *
     * @return true for registered customer sessions
     */
    public boolean isSesionRegistrada() {
        return sesionRegistrada;
    }

    /**
     * Returns the active registered customer.
     *
     * @return active customer
     */
    public ClienteRegistrado getClienteActual() {
        return clienteActual;
    }

    /**
     * Returns a copy of the products shown in the shop catalogue.
     *
     * @return shop products
     */
    public List<ProductoTienda> getProductosTienda() {
        return new ArrayList<>(productosTienda);
    }

    /**
     * Returns the shared shop stock.
     *
     * @return shop stock
     */
    public Stock getStock() {
        return stock;
    }

    /**
     * Returns the configured packs.
     *
     * @return copy of pack list
     */
    public List<Pack> getPacks() {
        return new ArrayList<>(packs);
    }

    /**
     * Returns exchange proposals shown in the GUI.
     *
     * @return copy of exchange list
     */
    public List<Intercambio> getIntercambios() {
        return new ArrayList<>(intercambios);
    }

    /**
     * Returns public second-hand products owned by other customers.
     *
     * @return available second-hand products
     */
    public List<ProductoSegundaMano> getProductosSegundaManoDisponibles() {
        List<ProductoSegundaMano> disponibles = new ArrayList<>();
        for (ProductoSegundaMano producto : productosSegundaMano) {
            if (producto.getDisponibilidad() && producto.getPropietario() != clienteActual) {
                disponibles.add(producto);
            }
        }
        return disponibles;
    }

    /**
     * Starts a customer session.
     *
     * @param identificacion user name entered in the form
     */
    public void iniciarSesionCliente(String identificacion) {
        sesionRegistrada = true;
        if (identificacion != null && !identificacion.isBlank()) {
            clienteActual.editarPerfil(identificacion.trim(), clienteActual.getContrase\u00f1a());
        }
        cambiarPantalla(PANTALLA_HOME);
    }

    /**
     * Starts an anonymous browsing session.
     */
    public void iniciarSesionInvitado() {
        if (clienteInvitado == null) {
            clienteInvitado = new ClienteNoRegistrado("invitado", "");
            sistema.addUsuario(clienteInvitado);
        }
        sesionRegistrada = false;
        cambiarPantalla(PANTALLA_HOME);
    }

    /**
     * Starts a basic employee or manager session.
     *
     * @param rol selected role name
     */
    public void iniciarSesionGestion(String rol) {
        sesionRegistrada = true;
        cambiarPantalla(PANTALLA_GESTION);
    }

    /**
     * Registers a new customer and makes it the active session.
     *
     * @param nombre user name
     * @param contrasena password
     * @param dni identity document
     */
    public void registrarCliente(String nombre, String contrasena, String dni) {
        sesionRegistrada = true;
        clienteActual = new ClienteRegistrado(nombre, contrasena, dni);
        sistema.addUsuario(clienteActual);
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.NUEVO_DESCUENTO,
                "Bienvenido a GOAT & GET. Ya puedes revisar el catalogo."));
        if (panelMisProductos != null) {
            panelMisProductos.setCliente(clienteActual);
        }
        refrescarPantallasConDatos();
        cambiarPantalla(PANTALLA_HOME);
    }

    /**
     * Adds one shop product unit to the customer's basket and reduces stock.
     *
     * @param producto selected shop product
     */
    public void anadirProductoACesta(ProductoTienda producto) {
        if (!sesionRegistrada) {
            JOptionPane.showMessageDialog(this,
                    "Los clientes no registrados solo pueden consultar productos. Inicia sesion para comprar.",
                    "Cliente no registrado", JOptionPane.INFORMATION_MESSAGE);
            cambiarPantalla(PANTALLA_CLIENTE);
            return;
        }
        if (stock.getNumProductos(producto) <= 0) {
            JOptionPane.showMessageDialog(this, "No queda stock de este producto.", "Stock agotado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        clienteActual.a\u00f1adirALaCesta(producto, stock);
        panelCesta.refrescar();
        homePanel.refrescar();
        JOptionPane.showMessageDialog(this, "Producto anadido a la cesta.");
    }

    /**
     * Removes one product line from the basket and restores its units to stock.
     *
     * @param producto product to remove
     */
    public void retirarProductoDeCesta(ProductoTienda producto) {
        int cantidad = clienteActual.getCesta().getProductos().getOrDefault(producto, 0);
        if (cantidad > 0) {
            clienteActual.getCesta().eliminarProducto(producto);
            stock.a\u00f1adirProducto(producto, cantidad);
            refrescarPantallasConDatos();
        }
    }

    /**
     * Creates an order from the current basket.
     */
    public void finalizarCompra() {
        if (clienteActual.comprar().name().equals("OK")) {
            Pedido pedido = obtenerUltimoPedido();
            if (pedido != null) {
                sistema.addPedido(pedido);
            }
            clienteActual.addNotificacion(new Notificacion(TipoNotificacion.PAGO_REALIZADO,
                    "Tu pedido se ha creado correctamente. Puedes pagarlo desde pedidos."));
            JOptionPane.showMessageDialog(this, "Pedido creado correctamente. Revisalo en la pantalla de pedidos.");
            refrescarPantallasConDatos();
            cambiarPantalla(PANTALLA_PEDIDOS);
        } else {
            JOptionPane.showMessageDialog(this, "La cesta esta vacia.", "Cesta", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Pays an order and generates the customer pickup code notification.
     *
     * @param pedido order to pay
     */
    public void pagarPedido(Pedido pedido) {
        if (pedido == null || pedido.getEstadoPedido() != EstadoPedido.EN_CARRITO) {
            return;
        }
        pedido.setEstadoPedido(EstadoPedido.EN_PREPARACION);
        clienteActual.addCodigo(pedido.getCodigo());
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.PAGO_REALIZADO,
                "Pago realizado. Codigo de recogida: " + pedido.getCodigo().getCodigo()
                        + ". Documento: " + clienteActual.getDNI()));
        JOptionPane.showMessageDialog(this, "Pago realizado. Codigo: " + pedido.getCodigo().getCodigo());
        refrescarPantallasConDatos();
    }

    /**
     * Cancels an unpaid order and restores its units to stock.
     *
     * @param pedido order to cancel
     */
    public void cancelarPedidoCliente(Pedido pedido) {
        if (pedido == null || pedido.getEstadoPedido() != EstadoPedido.EN_CARRITO) {
            return;
        }
        for (Map.Entry<ProductoTienda, Integer> entry : pedido.getProductos().entrySet()) {
            stock.a\u00f1adirProducto(entry.getKey(), entry.getValue());
        }
        pedido.cancelar();
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.PEDIDO_EXPIRADO,
                "Pedido cancelado. Las unidades han vuelto al stock."));
        refrescarPantallasConDatos();
    }

    /**
     * Simulates the employee preparing the order.
     *
     * @param pedido order to mark as ready
     */
    public void prepararPedido(Pedido pedido) {
        if (pedido == null || pedido.getEstadoPedido() != EstadoPedido.EN_PREPARACION) {
            return;
        }
        pedido.setEstadoPedido(EstadoPedido.LISTO);
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.PEDIDO_LISTO,
                "Tu pedido esta listo para recoger con DNI " + clienteActual.getDNI()
                        + " y codigo " + pedido.getCodigo().getCodigo() + "."));
        refrescarPantallasConDatos();
    }

    /**
     * Marks a ready order as delivered.
     *
     * @param pedido order to deliver
     */
    public void entregarPedido(Pedido pedido) {
        if (pedido == null || pedido.getEstadoPedido() != EstadoPedido.LISTO) {
            return;
        }
        pedido.setEstadoPedido(EstadoPedido.ENTREGADO);
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.PAGO_REALIZADO,
                "Pedido entregado correctamente."));
        refrescarPantallasConDatos();
    }

    /**
     * Adds the products contained in a pack to the active basket.
     *
     * @param pack selected pack
     */
    public void anadirPackACesta(Pack pack) {
        for (Producto producto : pack.getProductos()) {
            if (producto instanceof ProductoTienda) {
                ProductoTienda tienda = (ProductoTienda) producto;
                if (stock.getNumProductos(tienda) > 0) {
                    clienteActual.a\u00f1adirALaCesta(tienda, stock);
                }
            }
        }
        panelCesta.refrescar();
        homePanel.refrescar();
        JOptionPane.showMessageDialog(this, "Pack anadido a la cesta.");
    }

    /**
     * Accepts an exchange proposal.
     *
     * @param intercambio exchange to accept
     */
    public void aceptarIntercambio(Intercambio intercambio) {
        intercambio.aceptarOferta();
        intercambio.getOferta().getProductoDeseado().setDisponibilidad(false);
        intercambio.getOferta().getProductoOfertado().setDisponibilidad(false);
        intercambio.getOferta().getProductoDeseado().setEstadoProducto(EstadoProducto.EN_INTERCAMBIO);
        intercambio.getOferta().getProductoOfertado().setEstadoProducto(EstadoProducto.EN_INTERCAMBIO);
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.OFERTA_ACEPTADA,
                "Has aceptado una oferta de intercambio."));
        cambiarPantalla(PANTALLA_INTERCAMBIOS);
    }

    /**
     * Rejects an exchange proposal.
     *
     * @param intercambio exchange to reject
     */
    public void rechazarIntercambio(Intercambio intercambio) {
        intercambio.rechazarOferta();
        intercambio.getOferta().getProductoOfertado().setDisponibilidad(true);
        intercambio.getOferta().getProductoOfertado().setEstadoProducto(EstadoProducto.VALORADO);
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.OFERTA_RECHAZADA,
                "Has rechazado una oferta de intercambio."));
        cambiarPantalla(PANTALLA_INTERCAMBIOS);
    }

    /**
     * Creates a new exchange offer for a public second-hand product.
     *
     * @param deseado product wanted by the active customer
     */
    public void proponerIntercambio(ProductoSegundaMano deseado) {
        ProductoSegundaMano ofertado = buscarProductoPropioParaIntercambio();
        if (ofertado == null) {
            JOptionPane.showMessageDialog(this,
                    "Necesitas tener un producto valorado y publicado para proponer intercambios.",
                    "Intercambios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ofertado.setDisponibilidad(false);
        ofertado.setEstadoProducto(EstadoProducto.EN_OFERTA);
        Oferta oferta = new Oferta(ofertado, deseado, deseado.getPropietario(), clienteActual);
        Intercambio intercambio = new Intercambio(new Date(), oferta);
        intercambios.add(intercambio);
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.NUEVA_OFERTA,
                "Has propuesto intercambiar " + ofertado.getNombre() + " por " + deseado.getNombre() + "."));
        refrescarPantallasConDatos();
    }

    /**
     * Marks a notification as read and refreshes the inbox.
     *
     * @param notificacion notification to mark
     */
    public void marcarNotificacionLeida(Notificacion notificacion) {
        if (notificacion != null) {
            notificacion.setLeida();
            refrescarPantallasConDatos();
        }
    }

    /**
     * Hides a notification from the active customer's inbox.
     *
     * @param notificacion notification to delete
     */
    public void borrarNotificacion(Notificacion notificacion) {
        if (notificacion != null) {
            notificacion.setBorrada();
            refrescarPantallasConDatos();
        }
    }

    /**
     * Returns true if the active customer has bought the given product.
     *
     * @param producto product to check
     * @return true if the product appears in a non-cancelled order
     */
    public boolean clienteHaCompradoProducto(ProductoTienda producto) {
        for (Pedido pedido : clienteActual.getPedidos()) {
            if (pedido.getEstadoPedido() != EstadoPedido.CANCELADO
                    && pedido.getProductos().containsKey(producto)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a review for a product previously bought by the active customer.
     *
     * @param producto reviewed product
     * @param valoracion score between 1 and 5
     * @param comentario review text
     */
    public void comentarYValorarProducto(ProductoTienda producto, int valoracion, String comentario) {
        if (!clienteHaCompradoProducto(producto)) {
            JOptionPane.showMessageDialog(this,
                    "Solo puedes comentar productos que hayas comprado.", "Resena",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        producto.setValoracion(Math.max(1, Math.min(5, valoracion)));
        producto.addComentario(clienteActual.getNombre(), comentario);

        for (Pedido pedido : clienteActual.getPedidos()) {
            if (pedido.getProductos().containsKey(producto)) {
                Map<ProductoTienda, Integer> valoraciones = new HashMap<>(pedido.getValoracionesProductos());
                valoraciones.put(producto, valoracion);
                pedido.setValoracionesProductos(valoraciones);
                break;
            }
        }
        refrescarPantallasConDatos();
    }

    /**
     * Creates a second-hand product and stores it in the customer's wallet.
     *
     * @param nombre product name
     * @param descripcion product description
     * @param imagen optional image path
     */
    public void anadirProductoALaCartera(String nombre, String descripcion, String imagen) {
        ProductoSegundaMano nuevo = new ProductoSegundaMano(nombre, descripcion, imagen, clienteActual);
        clienteActual.subirProducto(nuevo);
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.VALORACION_REALIZADA,
                "Producto subido. Puedes solicitar su valoracion desde cartera."));
        panelMisProductos.refrescar();
    }

    /**
     * Requests valuation for a second-hand product.
     *
     * @param producto product to value
     */
    public void solicitarValoracion(ProductoSegundaMano producto) {
        producto.pedirValoracion();
        JOptionPane.showMessageDialog(this, "Solicitud enviada. Un empleado debera valorar el producto.");
        panelMisProductos.refrescar();
    }

    /**
     * Publishes a valued second-hand product.
     *
     * @param producto product to publish
     */
    public void publicarProducto(ProductoSegundaMano producto) {
        producto.subirProducto();
        if (producto.getDisponibilidad()) {
            JOptionPane.showMessageDialog(this, "Producto publicado.");
        } else {
            JOptionPane.showMessageDialog(this, "El producto debe estar valorado antes de publicarse.",
                    "No publicado", JOptionPane.WARNING_MESSAGE);
        }
        panelMisProductos.refrescar();
    }

    /**
     * Changes the active customer's visible name.
     *
     * @param nuevoNombre new user name
     */
    public void cambiarNombreCliente(String nuevoNombre) {
        if (nuevoNombre != null && !nuevoNombre.isBlank()) {
            clienteActual.editarPerfil(nuevoNombre.trim(), clienteActual.getContrase\u00f1a());
            refrescarPantallasConDatos();
        }
    }

    /**
     * Adds stock units to a shop product from the management screen.
     *
     * @param producto product to update
     * @param unidades units to add
     */
    public void sumarStockProducto(ProductoTienda producto, int unidades) {
        if (producto != null && unidades > 0) {
            stock.a\u00f1adirProducto(producto, unidades);
            refrescarPantallasConDatos();
        }
    }

    private void construirPantallas() {
        panelContenedor.add(new LoginClientePanel(this), PANTALLA_CLIENTE);
        panelContenedor.add(new RegistroPanel(this), PANTALLA_REGISTRO);

        homePanel = new HomePanel(this);
        panelCesta = new PanelCesta(this);
        panelMisProductos = new PanelMisProductos(this, clienteActual);
        panelPerfil = new PanelPerfil(this);
        PanelSubirProducto panelSubir = new PanelSubirProducto(this);
        panelPacks = new PanelPacks(this);
        panelIntercambios = new PanelIntercambios(this);
        panelNotificaciones = new PanelNotificaciones(this);
        panelPedidos = new PanelPedidos(this);
        panelGestion = new PanelGestion(this);

        panelMisProductos.addListenerSubirProducto(e -> cambiarPantalla(PANTALLA_SUBIR));
        panelMisProductos.addListenerPedirValoracion(e -> {
            Object origen = e.getSource();
            if (origen instanceof ProductoSegundaMano) {
                solicitarValoracion((ProductoSegundaMano) origen);
            }
        });
        panelMisProductos.addListenerPublicar(e -> {
            Object origen = e.getSource();
            if (origen instanceof ProductoSegundaMano) {
                publicarProducto((ProductoSegundaMano) origen);
            }
        });

        panelContenedor.add(homePanel, PANTALLA_HOME);
        panelContenedor.add(panelCesta, PANTALLA_CESTA);
        panelContenedor.add(panelMisProductos, PANTALLA_MIS_PRODUCTOS);
        panelContenedor.add(panelSubir, PANTALLA_SUBIR);
        panelContenedor.add(panelPerfil, PANTALLA_PERFIL);
        panelContenedor.add(panelPacks, PANTALLA_PACKS);
        panelContenedor.add(panelIntercambios, PANTALLA_INTERCAMBIOS);
        panelContenedor.add(panelNotificaciones, PANTALLA_NOTIFICACIONES);
        panelContenedor.add(panelPedidos, PANTALLA_PEDIDOS);
        panelContenedor.add(panelGestion, PANTALLA_GESTION);
    }

    private boolean esPantallaSoloRegistrado(String nombrePantalla) {
        return PANTALLA_CESTA.equals(nombrePantalla)
                || PANTALLA_MIS_PRODUCTOS.equals(nombrePantalla)
                || PANTALLA_SUBIR.equals(nombrePantalla)
                || PANTALLA_PERFIL.equals(nombrePantalla)
                || PANTALLA_PACKS.equals(nombrePantalla)
                || PANTALLA_PEDIDOS.equals(nombrePantalla)
                || PANTALLA_GESTION.equals(nombrePantalla)
                || PANTALLA_INTERCAMBIOS.equals(nombrePantalla)
                || PANTALLA_NOTIFICACIONES.equals(nombrePantalla);
    }

    private void refrescarPantallasConDatos() {
        if (homePanel != null) {
            homePanel.refrescar();
        }
        if (panelCesta != null) {
            panelCesta.refrescar();
        }
        if (panelMisProductos != null) {
            panelMisProductos.refrescar();
        }
        if (panelPerfil != null) {
            panelPerfil.refrescar();
        }
        if (panelPacks != null) {
            panelPacks.refrescar();
        }
        if (panelIntercambios != null) {
            panelIntercambios.refrescar();
        }
        if (panelNotificaciones != null) {
            panelNotificaciones.refrescar();
        }
        if (panelPedidos != null) {
            panelPedidos.refrescar();
        }
        if (panelGestion != null) {
            panelGestion.refrescar();
        }
    }

    private Pedido obtenerUltimoPedido() {
        List<Pedido> pedidos = clienteActual.getPedidos();
        return pedidos.isEmpty() ? null : pedidos.get(pedidos.size() - 1);
    }

    /**
     * Returns recommended products for the active customer.
     *
     * @return products from preferred categories not bought and not in basket
     */
    public List<ProductoTienda> getProductosRecomendados() {
        List<ProductoTienda> recomendados = new ArrayList<>();
        if (!sesionRegistrada) {
            return recomendados;
        }

        Set<Class<?>> categoriasCompradas = new HashSet<>();
        Set<ProductoTienda> excluidos = new HashSet<>(clienteActual.getCesta().getProductos().keySet());
        for (Pedido pedido : clienteActual.getPedidos()) {
            for (ProductoTienda producto : pedido.getProductos().keySet()) {
                excluidos.add(producto);
                if (producto.getCategoria() != null) {
                    categoriasCompradas.add(producto.getCategoria().getClass());
                }
            }
        }

        for (ProductoTienda producto : productosTienda) {
            if (excluidos.contains(producto)) {
                continue;
            }
            if (categoriasCompradas.isEmpty()
                    || (producto.getCategoria() != null
                    && categoriasCompradas.contains(producto.getCategoria().getClass()))) {
                recomendados.add(producto);
            }
            if (recomendados.size() == 6) {
                break;
            }
        }
        return recomendados;
    }

    private void inicializarDatos() {
        clienteActual = new ClienteRegistrado("cliente", "1234", "00000000T");
        sistema.addUsuario(clienteActual);
        sistema.setStock(stock);
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.PEDIDO_LISTO,
                "Tu ultimo pedido esta listo para recoger."));
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.NUEVO_DESCUENTO,
                "Hay nuevos descuentos disponibles en comics y figuras."));
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.PAGO_REALIZADO,
                "El pedido de EAN-1467 ha sido entregado."));
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.NUEVA_OFERTA,
                "@luciaga16 te ha propuesto un intercambio."));
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.OFERTA_ACEPTADA,
                "@luciaga16 ha aceptado el intercambio."));
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.OFERTA_RECHAZADA,
                "@luciaga16 ha rechazado el intercambio."));
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.INTERCAMBIO_REALIZADO,
                "El intercambio con @luciaga16 se ha realizado."));

        ProductoSegundaMano valorado = new ProductoSegundaMano("Comic Avengers 1963",
                "Ejemplar conservado en funda desde la compra.", null, clienteActual);
        valorado.setValoracion(4, 18.50, EstadoConservacion.MUY_BUENO);
        valorado.subirProducto();
        clienteActual.getCartera().a\u00f1adirProducto(valorado);

        ProductoSegundaMano pendiente = new ProductoSegundaMano("Figura coleccionista",
                "Figura sin caja original, con pequenos signos de uso.", null, clienteActual);
        clienteActual.getCartera().a\u00f1adirProducto(pendiente);

        cargarCatalogoDesdeCsv();
        aplicarPromocionesIniciales();
        crearPacksDesdeCatalogo();

        ClienteRegistrado otroCliente = new ClienteRegistrado("laura67", "1234", "11111111H");
        ProductoSegundaMano deseado = new ProductoSegundaMano("Comic X-Men 1992",
                "Comic valorado por otro usuario.", null, otroCliente);
        deseado.setValoracion(4, 21.00, EstadoConservacion.MUY_BUENO);
        deseado.subirProducto();
        productosSegundaMano.add(deseado);
        ProductoSegundaMano juegoRetro = new ProductoSegundaMano("Caja Zelda coleccionista",
                "Edicion de segunda mano valorada, con caja y manual. Disponible para intercambio.", null, otroCliente);
        juegoRetro.setValoracion(5, 35.00, EstadoConservacion.PERFECTO);
        juegoRetro.subirProducto();
        productosSegundaMano.add(juegoRetro);
        Oferta oferta = new Oferta(deseado, pendiente, clienteActual, otroCliente);
        intercambios.add(new Intercambio(new Date(), oferta));
    }

    private ProductoSegundaMano buscarProductoPropioParaIntercambio() {
        for (ProductoSegundaMano producto : clienteActual.getCartera().getProductos()) {
            if (producto.getDisponibilidad() && producto.getEstadoProducto() == EstadoProducto.VALORADO) {
                return producto;
            }
        }
        return null;
    }

    private void aplicarPromocionesIniciales() {
        for (int i = 0; i < productosTienda.size(); i++) {
            ProductoTienda producto = productosTienda.get(i);
            if (i % 7 == 0) {
                producto.setRebajaPorcentaje(10.0);
            } else if (i % 9 == 0) {
                producto.setRebajaFija(3.0);
            } else if (i % 11 == 0) {
                producto.setTiene2x1(true);
            }
        }
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.NUEVO_DESCUENTO,
                "Nuevos descuentos configurados en comics, juegos y figuras del catalogo."));
    }

    private void cargarCatalogoDesdeCsv() {
        productosTienda.clear();

        File archivo = new File("productos.csv");
        if (!archivo.exists()) {
            archivo = new File("..", "productos.csv");
        }

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int indice = 0;
            boolean esCabecera = true;

            while ((linea = lector.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                if (esCabecera) {
                    esCabecera = false;
                    continue;
                }

                ProductoTienda productoTienda = crearProductoDesdeLineaCsv(linea, indice);
                if (productoTienda != null) {
                    int unidades = parseIntCsv(campo(linea.split(";", -1), 5), 1);
                    registrarProductoTienda(productoTienda, unidades);
                    indice++;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "No se ha podido cargar productos.csv. Revisa que el archivo este en la raiz del proyecto.",
                    "Catalogo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private ProductoTienda crearProductoDesdeLineaCsv(String linea, int indice) {
        String[] datos = linea.split(";", -1);
        if (datos.length < 6) {
            return null;
        }

        String tipo = campo(datos, 0).toUpperCase();
        String id = campo(datos, 1);
        String nombre = campo(datos, 2);
        String descripcion = campo(datos, 3);
        double precio = parseDoubleCsv(campo(datos, 4), 0.0);

        if (nombre.isBlank()) {
            return null;
        }

        ProductoTienda producto = new ProductoTienda(nombre, descripcion, "");
        producto.setId(id.isBlank() ? producto.getId() : id);
        producto.setPrecio(precio);
        producto.setValoracion(3 + (indice % 3));

        if ("C".equals(tipo)) {
            producto.setCategoria(new Comic(nombre, parseIntCsv(campo(datos, 7), 120),
                    campoConDefecto(datos, 8, "Autor desconocido"),
                    campoConDefecto(datos, 9, "Editorial desconocida"),
                    resolverGeneroCsv(campo(datos, 6)),
                    parseIntCsv(campo(datos, 10), 2024)));
        } else if ("J".equals(tipo)) {
            producto.setCategoria(new Juego(nombre,
                    parseIntCsv(campo(datos, 11), 4),
                    parseIntCsv(campo(datos, 12), 8),
                    resolverTipoJuegoCsv(campo(datos, 13))));
        } else if ("F".equals(tipo)) {
            producto.setCategoria(new Figura(nombre,
                    parseDoubleCsv(campo(datos, 16), 10.0),
                    campoConDefecto(datos, 14, "GOAT"),
                    campoConDefecto(datos, 15, "PVC")));
        }

        producto.setImagen(crearPortadaParaProducto(producto));
        producto.addComentario("juan15",
                "Un producto muy interesante para ampliar la coleccion y revisar con calma en la ficha.");
        producto.addComentario("laura67",
                "La descripcion es completa y ayuda bastante a decidir si encaja con lo que buscas.");
        producto.addComentario("alex",
                "Buena relacion entre precio, presentacion y disponibilidad en tienda.");
        return producto;
    }

    private String campo(String[] datos, int indice) {
        return indice >= 0 && indice < datos.length && datos[indice] != null ? datos[indice].trim() : "";
    }

    private String campoConDefecto(String[] datos, int indice, String defecto) {
        String valor = campo(datos, indice);
        return valor.isBlank() ? defecto : valor;
    }

    private int parseIntCsv(String valor, int defecto) {
        if (valor == null || valor.isBlank()) {
            return defecto;
        }

        StringBuilder digitos = new StringBuilder();
        boolean leyendoNumero = false;
        for (int i = 0; i < valor.length(); i++) {
            char caracter = valor.charAt(i);
            if (Character.isDigit(caracter)) {
                digitos.append(caracter);
                leyendoNumero = true;
            } else if (leyendoNumero) {
                break;
            }
        }

        if (digitos.length() == 0) {
            return defecto;
        }

        try {
            return Integer.parseInt(digitos.toString());
        } catch (NumberFormatException e) {
            return defecto;
        }
    }

    private double parseDoubleCsv(String valor, double defecto) {
        if (valor == null || valor.isBlank()) {
            return defecto;
        }

        String normalizado = valor.replace(',', '.').replaceAll("[^0-9.]", "");
        if (normalizado.isBlank()) {
            return defecto;
        }

        try {
            return Double.parseDouble(normalizado);
        } catch (NumberFormatException e) {
            return defecto;
        }
    }

    private Genero resolverGeneroCsv(String categorias) {
        String normalizado = categorias == null ? "" : categorias.toLowerCase();
        if (normalizado.contains("romance")) {
            return Genero.ROMANCE;
        }
        if (normalizado.contains("comedia") || normalizado.contains("humor")) {
            return Genero.COMEDIA;
        }
        return Genero.AVENTURA;
    }

    private TipoJuego resolverTipoJuegoCsv(String estilo) {
        String normalizado = estilo == null ? "" : estilo.toLowerCase();
        if (normalizado.contains("carta")) {
            return TipoJuego.CARTAS;
        }
        if (normalizado.contains("dado")) {
            return TipoJuego.DADOS;
        }
        return TipoJuego.JUEGO_MESA;
    }

    private void crearPacksDesdeCatalogo() {
        packs.clear();
        if (productosTienda.size() >= 2) {
            packs.add(new Pack("Pack seleccion inicial", 24.90,
                    listaProductos(productosTienda.get(0), productosTienda.get(1))));
        }
        if (productosTienda.size() >= 4) {
            packs.add(new Pack("Pack mesa y vitrina", 59.90,
                    listaProductos(productosTienda.get(1), productosTienda.get(2), productosTienda.get(3))));
        }
        if (productosTienda.size() >= 6) {
            packs.add(new Pack("Pack coleccion GOAT", 89.90,
                    listaProductos(productosTienda.get(0), productosTienda.get(4), productosTienda.get(5))));
        }
        if (productosTienda.size() >= 9) {
            packs.add(new Pack("Pack familiar ampliado", 99.90,
                    listaProductos(productosTienda.get(6), productosTienda.get(7), productosTienda.get(8))));
        }
    }

    private String crearPortadaParaProducto(ProductoTienda producto) {
        if (producto.getCategoria() instanceof Comic) {
            return crearPortada(producto.getNombre(), new Color(191, 55, 42), new Color(34, 64, 116));
        }
        if (producto.getCategoria() instanceof Juego) {
            return crearPortada(producto.getNombre(), new Color(49, 92, 80), new Color(222, 156, 62));
        }
        if (producto.getCategoria() instanceof Figura) {
            return crearPortada(producto.getNombre(), new Color(112, 88, 140), new Color(44, 42, 54));
        }
        return crearPortada(producto.getNombre(), UiStyle.COLOR_CABECERA, UiStyle.COLOR_TEXTO);
    }

    private void registrarProductoTienda(ProductoTienda producto, int unidades) {
        productosTienda.add(producto);
        sistema.addProducto(producto);
        stock.a\u00f1adirProducto(producto, unidades);
    }

    private List<Producto> listaProductos(Producto... productos) {
        List<Producto> resultado = new ArrayList<>();
        for (Producto producto : productos) {
            resultado.add(producto);
        }
        return resultado;
    }

    private ProductoTienda crearComic(String nombre, String descripcion, double precio, int valoracion, Genero genero) {
        ProductoTienda producto = new ProductoTienda(nombre, descripcion,
                crearPortada(nombre, new Color(191, 55, 42), new Color(34, 64, 116)));
        producto.setPrecio(precio);
        producto.setValoracion(valoracion);
        producto.setCategoria(new Comic(nombre, 192, "Autor", "Editorial", genero, 2024));
        producto.addComentario("marta", "Muy buena edicion para coleccion.");
        producto.addComentario("juan15", "Me ha sorprendido el nivel de detalle y lo bien que queda expuesto.");
        producto.addComentario("laura67", "Una compra recomendable si buscas algo vistoso y con buena presentacion.");
        return producto;
    }

    private ProductoTienda crearJuego(String nombre, String descripcion, double precio, int valoracion, TipoJuego tipo) {
        ProductoTienda producto = new ProductoTienda(nombre, descripcion,
                crearPortada(nombre, new Color(49, 92, 80), new Color(222, 156, 62)));
        producto.setPrecio(precio);
        producto.setValoracion(valoracion);
        producto.setCategoria(new Juego(nombre, 4, 8, tipo));
        producto.addComentario("alex", "Perfecto para partidas largas.");
        producto.addComentario("lucia16", "Las reglas entran rapido y tiene suficiente profundidad para repetir.");
        producto.addComentario("diego", "Buen ritmo en mesa y componentes resistentes.");
        return producto;
    }

    private ProductoTienda crearFigura(String nombre, String descripcion, double precio, int valoracion) {
        ProductoTienda producto = new ProductoTienda(nombre, descripcion,
                crearPortada(nombre, new Color(112, 88, 140), new Color(44, 42, 54)));
        producto.setPrecio(precio);
        producto.setValoracion(valoracion);
        producto.setCategoria(new Figura(nombre, 15.0, "GOAT", "PVC"));
        producto.addComentario("laura", "Llego en buen estado y bien protegida.");
        producto.addComentario("mario", "La figura tiene presencia y se sostiene bastante bien.");
        producto.addComentario("nerea", "El color se ve mejor en persona que en la ficha.");
        return producto;
    }

    private String crearPortada(String titulo, Color colorPrincipal, Color colorSecundario) {
        try {
            File directorio = new File("bin/gui-assets");
            if (!directorio.exists() && !directorio.mkdirs()) {
                return null;
            }

            String nombreArchivo = titulo.replaceAll("[^a-zA-Z0-9]+", "_").toLowerCase() + ".png";
            File destino = new File(directorio, nombreArchivo);
            if (destino.exists()) {
                return destino.getPath();
            }

            int ancho = 300;
            int alto = 404;
            BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = imagen.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(colorPrincipal);
            g2.fillRect(0, 0, ancho, alto);
            g2.setColor(colorSecundario);
            g2.fillRoundRect(24, 28, ancho - 48, alto - 56, 28, 28);
            g2.setColor(new Color(255, 245, 220));
            g2.fillRoundRect(44, 52, ancho - 88, 76, 22, 22);
            g2.setColor(colorPrincipal.darker());
            g2.fillOval(62, 160, 176, 142);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 26));
            dibujarTextoCentrado(g2, titulo.toUpperCase(), 58, 68, ancho - 116, 110);
            g2.setFont(new Font("SansSerif", Font.BOLD, 22));
            g2.drawString("GOAT & GET", 70, alto - 48);
            g2.dispose();
            ImageIO.write(imagen, "png", destino);
            return destino.getPath();
        } catch (IOException e) {
            return null;
        }
    }

    private void dibujarTextoCentrado(Graphics2D g2, String texto, int x, int y, int ancho, int alto) {
        FontMetrics metrics = g2.getFontMetrics();
        List<String> lineas = new ArrayList<>();
        StringBuilder linea = new StringBuilder();
        for (String palabra : texto.split(" ")) {
            String candidata = linea.length() == 0 ? palabra : linea + " " + palabra;
            if (metrics.stringWidth(candidata) > ancho && linea.length() > 0) {
                lineas.add(linea.toString());
                linea = new StringBuilder(palabra);
            } else {
                linea = new StringBuilder(candidata);
            }
        }
        if (linea.length() > 0) {
            lineas.add(linea.toString());
        }

        int altoLinea = metrics.getHeight();
        int yInicial = y + Math.max(0, (alto - altoLinea * lineas.size()) / 2) + metrics.getAscent();
        for (int i = 0; i < lineas.size(); i++) {
            String actual = lineas.get(i);
            int xTexto = x + (ancho - metrics.stringWidth(actual)) / 2;
            g2.drawString(actual, xTexto, yInicial + i * altoLinea);
        }
    }

    /**
     * Application entry point.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
