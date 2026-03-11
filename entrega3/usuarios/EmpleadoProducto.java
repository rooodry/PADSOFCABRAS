package usuarios;

import productos.Producto;
import productos.ProductoTienda;
import productos.Stock;

public class EmpleadoProducto extends Empleado {
    private Stock stock;

    public EmpleadoProducto(String nombreUsuario, String contraseña, Stock stock) {
        super(nombreUsuario, contraseña);
        this.stock = stock;
    }

    public void añadirProducto(ProductoTienda p) {
        stock.añadirProducto(p);
    }

    public void retirarProducto(ProductoTienda p) {
        stock.retirarProducto(p);
    }

    public void editarProducto(Producto p, String nombre, String descripcion, String imagen) {
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setImagen(imagen);
    }
}
