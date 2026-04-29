package GUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class HomePanel extends JPanel {

    private Main mainFrame;
    private Color colorMarron = new Color(153, 133, 112); // Color principal de la imagen

    public HomePanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 1. CABECERA (Top Bar)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(colorMarron);
        headerPanel.setPreferredSize(new Dimension(0, 50));
        headerPanel.setBorder(new EmptyBorder(5, 15, 5, 15));

        JLabel menuIcon = new JLabel("☰"); // Icono hamburguesa
        menuIcon.setFont(new Font("SansSerif", Font.BOLD, 30));
        menuIcon.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("GOAT & GET", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel iconsRight = new JLabel("🔔 🐐"); // Iconos derecha
        iconsRight.setFont(new Font("SansSerif", Font.PLAIN, 24));
        iconsRight.setForeground(Color.WHITE);

        headerPanel.add(menuIcon, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(iconsRight, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. MENÚ LATERAL (Sidebar)
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new GridLayout(6, 1, 0, 2)); // 6 filas, 1 columna, separación vertical
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setPreferredSize(new Dimension(200, 0));

        String[] menuItems = {"HOME", "CARTERA", "CESTA", "INTERCAMBIOS", "PACKS"};
        for (String item : menuItems) {
            JButton menuBtn = new JButton(item);
            menuBtn.setBackground(colorMarron);
            menuBtn.setForeground(Color.WHITE);
            menuBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
            menuBtn.setFocusPainted(false);
            menuBtn.setBorder(new LineBorder(Color.WHITE, 1));
            sidebarPanel.add(menuBtn);
        }
        // Espacio vacío al final del menú
        JPanel emptySidebarSpace = new JPanel();
        emptySidebarSpace.setBackground(colorMarron);
        sidebarPanel.add(emptySidebarSpace);

        add(sidebarPanel, BorderLayout.WEST);

        // 3. CONTENIDO CENTRAL (Grid de Cómics)
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        
        JLabel homeTitle = new JLabel("HOME", SwingConstants.CENTER);
        homeTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        homeTitle.setForeground(new Color(100, 80, 60));
        homeTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
        contentPanel.add(homeTitle, BorderLayout.NORTH);

        // Cuadrícula para los productos
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 20, 20)); // 3 columnas, espacio entre ellas
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Añadir productos de ejemplo
        gridPanel.add(crearTarjetaProducto("Comic Avengers 1963", "19.99€", Color.YELLOW));
        gridPanel.add(crearTarjetaProducto("Action Comics Deluxe", "35.79€", Color.BLUE));
        gridPanel.add(crearTarjetaProducto("X-Men", "23.99€", Color.RED));
        gridPanel.add(crearTarjetaProducto("Super Man Comic", "42.89€", Color.CYAN));
        gridPanel.add(crearTarjetaProducto("Comic Avengers 1965", "15.99€", Color.ORANGE));
        gridPanel.add(crearTarjetaProducto("Spiderman", "21.39€", Color.MAGENTA));

        // Añadir Scroll
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    // Método auxiliar para crear cada cuadradito de cómic
    private JPanel crearTarjetaProducto(String titulo, String precio, Color colorPortada) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(colorMarron);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Simulador de imagen de portada
        JPanel imagePlaceholder = new JPanel();
        imagePlaceholder.setBackground(colorPortada);
        imagePlaceholder.setPreferredSize(new Dimension(120, 160));
        imagePlaceholder.setMaximumSize(new Dimension(120, 160));
        imagePlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(titulo);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel(precio);
        priceLabel.setForeground(Color.WHITE);
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(imagePlaceholder);
        card.add(Box.createVerticalStrut(10));
        card.add(titleLabel);
        card.add(priceLabel);

        return card;
    }
}