package intercambios;
import java.util.Calendar;
import java.util.Date;

import utilidades.EstadoOferta;


public class Intercambio {

    private final Date fechaOferta;
    private final Date fechaLimite;
    private Date fechaAceptada;
    private boolean intercambiado;
    private final Oferta oferta;

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

    //SETTERS//
    public void setFechaAceptada(Date fecha) {this.fechaAceptada = new Date(fecha.getTime());}
    public void setIntercambiado(boolean flag) {this.intercambiado = flag;}

    //GETTERS//
    public Date getFechaOferta() {return new Date(this.fechaOferta.getTime());}
    public Date getFechaLimite() {return new Date(this.fechaLimite.getTime());}
    public Date getFechaAceptada() {return this.fechaAceptada != null ? new Date(this.fechaAceptada.getTime()) : null;}
    public boolean getIntercambiado() {return this.intercambiado;}
    public Oferta getOferta() {return this.oferta;}


    public void aceptarOferta() {
        this.oferta.setEstadoOferta(EstadoOferta.ACEPTADA);
    }

    public void rechazarOferta() {
        this.oferta.setEstadoOferta(EstadoOferta.RECHAZADA);
    }


}
