public class intercambio {
    private ClienteRegistrado ofrece;
    private ClienteRegistrado receptor;
    private Producto productoOfrecido;
    private Producto productoDeseado;
    private String estado; 

    public intercambio(ClienteRegistrado ofrece, ClienteRegistrado receptor, Producto productoOfrecido, Producto productoDeseado, String estado) {
        this.ofrece = ofrece;
        this.receptor = receptor;
        this.productoOfrecido = productoOfrecido;
        this.productoDeseado = productoDeseado;
        this.estado = estado;
    }

    public ClienteRegistrado getOfrece() {
        return ofreces;
    }

    public ClienteRegistrado getReceptor() {
        return receptor;
    }

    public Producto getProductoOfrecido() {
        return productoOfrecido;
    }

    public Producto getProductoDeseado() {
        return productoDeseado;
    }
}