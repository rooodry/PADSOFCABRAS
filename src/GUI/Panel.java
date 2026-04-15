package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


import productos.*;


public class Panel extends JPanel {

    private String nombre;
    private List<ProductoTienda> productos;

    Panel(String nombre, List<ProductoTienda> productos) {

        this.nombre = nombre;
        this.productos = new ArrayList<ProductoTienda>(productos);
        
        
        this.setLayout(new FlowLayout());

        JLabel etiqueta = new JLabel(this.nombre);
        JTextArea areaTexto = new JTextArea(5, 30);
        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setPreferredSize(new Dimension(400, 100));


        

    }
    
}
