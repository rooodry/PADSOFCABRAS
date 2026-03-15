package usuarios;

import productos.Producto;
import productos.ProductoTienda;
import productos.Stock;
import utilidades.Status;

public class EmpleadoProducto extends Empleado {
    private final Stock stock;

    public EmpleadoProducto(String nombreUsuario, String contraseña, Stock stock) {
        super(nombreUsuario, contraseña);
        this.stock = stock;
    }

    public Status añadirProducto(ProductoTienda p, int cantidad) {
        if (cantidad <= 0) {
            System.err.println("Error: la cantidad debe ser mayor que 0.");
            return Status.ERROR;
        }
        stock.añadirProducto(p, cantidad);
        return Status.OK;
    }

    public void retirarProducto(ProductoTienda p) {
        stock.retirarProducto(p);
    }

    public void editarProducto(Producto p, String nombre, String descripcion, String imagen, double precio) {
        p.setNombre(nombre);
        p.setDescripcion(descripcion);
        p.setImagen(imagen);
        p.setPrecio(precio);
    }
}
