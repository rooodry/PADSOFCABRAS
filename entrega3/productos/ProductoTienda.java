package productos;

public class ProductoTienda extends Producto {

        private double precio;

        public ProductoTienda(String nombre, String descripcion, String imagen) {
            super(nombre, descripcion, imagen);
        }

        //SETTER//
        public void setPrecio(double precio) {this.precio = precio;}

        //GETTER//
        public double getPrecio() {return this.precio;}


}