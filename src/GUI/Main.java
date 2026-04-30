package GUI;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import intercambios.Intercambio;
import intercambios.Oferta;
import notificaciones.Notificacion;
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
import utilidades.EstadoConservacion;
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

    private final Sistema sistema;
    private final Stock stock;
    private final List<ProductoTienda> productosTienda;
    private final List<Pack> packs;
    private final List<Intercambio> intercambios;
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
        this.cardLayout = new CardLayout();
        this.panelContenedor = new JPanel(cardLayout);

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
        refrescarPantallasConDatos();
        cardLayout.show(panelContenedor, nombrePantalla);
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
     * Starts a customer session.
     *
     * @param identificacion user name entered in the form
     */
    public void iniciarSesionCliente(String identificacion) {
        if (identificacion != null && !identificacion.isBlank()) {
            clienteActual.editarPerfil(identificacion.trim(), clienteActual.getContrase\u00f1a());
        }
        cambiarPantalla(PANTALLA_HOME);
    }

    /**
     * Registers a new customer and makes it the active session.
     *
     * @param nombre user name
     * @param contrasena password
     * @param dni identity document
     */
    public void registrarCliente(String nombre, String contrasena, String dni) {
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
            clienteActual.addNotificacion(new Notificacion(TipoNotificacion.PAGO_REALIZADO,
                    "Tu pedido se ha creado correctamente."));
            JOptionPane.showMessageDialog(this, "Pedido creado correctamente.");
            refrescarPantallasConDatos();
            cambiarPantalla(PANTALLA_HOME);
        } else {
            JOptionPane.showMessageDialog(this, "La cesta esta vacia.", "Cesta", JOptionPane.WARNING_MESSAGE);
        }
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
        clienteActual.addNotificacion(new Notificacion(TipoNotificacion.OFERTA_RECHAZADA,
                "Has rechazado una oferta de intercambio."));
        cambiarPantalla(PANTALLA_INTERCAMBIOS);
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
        clienteActual.getCartera().a\u00f1adirProducto(valorado);

        ProductoSegundaMano pendiente = new ProductoSegundaMano("Figura coleccionista",
                "Figura sin caja original, con pequenos signos de uso.", null, clienteActual);
        clienteActual.getCartera().a\u00f1adirProducto(pendiente);

        ProductoTienda dragonBall = crearComic("Dragon Ball, Vol. 1",
                "Aventuras de Goku en el inicio de una de las sagas mas conocidas del manga.",
                13.00, 5, Genero.AVENTURA);
        registrarProductoTienda(dragonBall, 12);
        ProductoTienda risk = crearJuego("Risk",
                "Juego de conquista y estrategia para competir por el control global.",
                44.80, 4, TipoJuego.JUEGO_MESA);
        registrarProductoTienda(risk, 8);
        ProductoTienda funko = crearFigura("Funko Star Wars Pop! Maul",
                "Figura coleccionable de Darth Maul con sable laser de doble hoja.",
                13.00, 3);
        registrarProductoTienda(funko, 20);
        ProductoTienda monopoly = crearJuego("Monopoly",
                "Clasico juego de mesa de compra de propiedades y gestion economica.",
                33.50, 4, TipoJuego.JUEGO_MESA);
        registrarProductoTienda(monopoly, 10);
        ProductoTienda mazinger = crearFigura("Mazinger Z",
                "Figura articulada del legendario robot Mazinger Z.",
                128.40, 5);
        registrarProductoTienda(mazinger, 2);
        ProductoTienda oishinbo = crearComic("Oishinbo: Cocina japonesa",
                "Manga gastronomico centrado en la cultura culinaria japonesa.",
                12.00, 4, Genero.COMEDIA);
        registrarProductoTienda(oishinbo, 5);

        packs.add(new Pack("Pack estrategia", 69.90, listaProductos(risk, monopoly)));
        packs.add(new Pack("Pack coleccionista", 139.90, listaProductos(funko, mazinger)));
        packs.add(new Pack("Pack manga inicial", 22.50, listaProductos(dragonBall, oishinbo)));

        ClienteRegistrado otroCliente = new ClienteRegistrado("laura67", "1234", "11111111H");
        ProductoSegundaMano deseado = new ProductoSegundaMano("Comic X-Men 1992",
                "Comic valorado por otro usuario.", null, otroCliente);
        deseado.setValoracion(4, 21.00, EstadoConservacion.MUY_BUENO);
        Oferta oferta = new Oferta(deseado, pendiente, clienteActual, otroCliente);
        intercambios.add(new Intercambio(new Date(), oferta));
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
        ProductoTienda producto = new ProductoTienda(nombre, descripcion, null);
        producto.setPrecio(precio);
        producto.setValoracion(valoracion);
        producto.setCategoria(new Comic(nombre, 192, "Autor", "Editorial", genero, 2024));
        producto.addComentario("marta", "Muy buena edicion para coleccion.");
        return producto;
    }

    private ProductoTienda crearJuego(String nombre, String descripcion, double precio, int valoracion, TipoJuego tipo) {
        ProductoTienda producto = new ProductoTienda(nombre, descripcion, null);
        producto.setPrecio(precio);
        producto.setValoracion(valoracion);
        producto.setCategoria(new Juego(nombre, 4, 8, tipo));
        producto.addComentario("alex", "Perfecto para partidas largas.");
        return producto;
    }

    private ProductoTienda crearFigura(String nombre, String descripcion, double precio, int valoracion) {
        ProductoTienda producto = new ProductoTienda(nombre, descripcion, null);
        producto.setPrecio(precio);
        producto.setValoracion(valoracion);
        producto.setCategoria(new Figura(nombre, 15.0, "GOAT", "PVC"));
        producto.addComentario("laura", "Llego en buen estado y bien protegida.");
        return producto;
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
