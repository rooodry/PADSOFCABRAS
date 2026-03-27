package sistema;

import productos.*;
import usuarios.*;
import descuentos.*;
import estadisticas.Estadistica;
import excepciones.*;
import notificaciones.Notificacion;
import compras.*;
import utilidades.*;
import intercambios.*;
import productos.categoria.*;

import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.processing.FilerException;

public class Sistema {
    
    private List<Producto> productos;
    private List<Descuento> descuentos;
    private List<Usuario> usuarios;
    private List<Notificacion> notificaciones;
    private List<Pedido> pedidos;
    private Stock stock;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    public Sistema() {
        this.productos = new ArrayList<Producto>();
        this.descuentos = new ArrayList<Descuento>();
        this.usuarios = new ArrayList<Usuario>();
        this.notificaciones = new ArrayList<Notificacion>();
        this.pedidos = new ArrayList<Pedido>();
        this.stock = null;
    }


    //SETTERS//
    public void addProducto(Producto p) {this.productos.add(p);}
    public void addDescuento(Descuento d) {this.descuentos.add(d);}
    public void addUsuario(Usuario u) {this.usuarios.add(u);}
    public void addNotificacion(Notificacion n) {this.notificaciones.add(n);}
    public void addPedido(Pedido p) {this.pedidos.add(p);}
    public void setStock(Stock s) {this.stock = s;}


    public void darAltaEmpleado(Usuario admin, String nombreEmpleado, String contraseña, TiposEmpleado tipo) throws ExcepcionUsuariosAdmin {
        
        Empleado e = null;

        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }

        if(tipo == TiposEmpleado.EMPLEADOS_INTERCAMBIO) {
            e = new EmpleadoIntercambio(nombreEmpleado, contraseña);
        } else if (tipo == TiposEmpleado.EMPLEADOS_PEDIDO) {
            e = new EmpleadoPedido(nombreEmpleado, contraseña);
        } else {
            e = new EmpleadoProducto(nombreEmpleado, contraseña, this.stock);
        }

        this.usuarios.add(e);

    }

    public void darBajaEmpleado(Usuario admin, Empleado e) throws ExcepcionUsuariosAdmin {
        
        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        this.usuarios.remove(e);
    }

    public Empleado modificarPermiso(Usuario admin, Empleado e, TiposEmpleado tipo) throws ExcepcionUsuariosAdmin {
        
        Empleado emp;

        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }

        if(tipo == TiposEmpleado.EMPLEADOS_INTERCAMBIO) {
            emp = new EmpleadoIntercambio(e.getNombre(), e.getContraseña());
            this.usuarios.remove(e);
        } else if (tipo == TiposEmpleado.EMPLEADOS_PEDIDO) {
            emp = new EmpleadoPedido(e.getNombre(), e.getContraseña());
            this.usuarios.remove(e);
        } else {
            emp = new EmpleadoProducto(e.getNombre(), e.getContraseña(), this.stock);
            this.usuarios.remove(e);
        }

        this.usuarios.add(emp);

        return emp;
    }

    public Pack crearPack(Usuario admin, String nombre, double precio, List<Producto> productos) throws ExcepcionUsuariosAdmin {
        
        Pack pack;
        
        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }

        return pack = new Pack(nombre, precio, productos);

    }

    public Codigo generarCodigo() {

        Codigo codigo;

        return codigo = new Codigo();
    }

    public void actualizarStock(Usuario admin, ProductoTienda p, int cantidad) throws ExcepcionUsuariosAdmin {

        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        
        if(cantidad > 0) {
            this.stock.añadirProducto(p, cantidad);
        } else if (cantidad < 0) {
            this.stock.reducirStock(p, cantidad);
        } else {
            this.stock.retirarProducto(p);
        }
        
    }

    public void registrarPedido(Pedido p) {
        this.pedidos.add(p);
        
        scheduler.schedule(() -> {
            if (p.getEstadoPedido() == EstadoPedido.EN_PREPARACION) {
                cancelarPedido(p);
            }
        }, 15, TimeUnit.MINUTES);    
    }

    public void cancelarPedido(Pedido p) {

        for(Map.Entry<ProductoTienda, Integer> entry : p.getProductos().entrySet()) {
            ProductoTienda producto = entry.getKey();
            
            this.stock.retirarProducto(producto);
        }
        this.pedidos.remove(p);
        p.cancelar();
    }

    public void enviarCodigo(ClienteRegistrado c, Codigo cod) {
        c.addCodigo(cod);
    }

    public void setEstadoPedido(Pedido p, EstadoPedido e) {
        p.setEstadoPedido(e);
    }

    public void notificarUsuario(Usuario u, Notificacion n) {
        u.addNotificacion(n);
    }

    public void asignarValoracion(ProductoSegundaMano p, EmpleadoIntercambio e) {
        e.addProductoParaValorar(p);
    }

    public void asignarIntercambio(Intercambio i, EmpleadoIntercambio e) {
        e.addIntercambio(i);
    }

    public void añadirProductoCartera(ProductoSegundaMano p, ClienteRegistrado c) {
        c.getCartera().añadirProducto(p);
    }

    public void bloquearProductoOfertante(ProductoSegundaMano p) {
        p.setDisponibilidad(false);
    }

    
    public Map<String, Integer> obtenerCategoriasRecomendadas(ClienteRegistrado c) {

        int cont = 0;
        
        double[] interesComic = {0}; //0: AVENTURA, 1: ROMANCE, 2: COMEDIA
        double[] interesJuego = {0}; //0: JUEGOMESA 1: CARTAS 2:DADOS
        double interesFigura = 0;
        Map<String, Integer> categorias = new HashMap<String, Integer>();
        Map<String, Integer> categoriasOrdenadas = new LinkedHashMap<>();

        Estadistica e = new Estadistica("comprasCliente" + c.getNombre());

        try(BufferedReader br = new BufferedReader(new FileReader(e.getFichero()))) {

            String linea;
            String[] elementos;
            Integer valoracion;
            String categoria;
            String subcategoria;


            while((linea = br.readLine()) != null) {
                elementos = linea.split("\\|");
                valoracion = Integer.parseInt(elementos[2]);
                categoria = elementos[3];
                subcategoria = elementos[4];

                switch (categoria) {
                    case "COMIC":
                        switch (subcategoria) {
                            case "AVENTURA":
                                interesComic[0] += valoracion;
                                break;
                            case "ROMANCE":
                                interesComic[1] += valoracion;
                                break;
                            case "COMEDIA":
                                interesComic[2] += valoracion;
                                break;
                            default:
                                break;
                        }
                        break;
                    
                    case "FIGURA":
                        interesFigura += valoracion;
                        break;
                    
                    case "JUEGO":
                        switch(subcategoria) {
                            case "JUEGO_MESA":
                                interesJuego[0] += valoracion;
                                break;
                            case "CARTAS":
                                interesJuego[1] += valoracion;
                                break;
                            case "DADOS":
                                interesJuego[2] += valoracion;
                                break;
                            default:
                                break;
                        }
                    default:
                        break;
                    }
                cont ++;
            }

            for(int i = 0; i < 3; i++) {
                interesComic[i] = (int) (interesComic[i] / cont);
                interesJuego[i] = (int) (interesJuego[i] / cont);
            }
            interesFigura = (int) (interesFigura / cont);

            categorias.put("AVENTURA", (int) interesComic[0]);
            categorias.put("ROMANCE", (int) interesComic[1]);
            categorias.put("COMEDIA", (int) interesComic[2]);

            categorias.put("JUEGO_MESA", (int) interesComic[0]);
            categorias.put("CARTAS", (int) interesComic[1]);
            categorias.put("DADOS", (int) interesComic[2]);

            categorias.put("FIGURA", (int) interesFigura);


            List<Map.Entry<String, Integer>> listaEntradas = new ArrayList<>(categorias.entrySet());


            listaEntradas.sort((entrada1, entrada2) -> entrada2.getValue().compareTo(entrada1.getValue()));


            

            for (Map.Entry<String, Integer> entrada : listaEntradas) {
                categoriasOrdenadas.put(entrada.getKey(), entrada.getValue());
            }
            
            } catch (IOException ex) {
            System.err.println("Error abriendo archivo " + ex.getMessage());
            }
        
        return categoriasOrdenadas;
    }

    public List<ProductoTienda> recomendarProductos(Map<String, Integer> categorias, <List>ProductoTienda productos) {

        Map<ProductoTienda, Integer> productosValor = new HashMap<>();
        

        for(ProductoTienda p : productos) {

            switch (p.getCategoria().getSubcategoria()) {
                case Genero.AVENTURA:
                    productosValor.put(p, categorias.get("AVENTURA"));                    
                    break;
                case Genero.COMEDIA:
                    productosValor.put(p, categorias.get("COMEDIA"));                    
                    break;
                case Genero.ROMANCE:
                    productosValor.put(p, categorias.get("ROMANCE"));
                    break;
                case TipoJuego.CARTAS:
                    productosValor.put(p, categorias.get("CARTAS"));
                    break;
                case TipoJuego.DADOS:
                    productosValor.put(p, categorias.get("DADOS"));
                    break;
                case TipoJuego.JUEGO_MESA:
                    productosValor.put(p, categorias.get("JUEGO_MESA"));
                    break;
                case p.getCategoria().getSubcategoria() instanceof Figura:
                    productosValor.put(p, categorias.get("FIGURA"));
                    break;
                default:
                    break;
            }

        }

        List<ProductoTienda> productosOrdenados = productosValor.entrySet().stream().sorted(Map.Entry.<ProductoTienda, Integer>comparingByValue().reversed())
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

        return productosOrdenados;
        
    }

//0: AVENTURA, 1: ROMANCE, 2: COMEDIA
        double[] interesJuego = {0}; //0: JUEGOMESA 1: CARTAS 2:DADOS



}


