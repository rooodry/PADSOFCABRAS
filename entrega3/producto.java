/*LANZAR OFERTA DE INTERCAMBIO*/
Public class Producto {
    private String nombre;
    private String descripcion;
    private double precio;
    private int valoracion;

    public Producto(String nombre, String descripcion, double precio, int valoracion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.valoracion = valoracion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public int getValoracion() {
        return valoracion;
    }
    
}