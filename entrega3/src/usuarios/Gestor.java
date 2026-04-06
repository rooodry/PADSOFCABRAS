package usuarios;

import estadisticas.Estadistica;
import java.util.*;

public class Gestor extends Usuario {

    private List<Estadistica> estadistica;

    public Gestor(String nombre, String contraseña) {
        super(nombre, contraseña);
        this.estadistica = new ArrayList<>();
    }

    public void addEstadistica(Estadistica e) {this.estadistica.add(e);}
    public List<Estadistica> getEstadistica() {return new ArrayList<>(this.estadistica);}
}
