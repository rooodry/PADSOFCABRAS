import java.util.ArrayList;
import java.util.List;

public class ClienteRegistrado {
    private String nombreUsuario;
    private List<Producto> cartera; // Sus productos de segunda mano

    public ClienteRegistrado(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
        this.cartera = new ArrayList<>();
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void añadirACartera(Producto p) {
        cartera.add(p);
    }

    public void quitarDeCartera(Producto p) {
        cartera.remove(p);
    }
    
    public boolean tieneProducto(Producto p) {
        return cartera.contains(p);
    }
}