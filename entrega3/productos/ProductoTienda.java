package productos;
import java.util.Date;

public class ProductoTienda extends Producto {

        public ProductoTienda(String nombre, String descripcion, double precio, int valoracion, String imagen, Date fechaPublicacion) {
            super(nombre, descripcion, precio, valoracion, imagen, fechaPublicacion);
        }

}