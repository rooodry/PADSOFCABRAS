package usuarios;

import productos.Producto;

public abstract class Cliente extends Usuario{
    
    public Cliente(String nombre, String contraseña) {
        super(nombre, contraseña);
    }

    public void filtrarProducto() {
        
    }

    public void ordenarProducto() {}

    public void login() {}

    public void verProducto(Producto producto) {}

}
