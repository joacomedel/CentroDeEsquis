package Recursos;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import Hilos.Instructor;
import RecursosCompartidos.ComplejoInvernal;
import interfaz.Interfaz;


//Recurso compartido utilizando Semaforos 

public class CentroDeClasesGrupales {
    private Semaphore instructorEspera;
    private Semaphore alumnosEspera;
    private final static int cantAlumnosXClase = 4;
    private final static int segundosEspera = 14;
    private final static int cantidadInstructores = 5;
    private final static int msDuracionClase = 50000;
    private Instructor[] instructores;

    public CentroDeClasesGrupales() {
        instructorEspera = new Semaphore(0, true);
        alumnosEspera = new Semaphore(0, true);
    }

    public void iniciar(ComplejoInvernal complInv) {
        instructores = new Instructor[cantidadInstructores];
        for (int i = 0; i < cantidadInstructores; i++) {
            instructores[i] = new Instructor(complInv, i);
            instructores[i].start();
        }
    }

    public void participarClase() throws InterruptedException {
        // EJECUTADO POR PERSONA
    	Interfaz.alumnoEsperando();
        instructorEspera.release(1);
        if (alumnosEspera.tryAcquire(segundosEspera, TimeUnit.SECONDS)) {
            // Vino el instructor
        	Interfaz.claseIniciada();
            // Participa de la clase
            Thread.sleep(msDuracionClase);
        } else {
            // No vino el instructor por lo tanto devuelve el permiso y deja de esperar
        	Interfaz.alumnoSeVa();
            if (instructorEspera.tryAcquire(1)) {
                System.out.println("Devolvio bien el permiso");
            } else {
                System.out.println("Devolvio mal el permiso");
            }
        }

    }

    public void instruirClase() throws InterruptedException {
        // EJECUTADO POR INSTRUCTOR

        // instructor espera a que vengan la cantidad de alumnos necesarios para la
        // clase
    	Interfaz.instructorEsperando();
        instructorEspera.acquire(cantAlumnosXClase);
        System.out.println("Vinieron los alumnos necesarios ");
        System.out.println("Da permiso a los alumnos para comenzar la clase");
        alumnosEspera.release(cantAlumnosXClase);
        Thread.sleep(msDuracionClase);
    }
}
