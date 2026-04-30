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
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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

    public void addProducto(Producto p) { this.productos.add(p); }

    public void addDescuento(Descuento d) { this.descuentos.add(d); }

    public void addUsuario(Usuario u) { this.usuarios.add(u); }

    public void addNotificacion(Notificacion n) { this.notificaciones.add(n); }

    public void addPedido(Pedido p) { this.pedidos.add(p); }

    public void setStock(Stock s) { this.stock = s; }

    /**
     * Devuelve una copia de los productos registrados en el sistema.
     *
     * @return lista de productos del sistema
     */
    public List<Producto> getProductos() { return new ArrayList<>(this.productos); }

    /**
     * Devuelve el stock asociado al sistema.
     *
     * @return stock actual
     */
    public Stock getStock() { return this.stock; }

    public void darAltaEmpleado(Usuario admin, String nombreEmpleado, String contraseña, TiposEmpleado tipo)
            throws ExcepcionUsuariosAdmin {
        if (!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        Empleado e = new Empleado(nombreEmpleado, contraseña);
        e.addPermiso(tipo);
        this.usuarios.add(e);
    }

    public void darBajaEmpleado(Usuario admin, Empleado e) throws ExcepcionUsuariosAdmin {
        if (!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        this.usuarios.remove(e);
    }

    public void modificarPermisos(Usuario admin, Empleado e, Set<TiposEmpleado> nuevosPermisos)
            throws ExcepcionUsuariosAdmin {
        if (!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        ((Gestor) admin).configurarPermisos(e, nuevosPermisos);
    }

    public Pack crearPack(Usuario admin, String nombre, double precio, List<Producto> productos)
            throws ExcepcionUsuariosAdmin {
        if (!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        return new Pack(nombre, precio, productos);
    }

    public Codigo generarCodigo() {
        return new Codigo();
    }

    public void actualizarStock(Usuario admin, ProductoTienda p, int cantidad)
            throws ExcepcionUsuariosAdmin {
        if (!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        if (cantidad > 0) {
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
                if (descuentoMasAntiguo == null ||
                        descuento.getFechaInicio().before(descuentoMasAntiguo.getFechaInicio())) {
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
        return this.stock.getProductos().keySet().stream()
                .filter(prod -> prod.getPrecio() <= 15.0)
                .findAny()
                .orElse(null);
    }

    public void cancelarPedido(Pedido p) {
        for (Map.Entry<ProductoTienda, Integer> entry : p.getProductos().entrySet()) {
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

        try (BufferedReader br = new BufferedReader(new FileReader(e.getFichero()))) {
            String linea;
            String[] elementos;
            Integer valoracion;
            String categoria;
            String subcategoria;

            while ((linea = br.readLine()) != null) {
                elementos = linea.split("\\|");
                valoracion = Integer.parseInt(elementos[2]);
                categoria = elementos[3];
                subcategoria = elementos[4];

                switch (categoria) {
                    case "COMIC":
                        switch (subcategoria) {
                            case "AVENTURA": interesComic[0] += valoracion; break;
                            case "ROMANCE":  interesComic[1] += valoracion; break;
                            case "COMEDIA":  interesComic[2] += valoracion; break;
                        }
                        break;
                    case "FIGURA":
                        interesFigura += valoracion;
                        break;
                    case "JUEGO":
                        switch (subcategoria) {
                            case "JUEGO_MESA": interesJuego[0] += valoracion; break;
                            case "CARTAS":     interesJuego[1] += valoracion; break;
                            case "DADOS":      interesJuego[2] += valoracion; break;
                        }
                        break;
                }
                cont++;
            }

            for (int i = 0; i < 3; i++) {
                interesComic[i] = (int) (interesComic[i] / cont);
                interesJuego[i] = (int) (interesJuego[i] / cont);
            }
            interesFigura = (int) (interesFigura / cont);

            categorias.put("AVENTURA",   (int) interesComic[0]);
            categorias.put("ROMANCE",    (int) interesComic[1]);
            categorias.put("COMEDIA",    (int) interesComic[2]);
            categorias.put("JUEGO_MESA", (int) interesJuego[0]);
            categorias.put("CARTAS",     (int) interesJuego[1]);
            categorias.put("DADOS",      (int) interesJuego[2]);
            categorias.put("FIGURA",     (int) interesFigura);

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

    public List<ProductoTienda> recomendarProductos(Map<String, Integer> categorias, List<ProductoTienda> productos) {
        Map<ProductoTienda, Integer> productosValor = new HashMap<>();

        for (ProductoTienda p : productos) {
            Categoria cat = p.getCategoria();
            
            if (cat instanceof Comic) {
                Genero genero = ((Comic) cat).getGenero();
                if (genero == Genero.AVENTURA) {
                    productosValor.put(p, categorias.getOrDefault("AVENTURA", 0));
                } else if (genero == Genero.COMEDIA) {
                    productosValor.put(p, categorias.getOrDefault("COMEDIA", 0));
                } else if (genero == Genero.ROMANCE) {
                    productosValor.put(p, categorias.getOrDefault("ROMANCE", 0));
                }
            } else if (cat instanceof Juego) {
                TipoJuego tipo = ((Juego) cat).getTipoJuego();
                if (tipo == TipoJuego.CARTAS) {
                    productosValor.put(p, categorias.getOrDefault("CARTAS", 0));
                } else if (tipo == TipoJuego.DADOS) {
                    productosValor.put(p, categorias.getOrDefault("DADOS", 0));
                } else if (tipo == TipoJuego.JUEGO_MESA) {
                    productosValor.put(p, categorias.getOrDefault("JUEGO_MESA", 0));
                }
            } else if (cat instanceof Figura) {
                productosValor.put(p, categorias.getOrDefault("FIGURA", 0));
            } else {
                productosValor.put(p, 0);
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

        for (Map.Entry<String, Integer> entrada : categoriasRecomendadas.entrySet()) {
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

    public List<ProductoTienda> productosNoComprados(ClienteRegistrado cliente,
                                                      List<ProductoTienda> productos) {
        List<ProductoTienda> productosNoComprados = new ArrayList<>();
        for (Pedido p : cliente.getPedidos()) {
            for (ProductoTienda producto : productos) {
                if (!p.getProductos().containsKey(producto)) {
                    productosNoComprados.add(producto);
                } else {
                    productosNoComprados.remove(producto);
                }
            }
        }
        return productosNoComprados;
    }

    public List<ProductoTienda> recomendarProductosPorUsuarios(ClienteRegistrado cliente,
                                                                List<ClienteRegistrado> clientes,
                                                                List<ProductoTienda> productos) {
        Map<ClienteRegistrado, double[]> mapaClienteVector = new HashMap<>();
        Map<ClienteRegistrado, Double> mapaClienteSimilaridad = new HashMap<>();
        double[] vectorCliente = obtenerVectores(cliente);

        for (ClienteRegistrado c : clientes) {
            if (c != cliente) {
                mapaClienteVector.put(c, obtenerVectores(c));
            }
        }

        for (Map.Entry<ClienteRegistrado, double[]> entrada : mapaClienteVector.entrySet()) {
            double suma = 0;
            double[] vector = entrada.getValue();
            for (int i = 0; i < vector.length; i++) {
                suma += vectorCliente[i] * vector[i];
            }
            mapaClienteSimilaridad.put(entrada.getKey(), suma);
        }

        List<Map.Entry<ClienteRegistrado, Double>> lista = new ArrayList<>(mapaClienteSimilaridad.entrySet());
        lista.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        if (lista.size() < 3) return new ArrayList<>();

        List<ProductoTienda> productosParaRecomendar = new ArrayList<>();
        for (ProductoTienda p : productosNoComprados(cliente, productos)) {
            if (!productosNoComprados(lista.get(0).getKey(), productos).contains(p) ||
                !productosNoComprados(lista.get(1).getKey(), productos).contains(p) ||
                !productosNoComprados(lista.get(2).getKey(), productos).contains(p)) {
                productosParaRecomendar.add(p);
            }
        }

        List<ProductoTienda> lista1 = recomendarProductos(obtenerCategoriasRecomendadas(lista.get(0).getKey()), productosParaRecomendar);
        List<ProductoTienda> lista2 = recomendarProductos(obtenerCategoriasRecomendadas(lista.get(1).getKey()), productosParaRecomendar);
        List<ProductoTienda> lista3 = recomendarProductos(obtenerCategoriasRecomendadas(lista.get(2).getKey()), productosParaRecomendar);

        Map<ProductoTienda, Integer> mapaProductoValor = new HashMap<>();
        for (ProductoTienda p : productosParaRecomendar) {
            int suma = 0;
            if (lista1.contains(p)) suma += 3;
            if (lista2.contains(p)) suma += 2;
            if (lista3.contains(p)) suma += 1;
            mapaProductoValor.put(p, suma);
        }

        return mapaProductoValor.entrySet()
                .stream()
                .sorted(Map.Entry.<ProductoTienda, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }

    public void cargaProductos(String archivo) {

        String tipo, id, nombre, descripcion, autor, editorial, año,
               estilo, marca, material, dimension, imagen, categorias;
        Double precio;
        int unidades, paginas, numJugadores, edad;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            String[] elementos;
            while ((linea = br.readLine()) != null) {
                imagen = "";
                elementos = linea.split("\\;");
                tipo        = elementos[0];
                id          = elementos[1];
                nombre      = elementos[2];
                descripcion = elementos[3];
                precio      = Double.parseDouble(elementos[4]);
                unidades    = Integer.parseInt(elementos[5]);
                categorias  = elementos[6];
                paginas     = Integer.parseInt(elementos[7]);
                autor       = elementos[8];
                editorial   = elementos[9];
                año         = elementos[10];
                numJugadores = Integer.parseInt(elementos[11]);
                edad        = Integer.parseInt(elementos[12]);
                estilo      = elementos[13];
                marca       = elementos[14];
                material    = elementos[15];
                dimension   = elementos[16];

                ProductoTienda p = new ProductoTienda(nombre, descripcion, imagen);
                p.setId(id);
                p.setPrecio(precio);
                this.stock.añadirProducto(p, unidades);

                if (tipo.equals("C")) {
                    if (categorias.equals("Aventura")) {
                        Comic c = new Comic(nombre, paginas, autor, editorial, Genero.AVENTURA, Integer.parseInt(año));
                        p.setCategoria(c);
                    } else if (categorias.equals("Romance")) {
                        Comic c = new Comic(nombre, paginas, autor, editorial, Genero.ROMANCE, Integer.parseInt(año));
                        p.setCategoria(c);
                    } else {
                        Comic c = new Comic(nombre, paginas, autor, editorial, Genero.COMEDIA, Integer.parseInt(año));
                        p.setCategoria(c);
                    }

                } else if (tipo.equals("J")) {
                    if (estilo.equals("Cartas")) {
                        Juego j = new Juego(nombre, numJugadores, edad, TipoJuego.CARTAS);
                        p.setCategoria(j);
                    } else if (estilo.equals("Dados")) {
                        Juego j = new Juego(nombre, numJugadores, edad, TipoJuego.DADOS);
                        p.setCategoria(j);
                    } else {
                        Juego j = new Juego(nombre, numJugadores, edad, TipoJuego.JUEGO_MESA);
                        p.setCategoria(j);
                    }

                } else {
                    Figura f = new Figura(nombre, Double.parseDouble(dimension), marca, material);
                    p.setCategoria(f);
                }
            }
        } catch (IOException e) {
            System.err.println("Error abriendo archivo " + e.getMessage());
        }
    }

    /**
     * Carga productos de tienda desde el CSV del proyecto de forma tolerante a
     * cabeceras, campos vacios y valores con texto como {@code ">8 anos"}.
     *
     * @param archivo ruta del fichero CSV separado por punto y coma
     */
    public void cargarProductosDesdeCsv(String archivo) {
        if (this.stock == null) {
            this.stock = new Stock();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank() || linea.startsWith("TIPO(")) {
                    continue;
                }

                String[] elementos = linea.split(";", -1);
                if (elementos.length < 17) {
                    System.err.println("Linea de producto incompleta: " + linea);
                    continue;
                }

                String tipo = elementos[0].trim();
                String id = elementos[1].trim();
                String nombre = elementos[2].trim();
                String descripcion = elementos[3].trim();
                double precio = parseDoubleCsv(elementos[4], 0.0);
                int unidades = parseIntCsv(elementos[5], 0);
                String categorias = elementos[6].toLowerCase();
                int paginas = parseIntCsv(elementos[7], 0);
                String autor = elementos[8].trim();
                String editorial = elementos[9].trim();
                int anio = parseIntCsv(elementos[10], 0);
                int numJugadores = parseIntCsv(elementos[11], 2);
                int edad = parseIntCsv(elementos[12], 3);
                String estilo = elementos[13].toLowerCase();
                String marca = elementos[14].trim();
                String material = elementos[15].trim();
                String dimension = elementos[16].trim();

                ProductoTienda producto = new ProductoTienda(nombre, descripcion, "");
                producto.setId(id);
                producto.setPrecio(precio);
                producto.setValoracion(4);

                if (tipo.equals("C")) {
                    producto.setCategoria(new Comic(nombre, paginas, autor, editorial,
                            resolverGeneroCsv(categorias), anio));
                } else if (tipo.equals("J")) {
                    producto.setCategoria(new Juego(nombre, numJugadores, edad, resolverTipoJuegoCsv(estilo)));
                } else if (tipo.equals("F")) {
                    producto.setCategoria(new Figura(nombre, parseDoubleCsv(dimension, 10.0), marca, material));
                }

                this.productos.add(producto);
                this.stock.a\u00f1adirProducto(producto, unidades);
            }
        } catch (IOException e) {
            System.err.println("Error abriendo archivo " + e.getMessage());
        }
    }

    private int parseIntCsv(String valor, int defecto) {
        if (valor == null || valor.isBlank()) return defecto;
        try {
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\d+").matcher(valor);
            return matcher.find() ? Integer.parseInt(matcher.group()) : defecto;
        } catch (NumberFormatException e) {
            return defecto;
        }
    }

    private double parseDoubleCsv(String valor, double defecto) {
        if (valor == null || valor.isBlank()) return defecto;
        try {
            String normalizado = valor.replace(',', '.').replaceAll("[^0-9.]", "");
            return normalizado.isBlank() ? defecto : Double.parseDouble(normalizado);
        } catch (NumberFormatException e) {
            return defecto;
        }
    }

    private Genero resolverGeneroCsv(String categorias) {
        if (categorias.contains("romance")) return Genero.ROMANCE;
        if (categorias.contains("comedia") || categorias.contains("costumbrismo")
                || categorias.contains("cocina")) return Genero.COMEDIA;
        return Genero.AVENTURA;
    }

    private TipoJuego resolverTipoJuegoCsv(String estilo) {
        if (estilo.contains("carta")) return TipoJuego.CARTAS;
        if (estilo.contains("dado")) return TipoJuego.DADOS;
        return TipoJuego.JUEGO_MESA;
    }

    public void descargarProductos(String archivo) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write("TIPO(C/J/F);ID;NOMBRE;DESCRIPCION;PRECIO;UNIDADES;CATEGORIAS;PAGINAS;"
                   + "AUTOR;EDITORIAL;AÑO;JUGADORES;EDAD;ESTILO(Cartas/Dados/JuegoDeMesa);"
                   + "MARCA;MATERIAL;DIMENSION");
            bw.newLine();

            for (Producto p : this.productos) {
                if (p.getCategoria() instanceof Comic) {
                    bw.write("C;");
                } else if (p.getCategoria() instanceof Figura) {
                    bw.write("F;");
                } else {
                    bw.write("J;");
                }

                bw.write(p.getNombre() + ";");
                bw.write(p.getDescripcion() + ";");
                if (p instanceof ProductoTienda) {
                    ProductoTienda pt = (ProductoTienda) p;
                    bw.write(String.valueOf(pt.getPrecio()) + ";");
                    bw.write(this.stock.getNumProductos(pt) + ";");
                }

                if (p.getCategoria() instanceof Comic) {
                    bw.write(p.getCategoria().toString() + ";");
                    Comic c = (Comic) p.getCategoria();
                    bw.write(c.getNumPaginas() + ";");
                    bw.write(c.getAutor() + ";");
                    bw.write(c.getEditorial() + ";");
                    bw.write(c.getAño());
                    bw.newLine();

                } else if (p.getCategoria() instanceof Figura) {
                    bw.write("F;");
                    Figura f = (Figura) p.getCategoria();
                    bw.write(f.getMarca() + ";");
                    bw.write(f.getMaterial() + ";");
                    bw.write(f.getAltura() + ";");
                    bw.newLine();

                } else {
                    bw.write("J;");
                    Juego j = (Juego) p.getCategoria();
                    bw.write(j.getNumJugadores() + ";");
                    bw.write(j.getEdadMinima() + ";");
                    bw.write(j.getTipoJuego().toString());
                    bw.newLine();
                }
            }

        } catch (IOException e) {
            System.err.println("Error abrienddo archivo " + e.getMessage());
        }
    }

    public void cargaUsuarios(String fichero) {
        try(BufferedReader br = new BufferedReader(new FileReader(fichero))) {
            String linea;
            String[] elementos;
            while ((linea = br.readLine()) != null) {
                elementos = linea.split("\\;");
                
                if(elementos[0].equals("C")) {
                    ClienteRegistrado c = new ClienteRegistrado(elementos[1], elementos[2], elementos[3]);
                    this.usuarios.add(c);
                } else if (elementos[0].equals("E")) {
                    Empleado e = new Empleado(elementos[1], elementos[2]);
                    String[] permisos = elementos[4].split("\\-");
                    for(int i = 0; i < permisos.length; i++) {
                        if(permisos[i].equals("Producto")) {
                            e.addPermiso(TiposEmpleado.EMPLEADOS_PRODUCTO);
                        } else if(permisos[i].equals("Intercambio")) {
                            e.addPermiso(TiposEmpleado.EMPLEADOS_INTERCAMBIO);
                        } else {
                            e.addPermiso(TiposEmpleado.EMPLEADOS_PEDIDO);
                        }
                    }
                    this.usuarios.add(e);
                } else {
                    Gestor g = new Gestor(elementos[1], elementos[2]);
                    this.usuarios.add(g);
                }

            }

        } catch(IOException e) {
            System.err.println("Error abriendo archivo "+ e.getMessage());
        }
    }

    public void descargaUsuarios(String fichero) {

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(fichero))) {

            bw.write("TIPO(C/G/E);NOMBRE;CONTRASEÑA;DNI;PERMISOS(PERMISO1-PERMISO2-...)");
            bw.newLine();

            for(Usuario u : this.usuarios) {

                if(u instanceof ClienteRegistrado) {
                    ClienteRegistrado c = (ClienteRegistrado) u;
                    bw.write("C;");
                    bw.write(c.getNombre() + ";");
                    bw.write(c.getContraseña()+ ";");
                    bw.write(c.getDNI());
                    bw.newLine();
                } else if(u instanceof Empleado) {
                    Empleado e = (Empleado) u;
                    bw.write("E;");
                    bw.write(e.getNombre() + ";");
                    bw.write(e.getContraseña() + ";");
                    bw.write(e.getPermisos().toString());
                    bw.newLine();
                } else {
                    Gestor g = (Gestor) u;
                    bw.write("G;");
                    bw.write(g.getNombre() + ";");
                    bw.write(g.getContraseña());
                    bw.newLine();
                }

            }

        } catch (IOException e) {
            System.err.println("Error abriendo archivo " + e.getMessage());
        }

    }

    public List<Producto> filtrarPorCategoria(List<Producto> productos, String categoria) {
        List<Producto> productosFiltrados = new ArrayList<>();

        if (categoria.equals("COMIC")) {
            for (Producto p : productos) {
                if (p.getCategoria() instanceof Comic) {
                    productosFiltrados.add(p);
                }
            }
        } else if (categoria.equals("FIGURA")) {
            for (Producto p : productos) {
                if (p.getCategoria() instanceof Figura) {
                    productosFiltrados.add(p);
                }
            }
        } else if (categoria.equals("JUEGO")) {
            for (Producto p : productos) {
                if (p.getCategoria() instanceof Juego) {
                    productosFiltrados.add(p);
                }
            }
        }

        return productosFiltrados;
    }

    public List<Producto> filtrarPorValoracion(List<Producto> productos, int valoracion) {
        List<Producto> productosFiltrados = new ArrayList<>();

        for (Producto p : productos) {
            if (p.getValoracion() >= valoracion) {
                productosFiltrados.add(p);
            }
        }

        return productosFiltrados;
    }

    public List<ProductoTienda> filtrarPorPrecio(List<ProductoTienda> productos, double precio) {
        List<ProductoTienda> productosFiltrados = new ArrayList<>();

        for (ProductoTienda p : productos) {
            if (p.getPrecio() >= precio) {
                productosFiltrados.add(p);
            }
        }

        return productosFiltrados;
    }

    public List<Producto> ordenarPorOrdenAlfabetico(List<Producto> productos, boolean flag) {
        if (flag) {
            productos.sort(Comparator.comparing(Producto::getNombre));
        } else {
            productos.sort(Comparator.comparing(Producto::getNombre).reversed());
        }
        return productos;
    }

    public List<Producto> ordenarPorFecha(List<Producto> productos, boolean flag) {
        if (flag) {
            productos.sort(Comparator.comparing(Producto::getFechaPublicacion));
        } else {
            productos.sort(Comparator.comparing(Producto::getFechaPublicacion).reversed());
        }
        return productos;
    }

    public List<ProductoTienda> ordenarPorPrecio(List<ProductoTienda> productos, boolean flag) {
        if (flag) {
            productos.sort(Comparator.comparing(ProductoTienda::getPrecio));
        } else {
            productos.sort(Comparator.comparing(ProductoTienda::getPrecio).reversed());
        }
        return productos;
    }

    public List<Producto> ordenarPorValoracion(List<Producto> productos, boolean flag) {
        if (flag) {
            productos.sort(Comparator.comparing(Producto::getValoracion));
        } else {
            productos.sort(Comparator.comparing(Producto::getValoracion).reversed());
        }
        return productos;
    }
}
