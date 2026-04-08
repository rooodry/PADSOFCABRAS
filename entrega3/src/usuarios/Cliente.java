package usuarios;

import productos.Producto;

public abstract class Cliente extends Usuario{
    
    public Cliente(String nombre, String contraseña) {
        super(nombre, contraseña);
    }

    public void filtrarProducto() {

    }


}
