package intercambios;
import java.util.Calendar;
import java.util.Date;


public class Intercambio {

    private final Date fechaOferta;
    private final Date fechaLimite;
    private Date fechaAceptada;
    private boolean intercambiado;

    public Intercambio(Date fechaOferta) {

        this.fechaOferta = new Date(fechaOferta.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.fechaOferta);
        calendar.add(Calendar.DAY_OF_YEAR, 7);

        
        this.fechaLimite = calendar.getTime();

        this.fechaAceptada = null;

        this.intercambiado = false;


    }

    //SETTERS//
    public void setFechaAceptada(Date fecha) {this.fechaAceptada = new Date();}
    public void setIntercambiado(boolean flag) {this.intercambiado = flag;}

    //GETTERS//
    public Date getFechaOferta() {return new Date(this.fechaOferta.getTime());}
    public Date getFechaLimite() {return new Date(this.fechaLimite.getTime());}
    public Date getFechaAceptada() {return new Date(this.fechaAceptada.getTime());}
    public boolean getIntercambiado() {return this.intercambiado;}


}
