package descuentos;
import java.util.Date;

public class Descuento {
    private final Date fechaInicio;
    private final Date fechaFin;

    public Descuento(Date fechaInicio, Date fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    //GETTERS//
    public Date getFechaInicio() {return this.fechaInicio;}
    public Date getFechaFin() {return this.fechaFin;}



}
