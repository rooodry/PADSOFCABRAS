package GUI;


import javax.swing.*;
import java.awt.*;

import productos.ProductoTienda;

public class PanelDeProducto extends JPanel{
    
    public PanelDeProducto(ProductoTienda p) {

        this.setLayout(new FlowLayout());

        this.add(new JLabel("NOMBRE: " + p.getNombre()));
        this.add(new JLabel("PRECIO: "+ p.getPrecio()));
    }


}
