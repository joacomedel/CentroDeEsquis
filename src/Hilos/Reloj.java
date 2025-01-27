package Hilos;

import RecursosCompartidos.ComplejoInvernal;

public class Reloj extends Thread {
    // usado en Entrada
    private int horaMilitar;
    private ComplejoInvernal compInv;

    // Reloj con formato militar 0000 1000 1200 1300
    // Los dos primeros son para minutos , los dos ultimos para horas
    public Reloj(ComplejoInvernal compInvIn, int horaInicial) {
        compInv = compInvIn;
        horaMilitar = horaInicial;
    }

    @Override
    public void run() {
        try {
            while (true) {
                sumarTiempo(15);
                compInv.cambiarHora(horaMilitar);
                Thread.sleep(60000);
            }
        } catch (InterruptedException e) {
        }
    }

    public void sumarTiempo(int minutosASumar) throws InterruptedException {
        int horas = horaMilitar / 100;
        int minutos = horaMilitar % 100;
        int horasASumar = (minutos + minutosASumar) / 60;
        int minutosNuevos = (minutos + minutosASumar) % 60;
        int horasNuevas = (horas + horasASumar) % 24;
        horaMilitar = (horasNuevas * 100) + minutosNuevos;
        System.out.println("[RELOJ-28] Nueva Hora:" + horaMilitar);
    }
}
