

public class productoSegundaMano {
    private boolean estaDisponible;
    private ClienteRegistrado propietario;
    private int valoracion;
    private double valorEstimado;
    private boolean estaValorado;

    public productoSegundaMano(ClienteRegistrado propietario, double valorEstimado) {
        this.estaDisponible = true;
        this.propietario = propietario;
        this.valoracion = 0;
        this.valorEstimado = valorEstimado;
        this.estaValorado = false;
    }
}
