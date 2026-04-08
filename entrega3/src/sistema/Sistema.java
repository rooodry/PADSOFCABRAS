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
 * Clase principal que actúa como controlador central del sistema de tienda.
 *
 * <p>Gestiona todas las entidades del negocio: productos, usuarios, pedidos,
 * descuentos, stock y notificaciones. Proporciona operaciones para la
 * administración de empleados, procesamiento de pedidos, sistema de
 * recomendaciones basado en historial de compras y filtrado/ordenación
 * del catálogo de productos.</p>
 *
 * <p>El sistema utiliza un {@link ScheduledExecutorService} para cancelar
 * automáticamente los pedidos que permanecen en estado {@code EN_CARRITO}
 * pasados 3 segundos desde su registro.</p>
 */
public class Sistema {

    /** Lista de todos los productos del sistema. */
    private List<Producto> productos;

    /** Lista de descuentos disponibles para aplicar a pedidos. */
    private List<Descuento> descuentos;

    /** Lista de usuarios registrados en el sistema. */
    private List<Usuario> usuarios;

    /** Lista de notificaciones generadas en el sistema. */
    private List<Notificacion> notificaciones;

    /** Lista de pedidos realizados en el sistema. */
    private List<Pedido> pedidos;

    /** Almacén de stock que relaciona productos con sus unidades disponibles. */
    private Stock stock;

    /**
     * Planificador de tareas usado para cancelar pedidos abandonados
     * automáticamente tras un tiempo de espera.
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Construye una nueva instancia del sistema con todas las colecciones
     * vacías y el stock sin inicializar ({@code null}).
     */
    public Sistema() {
        this.productos = new ArrayList<>();
        this.descuentos = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
        this.pedidos = new ArrayList<>();
        this.stock = null;
    }

    // -------------------------------------------------------------------------
    // Métodos de adición básica
    // -------------------------------------------------------------------------

    /**
     * Añade un producto al catálogo general del sistema.
     *
     * @param p el producto a añadir; no debe ser {@code null}
     */
    public void addProducto(Producto p) { this.productos.add(p); }

    /**
     * Añade un descuento a la lista de descuentos aplicables del sistema.
     *
     * @param d el descuento a añadir; no debe ser {@code null}
     */
    public void addDescuento(Descuento d) { this.descuentos.add(d); }

    /**
     * Añade un usuario (cliente, empleado o gestor) al sistema.
     *
     * @param u el usuario a añadir; no debe ser {@code null}
     */
    public void addUsuario(Usuario u) { this.usuarios.add(u); }

    /**
     * Registra una notificación en la lista global del sistema.
     *
     * @param n la notificación a añadir; no debe ser {@code null}
     */
    public void addNotificacion(Notificacion n) { this.notificaciones.add(n); }

    /**
     * Añade un pedido directamente a la lista interna de pedidos.
     *
     * <p><b>Nota:</b> para el registro completo de un pedido (con cálculo de
     * precio, regalo y cancelación automática) utilice
     * {@link #registrarPedido(Pedido)}.</p>
     *
     * @param p el pedido a añadir; no debe ser {@code null}
     */
    public void addPedido(Pedido p) { this.pedidos.add(p); }

    /**
     * Establece el stock del sistema.
     *
     * @param s el objeto {@link Stock} que gestionará las unidades disponibles
     */
    public void setStock(Stock s) { this.stock = s; }

    // -------------------------------------------------------------------------
    // Gestión de empleados
    // -------------------------------------------------------------------------

    /**
     * Da de alta a un nuevo empleado en el sistema.
     *
     * <p>Solo un {@link Gestor} puede realizar esta operación. Se crea un
     * nuevo {@link Empleado} con el nombre y contraseña indicados y se le
     * asigna el tipo de permiso especificado.</p>
     *
     * @param admin           usuario que ejecuta la acción; debe ser instancia de {@link Gestor}
     * @param nombreEmpleado  nombre de usuario del nuevo empleado
     * @param contraseña      contraseña del nuevo empleado
     * @param tipo            tipo de permiso inicial asignado al empleado
     * @throws ExcepcionUsuariosAdmin si {@code admin} no es un {@link Gestor}
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
     * Da de baja a un empleado eliminándolo del sistema.
     *
     * <p>Solo un {@link Gestor} puede realizar esta operación.</p>
     *
     * @param admin usuario que ejecuta la acción; debe ser instancia de {@link Gestor}
     * @param e     empleado a eliminar
     * @throws ExcepcionUsuariosAdmin si {@code admin} no es un {@link Gestor}
     */
    public void darBajaEmpleado(Usuario admin, Empleado e) throws ExcepcionUsuariosAdmin {
        if (!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        this.usuarios.remove(e);
    }

    /**
     * Modifica el conjunto de permisos de un empleado.
     *
     * <p>Solo un {@link Gestor} puede realizar esta operación. La
     * actualización se delega en el método
     * {@link Gestor#configurarPermisos(Empleado, Set)}.</p>
     *
     * @param admin          usuario que ejecuta la acción; debe ser instancia de {@link Gestor}
     * @param e              empleado cuyos permisos se van a modificar
     * @param nuevosPermisos conjunto de nuevos tipos de permiso a asignar
     * @throws ExcepcionUsuariosAdmin si {@code admin} no es un {@link Gestor}
     */
    public void modificarPermisos(Usuario admin, Empleado e, Set<TiposEmpleado> nuevosPermisos)
            throws ExcepcionUsuariosAdmin {
        if (!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        ((Gestor) admin).configurarPermisos(e, nuevosPermisos);
    }

    // -------------------------------------------------------------------------
    // Gestión de productos y stock
    // -------------------------------------------------------------------------

    /**
     * Crea un pack de productos.
     *
     * <p>Solo un {@link Gestor} puede crear packs.</p>
     *
     * @param admin     usuario que ejecuta la acción; debe ser instancia de {@link Gestor}
     * @param nombre    nombre del pack
     * @param precio    precio total del pack
     * @param productos lista de productos que componen el pack
     * @return el nuevo {@link Pack} creado
     * @throws ExcepcionUsuariosAdmin si {@code admin} no es un {@link Gestor}
     */
    public Pack crearPack(Usuario admin, String nombre, double precio, List<Producto> productos)
            throws ExcepcionUsuariosAdmin {
        if (!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }
        return new Pack(nombre, precio, productos);
    }

    /**
     * Genera y devuelve un nuevo código de descuento o promocional.
     *
     * @return una nueva instancia de {@link Codigo}
     */
    public Codigo generarCodigo() {
        return new Codigo();
    }

    /**
     * Actualiza las unidades de un producto en el stock.
     *
     * <ul>
     *   <li>Si {@code cantidad > 0}: se añaden unidades al stock.</li>
     *   <li>Si {@code cantidad < 0}: se reducen unidades del stock.</li>
     *   <li>Si {@code cantidad == 0}: el producto se retira completamente del stock.</li>
     * </ul>
     *
     * <p>Solo un {@link Gestor} puede realizar esta operación.</p>
     *
     * @param admin    usuario que ejecuta la acción; debe ser instancia de {@link Gestor}
     * @param p        producto de tienda cuyo stock se va a modificar
     * @param cantidad número de unidades a añadir (positivo), reducir (negativo)
     *                 o {@code 0} para retirar el producto del stock
     * @throws ExcepcionUsuariosAdmin si {@code admin} no es un {@link Gestor}
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

    // -------------------------------------------------------------------------
    // Procesamiento de pedidos
    // -------------------------------------------------------------------------

    /**
     * Calcula el precio final de un pedido aplicando el descuento más antiguo
     * que sea aplicable.
     *
     * <p>Si hay varios descuentos aplicables se selecciona el que tiene la
     * {@code fechaInicio} más antigua. Si no hay ningún descuento aplicable,
     * se devuelve el precio base sin modificar.</p>
     *
     * @param pedido el pedido sobre el que calcular el precio
     * @return el precio final del pedido tras aplicar el descuento (si lo hay)
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
     * Registra un pedido en el sistema con toda la lógica asociada.
     *
     * <p>Pasos que realiza:</p>
     * <ol>
     *   <li>Calcula el precio final mediante {@link #calcularPrecioFinalPedido(Pedido)}.</li>
     *   <li>Si el total supera los 200 €, busca un producto regalo (precio ≤ 15 €)
     *       y lo añade al pedido.</li>
     *   <li>Añade el pedido a la lista interna.</li>
     *   <li>Programa una tarea que cancelará el pedido automáticamente si sigue
     *       en estado {@link EstadoPedido#EN_CARRITO} pasados 3 segundos.</li>
     * </ol>
     *
     * @param p el pedido a registrar; no debe ser {@code null}
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
     * Busca un producto del stock cuyo precio sea igual o inferior a 15 €
     * para usarlo como regalo en pedidos que superen los 200 €.
     *
     * @return un {@link ProductoTienda} con precio ≤ 15 €, o {@code null}
     *         si no existe ninguno disponible en el stock
     */
    private ProductoTienda buscarProductoRegalo() {
        return this.stock.getProductos().keySet().stream()
                .filter(prod -> prod.getPrecio() <= 15.0)
                .findAny()
                .orElse(null);
    }

    /**
     * Cancela un pedido devolviendo todas sus unidades al stock.
     *
     * <p>Por cada producto del pedido se restauran las unidades en el stock,
     * el pedido se elimina de la lista interna y se marca como cancelado
     * mediante {@link Pedido#cancelar()}.</p>
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
     * Envía un código promocional a un cliente registrado.
     *
     * @param c   cliente registrado que recibirá el código
     * @param cod código a enviar
     */
    public void enviarCodigo(ClienteRegistrado c, Codigo cod) {
        c.addCodigo(cod);
    }

    /**
     * Actualiza el estado de un pedido.
     *
     * @param p pedido cuyo estado se va a modificar
     * @param e nuevo estado del pedido
     */
    public void setEstadoPedido(Pedido p, EstadoPedido e) {
        p.setEstadoPedido(e);
    }

    /**
     * Envía una notificación a un usuario.
     *
     * @param u usuario que recibirá la notificación
     * @param n notificación a enviar
     */
    public void notificarUsuario(Usuario u, Notificacion n) {
        u.addNotificacion(n);
    }

    // -------------------------------------------------------------------------
    // Gestión de intercambios
    // -------------------------------------------------------------------------

    /**
     * Asigna un producto de segunda mano a un empleado para que lo valore,
     * siempre que el empleado tenga el permiso {@link TiposEmpleado#EMPLEADOS_INTERCAMBIO}.
     *
     * @param p producto de segunda mano pendiente de valoración
     * @param e empleado al que se asigna la valoración
     */
    public void asignarValoracion(ProductoSegundaMano p, Empleado e) {
        if (e.tienePermiso(TiposEmpleado.EMPLEADOS_INTERCAMBIO)) {
            e.addProductoParaValorar(p);
        }
    }

    /**
     * Asigna un intercambio a un empleado para que lo gestione,
     * siempre que el empleado tenga el permiso {@link TiposEmpleado#EMPLEADOS_INTERCAMBIO}.
     *
     * @param i intercambio a asignar
     * @param e empleado responsable de gestionar el intercambio
     */
    public void asignarIntercambio(Intercambio i, Empleado e) {
        if (e.tienePermiso(TiposEmpleado.EMPLEADOS_INTERCAMBIO)) {
            e.addIntercambio(i);
        }
    }

    /**
     * Añade un producto de segunda mano a la cartera de un cliente registrado.
     *
     * @param p producto de segunda mano a añadir
     * @param c cliente registrado propietario de la cartera
     */
    public void añadirProductoCartera(ProductoSegundaMano p, ClienteRegistrado c) {
        c.getCartera().añadirProducto(p);
    }

    /**
     * Bloquea un producto de segunda mano marcándolo como no disponible.
     *
     * <p>Se utiliza para reservar el producto durante un proceso de intercambio
     * iniciado por el ofertante.</p>
     *
     * @param p producto de segunda mano a bloquear
     */
    public void bloquearProductoOfertante(ProductoSegundaMano p) {
        p.setDisponibilidad(false);
    }

    // -------------------------------------------------------------------------
    // Sistema de recomendaciones
    // -------------------------------------------------------------------------

    /**
     * Obtiene un mapa de categorías y subcategorías ordenado por el nivel de
     * interés de un cliente, calculado a partir de su historial de compras.
     *
     * <p>Lee el fichero de estadísticas del cliente (formato CSV con separador
     * {@code |}), acumula las valoraciones por subcategoría (cómic, juego y
     * figura) y calcula la media ponderada. El resultado se devuelve ordenado
     * de mayor a menor interés.</p>
     *
     * <p>Subcategorías gestionadas:</p>
     * <ul>
     *   <li>Cómics: {@code AVENTURA}, {@code ROMANCE}, {@code COMEDIA}</li>
     *   <li>Juegos: {@code JUEGO_MESA}, {@code CARTAS}, {@code DADOS}</li>
     *   <li>Figuras: {@code FIGURA}</li>
     * </ul>
     *
     * @param c cliente registrado cuyo historial se va a analizar
     * @return mapa {@code <subcategoría, interésMedio>} ordenado descendentemente;
     *         vacío si ocurre un error de I/O
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
     * Recomienda una lista de productos de tienda ordenada según el perfil
     * de intereses de un cliente.
     *
     * <p>Para cada producto se obtiene la subcategoría y se le asigna el valor
     * de interés correspondiente del mapa {@code categorias}. Los productos se
     * devuelven ordenados de mayor a menor valor de interés.</p>
     *
     * @param categorias mapa {@code <subcategoría, interés>} obtenido de
     *                   {@link #obtenerCategoriasRecomendadas(ClienteRegistrado)}
     * @param productos  lista de productos candidatos a recomendar
     * @return lista de productos ordenada descendentemente por nivel de interés
     */
    public List<ProductoTienda> recomendarProductos(Map<String, Integer> categorias,
                                                     List<ProductoTienda> productos) {
        Map<ProductoTienda, Integer> productosValor = new HashMap<>();

        for (ProductoTienda p : productos) {
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

    /**
     * Obtiene el vector de intereses normalizado (norma euclídea) de un cliente.
     *
     * <p>El vector se construye a partir del mapa devuelto por
     * {@link #obtenerCategoriasRecomendadas(ClienteRegistrado)} y se normaliza
     * dividiéndolo por su módulo para permitir calcular la similitud del coseno
     * entre clientes.</p>
     *
     * @param c cliente registrado del que obtener el vector
     * @return array de {@code double} con el vector de intereses normalizado;
     *         todos los valores serán 0 si la norma del vector original es 0
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
     * Devuelve los productos de la lista que el cliente aún no ha comprado.
     *
     * <p>Itera sobre todos los pedidos del cliente y sobre todos los productos.
     * Un producto se añade si no está en algún pedido y se elimina si se
     * encuentra en alguno.</p>
     *
     * @param cliente  cliente registrado cuyo historial de compras se consulta
     * @param productos lista de productos a evaluar
     * @return lista de productos que el cliente no ha adquirido todavía
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
     * Recomienda productos basándose en la similitud de gustos con otros clientes
     * (filtrado colaborativo).
     *
     * <p>Algoritmo:</p>
     * <ol>
     *   <li>Calcula el vector normalizado de intereses del cliente objetivo.</li>
     *   <li>Para cada otro cliente, calcula la similitud del coseno respecto al
     *       cliente objetivo.</li>
     *   <li>Selecciona los 3 clientes más similares.</li>
     *   <li>Recoge los productos no comprados por el cliente que sí han sido
     *       comprados por al menos uno de los 3 vecinos.</li>
     *   <li>Puntúa cada producto candidato según cuántos de los 3 vecinos lo
     *       recomiendan (3 puntos el más similar, 2 el segundo, 1 el tercero).</li>
     *   <li>Devuelve la lista ordenada descendentemente por puntuación.</li>
     * </ol>
     *
     * <p><b>Precondición:</b> si hay menos de 3 clientes distintos al objetivo,
     * devuelve una lista vacía.</p>
     *
     * @param cliente   cliente registrado para el que se generan recomendaciones
     * @param clientes  lista completa de clientes registrados en el sistema
     * @param productos lista de productos de tienda disponibles
     * @return lista de {@link ProductoTienda} recomendados, ordenada por
     *         relevancia descendente; vacía si no hay suficientes clientes
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

    // -------------------------------------------------------------------------
    // Persistencia (carga y descarga de productos)
    // -------------------------------------------------------------------------

    /**
     * Carga productos desde un fichero CSV con separador {@code ;} y los
     * añade al stock del sistema.
     *
     * <p>Formato esperado por línea:</p>
     * <pre>
     * TIPO(C/J/F);ID;NOMBRE;DESCRIPCION;PRECIO;UNIDADES;CATEGORIAS;PAGINAS;
     * AUTOR;EDITORIAL;AÑO;JUGADORES;EDAD;ESTILO(Cartas/Dados/Tablero/Miniatura);
     * MARCA;MATERIAL;DIMENSION
     * </pre>
     *
     * <p>Cada línea crea un {@link ProductoTienda} y le asigna la categoría
     * correspondiente ({@link Comic}, {@link Juego} o {@link Figura}) según
     * el campo {@code TIPO}.</p>
     *
     * @param archivo ruta al fichero CSV de origen
     */
    public void subirProductosFichero(String archivo) {

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
     * Exporta el catálogo de productos a un fichero CSV con separador {@code ;}.
     *
     * <p>La primera línea escrita es la cabecera:</p>
     * <pre>
     * TIPO(C/J/F);ID;NOMBRE;DESCRIPCION;PRECIO;UNIDADES;CATEGORIAS;PAGINAS;
     * AUTOR;EDITORIAL;AÑO;JUGADORES;EDAD;ESTILO(Cartas/Dados/Tablero/Miniatura);
     * MARCA;MATERIAL;DIMENSION
     * </pre>
     *
     * <p>A continuación se escribe una línea por cada producto, con los campos
     * específicos según su tipo ({@link Comic}, {@link Figura} o {@link Juego}).</p>
     *
     * @param archivo ruta al fichero CSV de destino; se sobreescribirá si existe
     */
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

    // -------------------------------------------------------------------------
    // Filtrado de productos
    // -------------------------------------------------------------------------

    /**
     * Filtra una lista de productos por categoría principal.
     *
     * <p>Categorías aceptadas: {@code "COMIC"}, {@code "FIGURA"} y
     * {@code "JUEGO"}.</p>
     *
     *
     * @param productos lista de productos a filtrar
     * @param categoria cadena con la categoría deseada ({@code "COMIC"},
     *                  {@code "FIGURA"} o {@code "JUEGO"})
     * @return lista filtrada con los productos de la categoría indicada;
     *         vacía si la categoría no coincide con ninguna conocida
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
     * Filtra una lista de productos devolviendo únicamente aquellos con una
     * valoración mayor o igual a la indicada.
     *
     * @param productos  lista de productos a filtrar
     * @param valoracion valoración mínima requerida (inclusive)
     * @return lista de productos cuya valoración es ≥ {@code valoracion}
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
     * Filtra una lista de productos de tienda devolviendo únicamente aquellos
     * con un precio mayor o igual al indicado.
     *
     * @param productos lista de productos de tienda a filtrar
     * @param precio    precio mínimo requerido (inclusive)
     * @return lista de productos de tienda cuyo precio es ≥ {@code precio}
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

    // -------------------------------------------------------------------------
    // Ordenación de productos
    // -------------------------------------------------------------------------

    /**
     * Ordena una lista de productos por nombre en orden alfabético.
     *
     * @param productos lista de productos a ordenar (se modifica in-place)
     * @param flag      {@code true} para orden ascendente (A→Z);
     *                  {@code false} para orden descendente (Z→A)
     * @return la misma lista ordenada
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
     * Ordena una lista de productos por fecha de publicación.
     *
     * @param productos lista de productos a ordenar (se modifica in-place)
     * @param flag      {@code true} para orden ascendente (más antiguo primero);
     *                  {@code false} para orden descendente (más reciente primero)
     * @return la misma lista ordenada
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
     * Ordena una lista de productos de tienda por precio.
     *
     * @param productos lista de productos de tienda a ordenar (se modifica in-place)
     * @param flag      {@code true} para orden ascendente (más barato primero);
     *                  {@code false} para orden descendente (más caro primero)
     * @return la misma lista ordenada
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
     * Ordena una lista de productos por valoración.
     *
     * @param productos lista de productos a ordenar (se modifica in-place)
     * @param flag      {@code true} para orden ascendente (peor valorado primero);
     *                  {@code false} para orden descendente (mejor valorado primero)
     * @return la misma lista ordenada
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