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

/**
 * Fachada de negocio que coordina usuarios, productos, pedidos, descuentos e intercambios.
 */
public class Sistema {

    private List<Producto> productos;
    private List<Descuento> descuentos;
    private List<Usuario> usuarios;
    private List<Notificacion> notificaciones;
    private List<Pedido> pedidos;
    private Stock stock;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Constructor por defecto. Inicializa las listas del sistema y establece el stock a nulo.
     */
    public Sistema() {
        this.productos = new ArrayList<>();
        this.descuentos = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        this.stock = null;
    }

    /**
     * Añade un producto a la lista de productos del sistema.
     *
     * @param p el producto a añadir
     */
    public void addProducto(Producto p) { this.productos.add(p); }

    /**
     * Añade un descuento a la lista de descuentos disponibles en el sistema.
     *
     * @param d el descuento a añadir
     */
    public void addDescuento(Descuento d) { this.descuentos.add(d); }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param u el usuario a registrar
     */
    public void addUsuario(Usuario u) { this.usuarios.add(u); }

    /**
     * Elimina un usuario del sistema.
     *
     * @param u el usuario a eliminar
     */
    public void removeUsuario(Usuario u) { this.usuarios.remove(u); }

    /**
     * Añade una notificación al registro global de notificaciones.
     *
     * @param n la notificación a añadir
     */
    public void addNotificacion(Notificacion n) { this.notificaciones.add(n); }

    /**
     * Registra un nuevo pedido en la lista de pedidos del sistema.
     *
     * @param p el pedido a añadir
     */
    public void addPedido(Pedido p) { this.pedidos.add(p); }

    /**
     * Establece el inventario (stock) principal del sistema.
     *
     * @param s el stock a asignar
     */
    public void setStock(Stock s) { this.stock = s; }

    /**
     * Obtiene una copia de la lista de usuarios registrados en el sistema.
     *
     * @return lista de usuarios
     */
    public List<Usuario> getUsuarios() { return new ArrayList<>(this.usuarios); }

    /**
     * Obtiene una copia de la lista de pedidos realizados.
     *
     * @return lista de pedidos
     */
    public List<Pedido> getPedidos() { return new ArrayList<>(this.pedidos); }

    /**
     * Obtiene una copia de la lista de descuentos configurados en el sistema.
     *
     * @return lista de descuentos
     */
    public List<Descuento> getDescuentos() { return new ArrayList<>(this.descuentos); }

    /**
     * Reemplaza el estado actual del sistema con las listas y stock proporcionados.
     *
     * @param productos nueva lista de productos
     * @param usuarios  nueva lista de usuarios
     * @param pedidos   nueva lista de pedidos
     * @param descuentos nueva lista de descuentos
     * @param stock     nuevo stock
     */
    public void reemplazarEstado(List<Producto> productos, List<Usuario> usuarios,
            List<Pedido> pedidos, List<Descuento> descuentos, Stock stock) {
        this.productos.clear();
        this.usuarios.clear();
        this.pedidos.clear();
        this.descuentos.clear();

        if (productos != null) {
            this.productos.addAll(productos);
        }
        if (usuarios != null) {
            this.usuarios.addAll(usuarios);
        }
        if (pedidos != null) {
            this.pedidos.addAll(pedidos);
        }
        if (descuentos != null) {
            this.descuentos.addAll(descuentos);
        }
        this.stock = stock;
    }

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

    /**
     * Da de alta a un nuevo empleado en el sistema. Solo puede ser ejecutado por un Gestor.
     *
     * @param admin          usuario administrador que realiza la acción
     * @param nombreEmpleado nombre del nuevo empleado
     * @param contraseña     contraseña del nuevo empleado
     * @param tipo           tipo de permisos iniciales del empleado
     * @throws ExcepcionUsuariosAdmin si el usuario que intenta dar de alta no es un Gestor
     */
    public void darAltaEmpleado(Usuario admin, String nombreEmpleado, String contraseña, TiposEmpleado tipo)
            throws ExcepcionUsuariosAdmin {
        if (!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        Empleado e = new Empleado(nombreEmpleado, contraseña);
        e.addPermiso(tipo);
        this.usuarios.add(e);
    }

    /**
     * Da de baja a un empleado existente en el sistema. Solo puede ser ejecutado por un Gestor.
     *
     * @param admin usuario administrador que realiza la acción
     * @param e     empleado a dar de baja
     * @throws ExcepcionUsuariosAdmin si el usuario que intenta dar de baja no es un Gestor
     */
    public void darBajaEmpleado(Usuario admin, Empleado e) throws ExcepcionUsuariosAdmin {
        if (!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        this.usuarios.remove(e);
    }

    /**
     * Modifica los permisos de un empleado. Solo puede ser ejecutado por un Gestor.
     *
     * @param admin          usuario administrador que realiza la acción
     * @param e              empleado cuyos permisos se van a modificar
     * @param nuevosPermisos conjunto de nuevos permisos a asignar
     * @throws ExcepcionUsuariosAdmin si el usuario que intenta modificar no es un Gestor
     */
    public void modificarPermisos(Usuario admin, Empleado e, Set<TiposEmpleado> nuevosPermisos)
            throws ExcepcionUsuariosAdmin {
        if (!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        ((Gestor) admin).configurarPermisos(e, nuevosPermisos);
    }

    /**
     * Crea un pack de productos de tienda. Solo puede ser ejecutado por un Gestor.
     *
     * @param admin     usuario administrador que realiza la acción
     * @param nombre    nombre del pack
     * @param precio    precio total del pack
     * @param productos lista de productos que componen el pack
     * @return el pack de productos creado
     * @throws ExcepcionUsuariosAdmin si el usuario que intenta crear el pack no es un Gestor
     */
    public Pack crearPack(Usuario admin, String nombre, double precio, List<ProductoTienda> productos)
            throws ExcepcionUsuariosAdmin {
        if (!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        return new Pack(nombre, precio, productos);
    }

    /**
     * Genera un nuevo código promocional o de descuento.
     *
     * @return el código generado
     */
    public Codigo generarCodigo() {
        return new Codigo();
    }

    /**
     * Actualiza el inventario de un producto específico. Solo puede ser ejecutado por un Gestor.
     *
     * @param admin    usuario administrador que realiza la acción
     * @param p        producto cuyo stock se va a actualizar
     * @param cantidad cantidad a sumar (positivo), restar (negativo) o retirar por completo (cero)
     * @throws ExcepcionUsuariosAdmin si el usuario que intenta actualizar no es un Gestor
     */
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

    /**
     * Calcula el precio final de un pedido aplicando el descuento más antiguo válido si lo hubiere.
     *
     * @param pedido el pedido sobre el cual calcular el precio
     * @return el precio final tras aplicar descuentos
     */
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

    /**
     * Registra un pedido y aplica la lógica de regalos si supera los 200 euros.
     * Además, programa la cancelación automática si el pedido se queda en el carrito más de 3 segundos.
     *
     * @param p el pedido a registrar
     */
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

    /**
     * Busca un producto en el stock cuyo precio sea menor o igual a 15.0 para darlo como regalo.
     *
     * @return el producto de regalo encontrado, o null si no hay ninguno disponible
     */
    private ProductoTienda buscarProductoRegalo() {
        return this.stock.getProductos().keySet().stream()
                .filter(prod -> prod.getPrecio() <= 15.0)
                .findAny()
                .orElse(null);
    }

    /**
     * Cancela un pedido devolviendo sus productos al stock y actualizando su estado.
     *
     * @param p el pedido a cancelar
     */
    public void cancelarPedido(Pedido p) {
        for (Map.Entry<ProductoTienda, Integer> entry : p.getProductos().entrySet()) {
            ProductoTienda producto = entry.getKey();
            this.stock.añadirProducto(producto, entry.getValue());
        }
        this.pedidos.remove(p);
        p.cancelar();
    }

    /**
     * Envía un código promocional o de descuento a un cliente registrado.
     *
     * @param c   el cliente receptor
     * @param cod el código a enviar
     */
    public void enviarCodigo(ClienteRegistrado c, Codigo cod) {
        c.addCodigo(cod);
    }

    /**
     * Cambia el estado de un pedido.
     *
     * @param p el pedido a actualizar
     * @param e el nuevo estado del pedido
     */
    public void setEstadoPedido(Pedido p, EstadoPedido e) {
        p.setEstadoPedido(e);
    }

    /**
     * Envía una notificación a un usuario en particular.
     *
     * @param u el usuario receptor
     * @param n la notificación a enviar
     */
    public void notificarUsuario(Usuario u, Notificacion n) {
        u.addNotificacion(n);
    }

    /**
     * Asigna un producto de segunda mano a un empleado para que realice su valoración,
     * comprobando primero si tiene los permisos adecuados.
     *
     * @param p el producto de segunda mano a valorar
     * @param e el empleado encargado de la valoración
     */
    public void asignarValoracion(ProductoSegundaMano p, Empleado e) {
        if (e.tienePermiso(TiposEmpleado.EMPLEADOS_INTERCAMBIO)) {
            e.addProductoParaValorar(p);
        }
    }

    /**
     * Asigna una propuesta de intercambio a un empleado para su gestión.
     *
     * @param i el intercambio a asignar
     * @param e el empleado encargado
     */
    public void asignarIntercambio(Intercambio i, Empleado e) {
        if (e.tienePermiso(TiposEmpleado.EMPLEADOS_INTERCAMBIO)) {
            e.addIntercambio(i);
        }
    }

    /**
     * Añade un producto de segunda mano a la cartera virtual de un cliente registrado.
     *
     * @param p el producto de segunda mano
     * @param c el cliente registrado
     */
    public void añadirProductoCartera(ProductoSegundaMano p, ClienteRegistrado c) {
        c.getCartera().añadirProducto(p);
    }

    /**
     * Bloquea un producto ofertado estableciendo su disponibilidad a falso.
     *
     * @param p el producto a bloquear
     */
    public void bloquearProductoOfertante(ProductoSegundaMano p) {
        p.setDisponibilidad(false);
    }

    /**
     * Lee un archivo de estadísticas de compras de un cliente y obtiene un mapa con
     * las categorías recomendadas y su nivel de interés basado en valoraciones previas.
     *
     * @param c el cliente registrado del que se calcularán las recomendaciones
     * @return un mapa ordenado descendentemente con las categorías y su nivel de interés
     */
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

    /**
     * Recomienda una lista de productos en base a un mapa de interés por categorías.
     *
     * @param categorias mapa con las categorías y sus valores de interés
     * @param productos  lista de productos disponibles en tienda para filtrar
     * @return lista de productos ordenada por el interés calculado según las categorías
     */
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

    /**
     * Calcula y devuelve el vector normalizado de preferencias por categorías de un cliente.
     *
     * @param c el cliente registrado
     * @return un arreglo de tipo double que representa el vector normalizado de intereses
     */
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

    /**
     * Devuelve una lista con aquellos productos de tienda que el cliente aún no ha comprado.
     *
     * @param cliente   el cliente a analizar
     * @param productos la lista global de productos de tienda
     * @return lista de productos no adquiridos por el cliente
     */
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

    /**
     * Genera una recomendación de productos para un cliente mediante filtrado colaborativo,
     * buscando a los 3 clientes con intereses más similares.
     *
     * @param cliente   el cliente que recibe la recomendación
     * @param clientes  lista de todos los clientes del sistema para calcular similitud
     * @param productos lista global de productos disponibles
     * @return lista de productos recomendados ordenados por su relevancia
     */
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

    /**
     * Carga productos en el sistema a partir de un archivo de texto con un formato específico
     * separado por punto y coma (;).
     *
     * @param archivo la ruta del archivo a leer
     */
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
     * cabeceras, campos vacíos y valores con texto como {@code ">8 años"}.
     *
     * @param archivo ruta del fichero CSV separado por punto y coma
     */
    public void cargarProductosDesdeCsv(String archivo) {
        if (this.stock == null) {
            this.stock = new Stock();
        }
        this.productos.clear();
        this.stock = new Stock();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank() || linea.startsWith("TIPO(")) {
                    continue;
                }

                String[] elementos = linea.split(";", -1);
                if (elementos.length < 18) {
                    System.err.println("Linea de producto incompleta: " + linea);
                    continue;
                }

                String tipo = elementos[0].replace("\uFEFF", "").trim();
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
                String imagen = elementos[17].trim();

                ProductoTienda producto = new ProductoTienda(nombre, descripcion, "");
                producto.setId(id);
                producto.setPrecio(precio);
                producto.setValoracion(4);
                producto.setImagen(imagen);

                if (tipo.equals("C")) {
                    producto.setCategoria(new Comic(nombre, paginas, autor, editorial,
                            resolverGeneroCsv(categorias), anio));
                } else if (tipo.equals("J")) {
                    producto.setCategoria(new Juego(nombre, numJugadores, edad, resolverTipoJuegoCsv(estilo)));
                } else if (tipo.equals("F")) {
                    producto.setCategoria(new Figura(nombre, parseDoubleCsv(dimension, 10.0), marca, material));
                }

                this.productos.add(producto);
                this.stock.añadirProducto(producto, unidades);
            }
        } catch (IOException e) {
            System.err.println("Error abriendo archivo " + e.getMessage());
        }
    }

    /**
     * Analiza un valor de texto en formato CSV para intentar extraer un entero.
     *
     * @param valor   cadena con el posible valor numérico
     * @param defecto valor a retornar en caso de error
     * @return el número entero extraído o el valor por defecto
     */
    private int parseIntCsv(String valor, int defecto) {
        if (valor == null || valor.isBlank()) return defecto;
        try {
            java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\\d+").matcher(valor);
            return matcher.find() ? Integer.parseInt(matcher.group()) : defecto;
        } catch (NumberFormatException e) {
            return defecto;
        }
    }

    /**
     * Analiza un valor de texto en formato CSV para intentar extraer un decimal (double).
     *
     * @param valor   cadena con el posible valor decimal
     * @param defecto valor a retornar en caso de error
     * @return el número decimal extraído o el valor por defecto
     */
    private double parseDoubleCsv(String valor, double defecto) {
        if (valor == null || valor.isBlank()) return defecto;
        try {
            String normalizado = valor.replace(',', '.').replaceAll("[^0-9.]", "");
            return normalizado.isBlank() ? defecto : Double.parseDouble(normalizado);
        } catch (NumberFormatException e) {
            return defecto;
        }
    }

    /**
     * Traduce una cadena leída desde CSV al género de cómic correspondiente.
     *
     * @param categorias el texto extraído del CSV
     * @return el género identificado
     */
    private Genero resolverGeneroCsv(String categorias) {
        if (categorias.contains("romance")) return Genero.ROMANCE;
        if (categorias.contains("comedia") || categorias.contains("costumbrismo")
                || categorias.contains("cocina")) return Genero.COMEDIA;
        return Genero.AVENTURA;
    }

    /**
     * Traduce una cadena leída desde CSV al tipo de juego correspondiente.
     *
     * @param estilo el texto extraído del CSV
     * @return el tipo de juego identificado
     */
    private TipoJuego resolverTipoJuegoCsv(String estilo) {
        if (estilo.contains("carta")) return TipoJuego.CARTAS;
        if (estilo.contains("dado")) return TipoJuego.DADOS;
        return TipoJuego.JUEGO_MESA;
    }

    /**
     * Exporta la lista actual de productos a un archivo de texto en formato CSV.
     *
     * @param archivo la ruta del archivo de destino
     */
    public void descargarProductos(String archivo) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write("TIPO(C/J/F);ID;NOMBRE;DESCRIPCION;PRECIO;UNIDADES;CATEGORIAS;PAGINAS;"
                   + "AUTOR;EDITORIAL;AÑO;JUGADORES;EDAD;ESTILO(Cartas/Dados/JuegoDeMesa);"
                   + "MARCA;MATERIAL;DIMENSION;IMAGEN");
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
                    bw.write(c.getAño()+ ";");

                } else if (p.getCategoria() instanceof Figura) {
                    bw.write("F;");
                    Figura f = (Figura) p.getCategoria();
                    bw.write(f.getMarca() + ";");
                    bw.write(f.getMaterial() + ";");
                    bw.write(f.getAltura() + ";");

                } else {
                    bw.write("J;");
                    Juego j = (Juego) p.getCategoria();
                    bw.write(j.getNumJugadores() + ";");
                    bw.write(j.getEdadMinima() + ";");
                    bw.write(j.getTipoJuego().toString()+ ";");
                }
                bw.write(p.getImagen());
                bw.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error abrienddo archivo " + e.getMessage());
        }
    }

    /**
     * Carga usuarios en el sistema a partir de un archivo de texto separado por punto y coma (;).
     *
     * @param fichero la ruta del archivo a leer
     */
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

    /**
     * Exporta la lista actual de usuarios del sistema a un archivo de texto en formato CSV.
     *
     * @param fichero la ruta del archivo de destino
     */
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

    /**
     * Filtra una lista de productos devolviendo únicamente aquellos que pertenecen a la categoría indicada.
     *
     * @param productos la lista original de productos a filtrar
     * @param categoria la categoría objetivo ("COMIC", "FIGURA", "JUEGO")
     * @return una nueva lista con los productos filtrados
     */
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

    /**
     * Filtra una lista de productos devolviendo únicamente aquellos con una valoración mayor o igual a la indicada.
     *
     * @param productos  la lista original de productos a filtrar
     * @param valoracion la valoración mínima requerida
     * @return una nueva lista con los productos filtrados
     */
    public List<Producto> filtrarPorValoracion(List<Producto> productos, int valoracion) {
        List<Producto> productosFiltrados = new ArrayList<>();

        for (Producto p : productos) {
            if (p.getValoracion() >= valoracion) {
                productosFiltrados.add(p);
            }
        }

        return productosFiltrados;
    }

    /**
     * Filtra una lista de productos de tienda devolviendo únicamente aquellos con un precio mayor o igual al indicado.
     *
     * @param productos la lista original de productos a filtrar
     * @param precio    el precio mínimo requerido
     * @return una nueva lista con los productos filtrados
     */
    public List<ProductoTienda> filtrarPorPrecio(List<ProductoTienda> productos, double precio) {
        List<ProductoTienda> productosFiltrados = new ArrayList<>();

        for (ProductoTienda p : productos) {
            if (p.getPrecio() >= precio) {
                productosFiltrados.add(p);
            }
        }

        return productosFiltrados;
    }

    /**
     * Ordena una lista de productos alfabéticamente por su nombre.
     *
     * @param productos la lista de productos a ordenar
     * @param flag      true para orden ascendente, false para orden descendente
     * @return la misma lista de productos ordenada
     */
    public List<Producto> ordenarPorOrdenAlfabetico(List<Producto> productos, boolean flag) {
        if (flag) {
            productos.sort(Comparator.comparing(Producto::getNombre));
        } else {
            productos.sort(Comparator.comparing(Producto::getNombre).reversed());
        }
        return productos;
    }

    /**
     * Ordena una lista de productos en base a su fecha de publicación.
     *
     * @param productos la lista de productos a ordenar
     * @param flag      true para orden cronológico (más antiguos primero), false para orden inverso
     * @return la misma lista de productos ordenada
     */
    public List<Producto> ordenarPorFecha(List<Producto> productos, boolean flag) {
        if (flag) {
            productos.sort(Comparator.comparing(Producto::getFechaPublicacion));
        } else {
            productos.sort(Comparator.comparing(Producto::getFechaPublicacion).reversed());
        }
        return productos;
    }

    /**
     * Ordena una lista de productos de tienda en base a su precio.
     *
     * @param productos la lista de productos de tienda a ordenar
     * @param flag      true para orden ascendente (más baratos primero), false para descendente
     * @return la misma lista de productos ordenada
     */
    public List<ProductoTienda> ordenarPorPrecio(List<ProductoTienda> productos, boolean flag) {
        if (flag) {
            productos.sort(Comparator.comparing(ProductoTienda::getPrecio));
        } else {
            productos.sort(Comparator.comparing(ProductoTienda::getPrecio).reversed());
        }
        return productos;
    }

    /**
     * Ordena una lista de productos en base a su valoración.
     *
     * @param productos la lista de productos a ordenar
     * @param flag      true para orden ascendente (menor valoración primero), false para descendente
     * @return la misma lista de productos ordenada
     */
    public List<Producto> ordenarPorValoracion(List<Producto> productos, boolean flag) {
        if (flag) {
            productos.sort(Comparator.comparing(Producto::getValoracion));
        } else {
            productos.sort(Comparator.comparing(Producto::getValoracion).reversed());
        }
        return productos;
    }
}