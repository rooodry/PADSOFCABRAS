import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import productos.ProductoTienda;
import GUI.PanelProductos;

/**
 * Punto de entrada para probar la interfaz gráfica de la tienda GOAT &amp; GET.
 *
 * <p>Carga un conjunto de productos de ejemplo y lanza la ventana principal
 * con el {@link PanelProductos}. Al pulsar sobre un producto se abre el
 * {@link GUI.PanelDeProducto} con su detalle completo.</p>
 */
public class Main2 {

    /** Lista de productos de tienda cargados al inicio. */
    private static List<ProductoTienda> productos;

    /** Constructor privado: clase de utilidad, no instanciable. */
    private Main2() {}

    /**
     * Método principal: carga los datos y arranca la GUI en el hilo de Swing.
     *
     * @param args argumentos de línea de comandos (no se usan)
     */
    public static void main(String[] args) {
        cargarProductos();
        SwingUtilities.invokeLater(Main2::inicializarGUI);
    }

    /**
     * Crea y rellena la lista de productos de ejemplo con datos representativos.
     */
    private static void cargarProductos() {
        productos = new ArrayList<>();

        ProductoTienda avengers = new ProductoTienda(
                "The Avengers comic 1965-1967",
                "Kang el Conquistador transporta a los Vengadores al siglo XL para demostrar "
                + "su poderío militar y conquistar el reino de la princesa Ravonna. El equipo, "
                + "formado por el Capitán América, Hawkeye, Scarlet Witch y Quicksilver, se ve "
                + "superado por la avanzada tecnología del villano.",
                null
        );
        avengers.setPrecio(15.99);
        avengers.setValoracion(4);
        avengers.addComentario("juan15",
                "¡Un clásico total! Me encanta ver al \"Cuarteto Loco\" de Cap tan vulnerable "
                + "y humano frente a la tecnología de Kang.");
        avengers.addComentario("laura67",
                "¡Qué gran portada! Me fascina cómo Kang domina la escena, recordándonos que "
                + "estos Vengadores son simples mortales frente a su tecnología.");

        ProductoTienda spiderman = new ProductoTienda(
                "The Amazing Spider-Man #1",
                "Primera aparición en solitario del Hombre Araña. Peter Parker se enfrenta "
                + "al Camaleón en una emocionante historia de Stan Lee y Steve Ditko.",
                null
        );
        spiderman.setPrecio(24.99);
        spiderman.setValoracion(5);
        spiderman.addComentario("carlos_m",
                "El cómic que lo empezó todo. Un tesoro para cualquier coleccionista.");

        ProductoTienda xmen = new ProductoTienda(
                "X-Men #94 - New Team",
                "La nueva formación de los X-Men con Wolverine, Coloso y Tormenta.",
                null
        );
        xmen.setPrecio(19.50);
        xmen.setValoracion(3);

        productos.add(avengers);
        productos.add(spiderman);
        productos.add(xmen);
    }

    /**
     * Construye la ventana principal y la hace visible en el EDT de Swing.
     */
    private static void inicializarGUI() {
        JFrame ventana = new JFrame("GOAT & GET");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.add(new PanelProductos("Catálogo", productos));
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }
}
