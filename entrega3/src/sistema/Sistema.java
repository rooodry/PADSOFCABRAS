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
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Sistema {
    
    private List<Producto> productos;
    private List<Descuento> descuentos;
    private List<Usuario> usuarios;
    private List<Notificacion> notificaciones;
    private List<Pedido> pedidos;
    private Stock stock;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Sistema() {
        this.productos = new ArrayList<>();
        this.descuentos = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        this.stock = null;
    }

    public void addProducto(Producto p) {this.productos.add(p);}
    public void addDescuento(Descuento d) {this.descuentos.add(d);}
    public void addUsuario(Usuario u) {this.usuarios.add(u);}
    public void addNotificacion(Notificacion n) {this.notificaciones.add(n);}
    public void addPedido(Pedido p) {this.pedidos.add(p);}
    public void setStock(Stock s) {this.stock = s;}

    public void darAltaEmpleado(Usuario admin, String nombreEmpleado, String contraseña, TiposEmpleado tipo) throws ExcepcionUsuariosAdmin {
        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }

        Empleado e = new Empleado(nombreEmpleado, contraseña);
        e.addPermiso(tipo);
        this.usuarios.add(e);
    }

    public void darBajaEmpleado(Usuario admin, Empleado e) throws ExcepcionUsuariosAdmin {
        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        this.usuarios.remove(e);
    }

    public void modificarPermisos(Usuario admin, Empleado e, Set<TiposEmpleado> nuevosPermisos) throws ExcepcionUsuariosAdmin {
        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        ((Gestor) admin).configurarPermisos(e, nuevosPermisos);
    }

    public Pack crearPack(Usuario admin, String nombre, double precio, List<Producto> productos) throws ExcepcionUsuariosAdmin {
        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        return new Pack(nombre, precio, productos);
    }

    public Codigo generarCodigo() {
        return new Codigo();
    }

    public void actualizarStock(Usuario admin, ProductoTienda p, int cantidad) throws ExcepcionUsuariosAdmin {
        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        
        if(cantidad > 0) {
            this.stock.añadirProducto(p, cantidad);
        } else if (cantidad < 0) {
            this.stock.reducirStock(p, -cantidad); 
        } else {
            this.stock.retirarProducto(p);
        }
    }

    public double calcularPrecioFinalPedido(Pedido pedido) {
        double precioBase = pedido.calcularPrecioTotal();
        Descuento descuentoMasAntiguo = null;

        for (Descuento descuento : this.descuentos) {
            if (descuento.esAplicable(pedido)) {
                if (descuentoMasAntiguo == null || descuento.getFechaInicio().before(descuentoMasAntiguo.getFechaInicio())) {
                    descuentoMasAntiguo = descuento;
                }
            }
        }
        
        if (descuentoMasAntiguo != null) {
            pedido.setDescuento(descuentoMasAntiguo); 
            return descuentoMasAntiguo.aplicarDescuento(precioBase);
        }
        
        return precioBase;
    }

    public void registrarPedido(Pedido p) {
        double total = calcularPrecioFinalPedido(p);
    
        if (total > 200) {
            ProductoTienda productoRegalo = buscarProductoRegalo();
            if (productoRegalo != null) {
                p.setRegalo(productoRegalo); 
                System.out.println("¡REGALO AÑADIDO!: " + productoRegalo.getNombre());
            }
        }
        this.pedidos.add(p);
        
        scheduler.schedule(() -> {
            if (p.getEstadoPedido() == EstadoPedido.EN_CARRITO) {
                cancelarPedido(p);
            }
        }, 3, TimeUnit.SECONDS);    
    }

    private ProductoTienda buscarProductoRegalo() {
        return this.stock.getProductos().keySet().stream().filter(prod -> prod.getPrecio() <= 15.0).findAny().orElse(null);
    }

    public void cancelarPedido(Pedido p) {
        for(Map.Entry<ProductoTienda, Integer> entry : p.getProductos().entrySet()) {
            ProductoTienda producto = entry.getKey();
            this.stock.añadirProducto(producto, entry.getValue());
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

    public void asignarValoracion(ProductoSegundaMano p, Empleado e) {
        if (e.tienePermiso(TiposEmpleado.EMPLEADOS_INTERCAMBIO)) {
            e.addProductoParaValorar(p);
        }
    }

    public void asignarIntercambio(Intercambio i, Empleado e) {
        if (e.tienePermiso(TiposEmpleado.EMPLEADOS_INTERCAMBIO)) {
            e.addIntercambio(i);
        }
    }

    public void añadirProductoCartera(ProductoSegundaMano p, ClienteRegistrado c) {
        c.getCartera().añadirProducto(p);
    }

    public void bloquearProductoOfertante(ProductoSegundaMano p) {
        p.setDisponibilidad(false);
    }

    public Map<String, Integer> obtenerCategoriasRecomendadas(ClienteRegistrado c) {
        int cont = 0;
        double[] interesComic = {0}; 
        double[] interesJuego = {0}; 
        double interesFigura = 0;
        Map<String, Integer> categorias = new HashMap<>();
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
                            case "AVENTURA": interesComic[0] += valoracion; break;
                            case "ROMANCE": interesComic[1] += valoracion; break;
                            case "COMEDIA": interesComic[2] += valoracion; break;
                        }
                        break;
                    case "FIGURA":
                        interesFigura += valoracion;
                        break;
                    case "JUEGO":
                        switch(subcategoria) {
                            case "JUEGO_MESA": interesJuego[0] += valoracion; break;
                            case "CARTAS": interesJuego[1] += valoracion; break;
                            case "DADOS": interesJuego[2] += valoracion; break;
                        }
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
            categorias.put("JUEGO_MESA", (int) interesJuego[0]);
            categorias.put("CARTAS", (int) interesJuego[1]);
            categorias.put("DADOS", (int) interesJuego[2]);
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

    public List<ProductoTienda> recomendarProductos(Map<String, Integer> categorias, List<ProductoTienda> productos){
        Map<ProductoTienda, Integer> productosValor = new HashMap<>();
        
        for(ProductoTienda p : productos) {
            Object subcategoria = p.getCategoria().getSubcategoria();
            if (subcategoria == Genero.AVENTURA) {
                productosValor.put(p, categorias.getOrDefault("AVENTURA", 0));
            } else if (subcategoria == Genero.COMEDIA) {
                productosValor.put(p, categorias.getOrDefault("COMEDIA", 0));
            } else if (subcategoria == Genero.ROMANCE) {
                productosValor.put(p, categorias.getOrDefault("ROMANCE", 0));
            } else if (subcategoria == TipoJuego.CARTAS) {
                productosValor.put(p, categorias.getOrDefault("CARTAS", 0));
            } else if (subcategoria == TipoJuego.DADOS) {
                productosValor.put(p, categorias.getOrDefault("DADOS", 0));
            } else if (subcategoria == TipoJuego.JUEGO_MESA) {
                productosValor.put(p, categorias.getOrDefault("JUEGO_MESA", 0));
            } else if (subcategoria instanceof Figura) {
                productosValor.put(p, categorias.getOrDefault("FIGURA", 0));
            }
        }

        return productosValor.entrySet().stream()
            .sorted(Map.Entry.<ProductoTienda, Integer>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }
    
    public double[] obtenerVectores(ClienteRegistrado c) {
        int i = 0;
        Map<String, Integer> categoriasRecomendadas = obtenerCategoriasRecomendadas(c);
        double[] vectorNormalizado = new double[categoriasRecomendadas.size()];
        double normaCliente = 0;

        for(Map.Entry<String, Integer> entrada : categoriasRecomendadas.entrySet()) {
            vectorNormalizado[i] = entrada.getValue();
            i++;
        }

        double suma = 0.0;
        for (double v : vectorNormalizado) {
            suma += v * v;
        }
        normaCliente = Math.sqrt(suma);

        if (normaCliente != 0) {
            for (int j = 0; j < vectorNormalizado.length; j++) {
                vectorNormalizado[j] = vectorNormalizado[j] / normaCliente;
            }
        }

        return vectorNormalizado;
    }

    public List<ProductoTienda> productosNoComprados(ClienteRegistrado cliente, List<ProductoTienda> productos) {
        List<ProductoTienda> productosNoComprados = new ArrayList<>();
        for(Pedido p : cliente.getPedidos()) {
            for(ProductoTienda producto : productos) {
                if(!p.getProductos().containsKey(producto)) {
                    productosNoComprados.add(producto);
                } else {
                    productosNoComprados.remove(producto);
                }
            }
        }
        return productosNoComprados;
    }

    public List<ProductoTienda> recomendarProductosPorUsuarios(ClienteRegistrado cliente, List<ClienteRegistrado> clientes, List<ProductoTienda> productos) {
        Map<ClienteRegistrado, double[]> mapaClienteVector = new HashMap<>();
        Map<ClienteRegistrado, Double> mapaClienteSimilaridad = new HashMap<>();
        double[] vectorCliente = obtenerVectores(cliente);

        for(ClienteRegistrado c : clientes) {
            if(c != cliente) {
                mapaClienteVector.put(c, obtenerVectores(c));
            }
        }

        for(Map.Entry<ClienteRegistrado, double[]> entrada : mapaClienteVector.entrySet()) {
            double suma = 0;
            double[] vector = entrada.getValue();
            for(int i = 0; i < vector.length; i++) {
                suma += vectorCliente[i] * vector[i];
            }
            mapaClienteSimilaridad.put(entrada.getKey(), suma);
        }

        List<Map.Entry<ClienteRegistrado, Double>> lista = new ArrayList<>(mapaClienteSimilaridad.entrySet());
        lista.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        
        if(lista.size() < 3) return new ArrayList<>(); 

        List<ProductoTienda> productosParaRecomendar = new ArrayList<>();
        for(ProductoTienda p : productosNoComprados(cliente, productos)) {
            if(!productosNoComprados(lista.get(0).getKey(), productos).contains(p) || 
               !productosNoComprados(lista.get(1).getKey(), productos).contains(p) || 
               !productosNoComprados(lista.get(2).getKey(), productos).contains(p)) {
                productosParaRecomendar.add(p);
            }
        }

        List<ProductoTienda> lista1 = recomendarProductos(obtenerCategoriasRecomendadas(lista.get(0).getKey()), productosParaRecomendar);
        List<ProductoTienda> lista2 = recomendarProductos(obtenerCategoriasRecomendadas(lista.get(1).getKey()), productosParaRecomendar);
        List<ProductoTienda> lista3 = recomendarProductos(obtenerCategoriasRecomendadas(lista.get(2).getKey()), productosParaRecomendar);

        Map<ProductoTienda, Integer> mapaProductoValor = new HashMap<>();
        for(ProductoTienda p : productosParaRecomendar) {
            int suma = 0;
            if(lista1.contains(p)) suma += 3;
            if(lista2.contains(p)) suma += 2;
            if(lista3.contains(p)) suma += 1;
            mapaProductoValor.put(p, suma);
        }

        return mapaProductoValor.entrySet()
            .stream()
            .sorted(Map.Entry.<ProductoTienda, Integer>comparingByValue().reversed())
            .map(Map.Entry::getKey)
            .toList();
    }


    public void subirProductosFichero(String archivo) {

        String tipo, id, nombre, descripcion, autor, editorial, año, estilo, marca, material, dimension, imagen, categorias;
        Double precio;
        int unidades, paginas, numJugadores, edad;
        
        try(BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            String[] elementos;
            while ((linea = br.readLine()) != null) {
                imagen = "";
                elementos = linea.split("\\;");
                tipo = elementos[0];
                id = elementos[1];
                nombre = elementos[2];
                descripcion = elementos[3];
                precio  = Double.parseDouble(elementos[4]);
                unidades  = Integer.parseInt(elementos[5]);
                categorias = elementos[6];
                paginas  = Integer.parseInt(elementos[7]);
                autor  = elementos[8];
                editorial  = elementos[9];
                año  = elementos[10];
                numJugadores = Integer.parseInt(elementos[11]);
                edad = Integer.parseInt(elementos[12]);
                estilo = elementos[13];
                marca = elementos[14];
                material = elementos[15];
                dimension = elementos[16];

                ProductoTienda p = new ProductoTienda(nombre, descripcion, imagen);

                p.setId(id);
                p.setPrecio(precio);
                this.stock.añadirProducto(p, unidades);

                // CAMBIO porque en Java se usa .equals() para comparar cadenas, no ==
                if(tipo.equals("C")) {
                    Comic c = new Comic(nombre, paginas, autor, editorial, null); // Nota: Asumo que tu constructor de Comic acepta null para Enum, ajústalo si no es así
                    p.setCategoria(c);  
                } else if (tipo.equals("J")) {
                    Juego j = new Juego(nombre, numJugadores, edad, null);
                    p.setCategoria(j);
                } else {
                    Figura f = new Figura(nombre, 0, 0, marca);
                    p.setCategoria(f);
                }
            }
        } catch (IOException e) {
            System.err.println("Error abriendo archivo " + e.getMessage());
        }
    }
}