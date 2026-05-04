package intercambios;

import java.util.Calendar;
import java.util.Date;
import java.io.Serializable;
import utilidades.EstadoOferta;

/**
 * Clase que gestiona el ciclo de vida, los plazos temporales 
 * y la ejecución final de una oferta.
 */
public class Intercambio implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Date fechaOferta;
    private final Date fechaLimite;
    private Date fechaAceptada;
    private boolean intercambiado;
    private final Oferta oferta;

    /**
     * Constructor de la clase Intercambio.
     * Fija un límite predeterminado de 7 días para responder a la oferta.
     *
     * @param fechaOferta Fecha en la que se generó la propuesta.
     * @param oferta      Objeto Oferta asociado a este intercambio.
     */
    public Intercambio(Date fechaOferta, Oferta oferta) {
        this.fechaOferta = new Date(fechaOferta.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.fechaOferta);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        
        this.fechaLimite = calendar.getTime();
        this.fechaAceptada = null;
        this.intercambiado = false;
        this.oferta = oferta;
    }

    /**
     * Establece manualmente la fecha en la que fue aceptada la oferta.
     * @param fecha Fecha de aceptación.
     */
    public void setFechaAceptada(Date fecha) {
        this.fechaAceptada = new Date(fecha.getTime());
    }

    /**
     * Confirma si el proceso físico de intercambio ya se ha materializado.
     * @param flag true si los productos cambiaron de mano, false si aún no.
     */
    public void setIntercambiado(boolean flag) {
        this.intercambiado = flag;
    }

    /**
     * Obtiene la fecha inicial en la que se propuso el intercambio.
     * @return Copia de la fecha de creación.
     */
    public Date getFechaOferta() {
        return new Date(this.fechaOferta.getTime());
    }

    /**
     * Obtiene la fecha límite tras la cual la oferta expira o es inválida.
     * @return Copia de la fecha límite (7 días tras la creación).
     */
    public Date getFechaLimite() {
        return new Date(this.fechaLimite.getTime());
    }

    /**
     * Obtiene la fecha exacta en la que se resolvió y aceptó el intercambio.
     * @return Copia de la fecha de aceptación, o null si aún no se acepta.
     */
    public Date getFechaAceptada() {
        return this.fechaAceptada != null ? new Date(this.fechaAceptada.getTime()) : null;
    }

    /**
     * Verifica si el intercambio se ha finalizado y materializado.
     * @return true si los productos se han entregado/cambiado, false si está pendiente.
     */
    public boolean getIntercambiado() {
        return this.intercambiado;
    }

    /**
     * Obtiene la oferta asociada a este registro temporal de intercambio.
     * @return Objeto Oferta principal.
     */
    public Oferta getOferta() {
        return this.oferta;
    }

    /**
     * Procesa la aceptación de la oferta y registra la fecha actual como momento de acuerdo.
     */
    public void aceptarOferta() {
        this.oferta.setEstadoOferta(EstadoOferta.ACEPTADA);
        this.fechaAceptada = new Date();
    }

    /**
     * Procesa el rechazo de la oferta alterando su estado a RECHAZADA.
     */
    public void rechazarOferta() {
        this.oferta.setEstadoOferta(EstadoOferta.RECHAZADA);
    }
}
