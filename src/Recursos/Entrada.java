package Recursos;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Hilos.Reloj;
import RecursosCompartidos.ComplejoInvernal;
import interfaz.Interfaz;


//Recurso compartido utilizando Monitores 

public class Entrada {
    private final static  int horaApertura = 1000;
    private final static int horaCierre = 1700;
    private final static int horaInicioReloj = 1000;
    boolean boolAbierto = horaInicioReloj <= horaCierre && horaInicioReloj >= horaApertura;;

    
    
    //Metodos q utiliza el reloj para 
    public static int getHoraApertura() {
		return horaApertura;
	}
    public static int getHoraCierre() {
		return horaCierre;
	}
    public static int getHoraInicioReloj() {
		return horaInicioReloj;
	}
    
    public Entrada() {
    }

    public void iniciar(ComplejoInvernal compInv) {
        Reloj reloj = new Reloj(compInv, horaInicioReloj);
        reloj.start();
    }

    public synchronized void personaIntentaEntrar() throws InterruptedException {
        // Ejecutado por persona
    	Interfaz.llegadaComplejoCerrado();
    	System.out.println(boolAbierto);
        while (!boolAbierto) {
            wait();
        }
        Interfaz.entradaExitosa();
    }
    public synchronized void abrirComplejo(int horaActualIn) throws InterruptedException {
    	boolAbierto = true;
    	notifyAll();
    	Interfaz.complejoAbre(horaActualIn);
    }
    public synchronized void cerrarComplejo(int horaActualIn) throws InterruptedException {
    	boolAbierto = false;
    	Interfaz.complejoCierra(horaActualIn);
    }
   
}
