package Recursos;

public class Molinete {
    private int usos;

    public Molinete() {
        usos = 0;
    }

    public void usar() {
        usos++;
    }

    public int getUsos() {
        return usos;
    }

    public void setUsos(int usos) {
        this.usos = usos;
    }

}
