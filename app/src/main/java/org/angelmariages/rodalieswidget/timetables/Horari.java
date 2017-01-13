package org.angelmariages.rodalieswidget.timetables;

public class Horari {
    private final String hora_sortida;
    private final String hora_arribada;
    private String duracio_trajecte;
    private final int origen;
    private final int desti;
    private final String linia;

    public Horari(String hora_sortida, String hora_arribada, String duracio_trajecte, int origen, int desti, String linia) {
        this.hora_sortida = hora_sortida;
        this.hora_arribada = hora_arribada;
        this.duracio_trajecte = duracio_trajecte;
        this.origen = origen;
        this.desti = desti;
        this.linia = linia;
    }

    public String getHora_sortida() {
        return hora_sortida;
    }

    public String getHora_arribada() {
        return hora_arribada;
    }

    public String getDuracio_trajecte() {
        return duracio_trajecte;
    }

    public int getOrigen() {
        return origen;
    }

    public int getDesti() {
        return desti;
    }

    public String getLinia() {
        return linia;
    }
}
