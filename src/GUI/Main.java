package GUI;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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

/**
 * Ventana principal de la aplicacion Swing para el flujo de cliente registrado.
 *
 * <p>Actua como controlador sencillo de la interfaz: mantiene la sesion del
 * cliente, el sistema, el stock de tienda y las pantallas principales. Los
 * paneles delegan aqui las operaciones que modifican el modelo para que la GUI
 * no trabaje con datos duplicados.</p>
 */
public class Main extends JFrame {

    private static final long serialVersionUID = 1L;

    /** Pantalla de login de cliente. */
    public static final String PANTALLA_CLIENTE = "PANTALLA_CLIENTE";

    /** Pantalla de registro. */
    public static final String PANTALLA_REGISTRO = "PANTALLA_REGISTRO";

    /** Catalogo de productos de tienda. */
    public static final String PANTALLA_HOME = "PANTALLA_HOME";

    /** Cesta del cliente. */
    public static final String PANTALLA_CESTA = "PANTALLA_CESTA";

    /** Cartera de productos de segunda mano. */
    public static final String PANTALLA_MIS_PRODUCTOS = "PANTALLA_MIS_PRODUCTOS";

    /** Formulario de subida de producto de segunda mano. */
    public static final String PANTALLA_SUBIR = "PANTALLA_SUBIR";

    /** Perfil y ajustes basicos del cliente. */
    public static final String PANTALLA_PERFIL = "PANTALLA_PERFIL";

    private final Sistema sistema;
    private final Stock stock;
    private final List<ProductoTienda> productosTienda;
    private final CardLayout cardLayout;
    private final JPanel panelContenedor;

    private ClienteRegistrado clienteActual;
    private HomePanel homePanel;
    private PanelCesta panelCesta;
    private PanelMisProductos panelMisProductos;
    private PanelPerfil panelPerfil;

    /**
     * Crea la ventana, los datos iniciales de demostracion y las pantallas.
     */
    public Main() {
        setTitle("GOAT & GET");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 760);
        setLocationRelativeTo(null);

<<<<<<< HEAD
        this.sistema = new Sistema();
        this.stock = new Stock();
        this.productosTienda = new ArrayList<>();
        this.cardLayout = new CardLayout();
        this.panelContenedor = new JPanel(cardLayout);
=======
        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        // Añadimos todas las pantallas a la "baraja"
        panelContenedor.add(new LoginClientePanel(this), "PANTALLA_CLIENTE");
        panelContenedor.add(new LoginGestorPanel(this), "PANTALLA_GESTOR");
        panelContenedor.add(new RegistroPanel(this), "PANTALLA_REGISTRO");
        // Añadimos todas las pantallas a la "baraja"
        panelContenedor.add(new LoginEmpleadoPanel(this), "PANTALLA_EMPLEADO");
        panelContenedor.add(new HomePanel(this), "PANTALLA_HOME");
        
        // Añadimos la nueva pantalla HOME
        panelContenedor.add(new HomePanel(this), "PANTALLA_HOME");
>>>>>>> 334c8ba93cb956f4c93ad768b9567505ab7036e9

        inicializarDatos();
        construirPantallas();
        add(panelContenedor);
    }

    /**
     * Cambia la pantalla visible del {@link CardLayout}.
     *
     * @param nombrePantalla clave de la pantalla que se desea mostrar
     */
    public void cambiarPantalla(String nombrePantalla) {
        refrescarPantallasConDatos();
        cardLayout.show(panelContenedor, nombrePantalla);
    }

    /**
     * Devuelve el cliente actualmente autenticado.
     *
     * @return cliente registrado de la sesion
     */
    public ClienteRegistrado getClienteActual() {
        return clienteActual;
    }

    /**
     * Devuelve los productos de tienda mostrados en el catalogo.
     *
     * @return copia de la lista de productos disponibles
     */
    public List<ProductoTienda> getProductosTienda() {
        return new ArrayList<>(productosTienda);
    }

    /**
     * Devuelve el stock real usado por la aplicacion.
     *
     * @return stock de tienda
     */
    public Stock getStock() {
        return stock;
    }

    /**
     * Inicia sesion como cliente registrado.
     *
     * @param identificacion nombre introducido en el formulario; si esta vacio se
     *                       mantiene el cliente de prueba
     */
    public void iniciarSesionCliente(String identificacion) {
        if (identificacion != null && !identificacion.isBlank()) {
            clienteActual.editarPerfil(identificacion.trim(), clienteActual.getContraseña());
        }
        cambiarPantalla(PANTALLA_HOME);
    }

    /**
     * Registra un nuevo cliente y lo deja como sesion activa.
     *
     * @param nombre      nombre de usuario
     * @param contrasena  contrasena de acceso
     * @param dni         documento de identidad
     */
    public void registrarCliente(String nombre, String contrasena, String dni) {
        clienteActual = new ClienteRegistrado(nombre, contrasena, dni);
        sistema.addUsuario(clienteActual);
        if (panelMisProductos != null) {
            panelMisProductos.setCliente(clienteActual);
        }
        refrescarPantallasConDatos();
        cambiarPantalla(PANTALLA_HOME);
    }

    /**
     * Anade un producto de tienda a la cesta del cliente y descuenta stock.
     *
     * @param producto producto que se desea comprar
     */
    public void anadirProductoACesta(ProductoTienda producto) {
        if (stock.getNumProductos(producto) <= 0) {
            JOptionPane.showMessageDialog(this, "No queda stock de este producto.", "Stock agotado",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        clienteActual.añadirALaCesta(producto, stock);
        panelCesta.refrescar();
        homePanel.refrescar();
        JOptionPane.showMessageDialog(this, "Producto anadido a la cesta.");
    }

    /**
     * Retira completamente un producto de la cesta y devuelve sus unidades al stock.
     *
     * @param producto producto que se retira de la cesta
     */
    public void retirarProductoDeCesta(ProductoTienda producto) {
        int cantidad = clienteActual.getCesta().getProductos().getOrDefault(producto, 0);
        if (cantidad > 0) {
            clienteActual.getCesta().eliminarProducto(producto);
            stock.añadirProducto(producto, cantidad);
            refrescarPantallasConDatos();
        }
    }

    /**
     * Formaliza la compra de los productos de la cesta.
     */
    public void finalizarCompra() {
        if (clienteActual.comprar().name().equals("OK")) {
            JOptionPane.showMessageDialog(this, "Pedido creado correctamente.");
            refrescarPantallasConDatos();
            cambiarPantalla(PANTALLA_HOME);
        } else {
            JOptionPane.showMessageDialog(this, "La cesta esta vacia.", "Cesta", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Crea y guarda un producto de segunda mano en la cartera del cliente.
     *
     * @param nombre      nombre del producto
     * @param descripcion descripcion introducida por el cliente
     * @param imagen      ruta de imagen opcional
     */
    public void anadirProductoALaCartera(String nombre, String descripcion, String imagen) {
        ProductoSegundaMano nuevo = new ProductoSegundaMano(nombre, descripcion, imagen, clienteActual);
        clienteActual.subirProducto(nuevo);
        panelMisProductos.refrescar();
    }

    /**
     * Solicita la valoracion de un producto de segunda mano.
     *
     * @param producto producto del cliente
     */
    public void solicitarValoracion(ProductoSegundaMano producto) {
        producto.pedirValoracion();
        JOptionPane.showMessageDialog(this, "Solicitud enviada. Un empleado debera valorar el producto.");
        panelMisProductos.refrescar();
    }

    /**
     * Publica un producto ya valorado en el mercado de segunda mano.
     *
     * @param producto producto a publicar
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
     * Actualiza el nombre visible del cliente.
     *
     * @param nuevoNombre nuevo nombre de usuario
     */
    public void cambiarNombreCliente(String nuevoNombre) {
        if (nuevoNombre != null && !nuevoNombre.isBlank()) {
            clienteActual.editarPerfil(nuevoNombre.trim(), clienteActual.getContraseña());
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
    }

    private void inicializarDatos() {
        clienteActual = new ClienteRegistrado("cliente", "1234", "00000000T");
        sistema.addUsuario(clienteActual);
        sistema.setStock(stock);

        ProductoSegundaMano valorado = new ProductoSegundaMano("Comic Avengers 1963",
                "Ejemplar conservado en funda desde la compra.", null, clienteActual);
        valorado.setValoracion(4, 18.50, EstadoConservacion.MUY_BUENO);
        clienteActual.getCartera().añadirProducto(valorado);

        ProductoSegundaMano pendiente = new ProductoSegundaMano("Figura coleccionista",
                "Figura sin caja original, con pequenos signos de uso.", null, clienteActual);
        clienteActual.getCartera().añadirProducto(pendiente);

        registrarProductoTienda(crearComic("Dragon Ball, Vol. 1",
                "Aventuras de Goku en el inicio de una de las sagas mas conocidas del manga.",
                13.00, 5, Genero.AVENTURA), 12);
        registrarProductoTienda(crearJuego("Risk",
                "Juego de conquista y estrategia para competir por el control global.",
                44.80, 4, TipoJuego.JUEGO_MESA), 8);
        registrarProductoTienda(crearFigura("Funko Star Wars Pop! Maul",
                "Figura coleccionable de Darth Maul con sable laser de doble hoja.",
                13.00, 3), 20);
        registrarProductoTienda(crearJuego("Monopoly",
                "Clasico juego de mesa de compra de propiedades y gestion economica.",
                33.50, 4, TipoJuego.JUEGO_MESA), 10);
        registrarProductoTienda(crearFigura("Mazinger Z",
                "Figura articulada del legendario robot Mazinger Z.",
                128.40, 5), 2);
        registrarProductoTienda(crearComic("Oishinbo: Cocina japonesa",
                "Manga gastronomico centrado en la cultura culinaria japonesa.",
                12.00, 4, Genero.COMEDIA), 5);
    }

    private void registrarProductoTienda(ProductoTienda producto, int unidades) {
        productosTienda.add(producto);
        sistema.addProducto(producto);
        stock.añadirProducto(producto, unidades);
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
     * Punto de entrada de la aplicacion grafica.
     *
     * @param args argumentos de linea de comandos no utilizados
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}
