package Hilos;

import Recursos.Entrada;
import RecursosCompartidos.ComplejoInvernal;
import interfaz.Interfaz;

public class Reloj extends Thread {
    // usado en Entrada
    private int horaMilitar;
    private ComplejoInvernal compInv;
    private int horaActual;
    private final  int horaApertura = Entrada.getHoraApertura();
    private final  int horaCierre = Entrada.getHoraCierre();
    private final  int horaInicioReloj = Entrada.getHoraInicioReloj();
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
                if(this.cambiarHora(horaMilitar) == -1) {
                	//Significa que cambie la hora y cerre
                	compInv.cerrar();
                	System.out.println("Cerro el complejo");
                }
                Thread.sleep(15000);
            }
        } catch (InterruptedException e) {
        }
    }

    private void sumarTiempo(int minutosASumar) throws InterruptedException {
        int horas = horaMilitar / 100;
        int minutos = horaMilitar % 100;
        int horasASumar = (minutos + minutosASumar) / 60;
        int minutosNuevos = (minutos + minutosASumar) % 60;
        int horasNuevas = (horas + horasASumar) % 24;
        horaMilitar = (horasNuevas * 100) + minutosNuevos;
        System.out.println("[RELOJ-28] Nueva Hora:" + horaMilitar);
    }
    public synchronized int cambiarHora(int horaActualIn) throws InterruptedException {
        // Ejecutado por reloj
    	int rtn = 0;
    	//Devuelvo 0 si no hay un cambio en la entrada
        if (horaActual <= horaApertura) {
            // antes estaba cerrado
            if (horaActualIn >= horaApertura) {
            	compInv.abrirEntrada(horaActualIn);
                rtn = 1; //Devuelvo 1 si abrio
            }
        } else {
            if (horaActual <= horaCierre /* && horaActual > horaApertura */) {
                // esta abierto
                if (horaActualIn > horaCierre) {
                    // ahora se paso del horario de cierre , esta cerrado
                    compInv.cerrarEntrada(horaActualIn);
                    rtn = -1; //Devuelvo -1 si esta cerrado
                }
            }
        }
        horaActual = horaActualIn;
        return rtn;
    }
}
