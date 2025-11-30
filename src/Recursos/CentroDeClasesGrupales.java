package Recursos;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Hilos.Instructor;
import RecursosCompartidos.ComplejoInvernal;
import interfaz.Interfaz;

public class CentroDeClasesGrupales {
    // Reemplazo de semáforos por Lock y Conditions
    private Lock lock = new ReentrantLock(true);
    private Condition hayAlumnos = lock.newCondition();   // Reemplaza a instructorEspera
    private Condition claseComienza = lock.newCondition(); // Reemplaza a alumnosEspera

    private int alumnosEsperando = 0; // Variable para contar (ya que no tenemos los 'permits' del semáforo)

    private final static int cantAlumnosXClase = 4;
    private final static int segundosEspera = 14;
    private final static int cantidadInstructores = 5;
    private final static int msDuracionClase = 50000;
    private Instructor[] instructores;

    public CentroDeClasesGrupales() {
        // Ya no inicializamos semáforos, el lock se creó arriba
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
        lock.lock(); // 🔒 Entro a la zona segura
        try {
            Interfaz.alumnoEsperando();
            alumnosEsperando++;
            if (alumnosEsperando >= cantAlumnosXClase) {
                hayAlumnos.signal();
            }
            boolean entreAClase = claseComienza.await(segundosEspera, TimeUnit.SECONDS);
            if (entreAClase) {
                // Vino el instructor (me despertó con signal)
                Interfaz.claseIniciada();
            } else {
                // FALSE: No vino el instructor (Timeout)
                Interfaz.alumnoSeVa();
                // Equivalente a "Devolver el permiso"
                alumnosEsperando--; 
                System.out.println("Devolvió bien el permiso (Se fue por timeout)");
                return; // Importante: Salir del método para no ejecutar el sleep de abajo
            }
        } finally {
            lock.unlock(); // 🔓 Siempre libero el lock
        }
        Thread.sleep(msDuracionClase);
    }

    public void instruirClase() throws InterruptedException {
        // EJECUTADO POR INSTRUCTOR
        lock.lock(); // 🔒
        try {
            Interfaz.instructorEsperando();
            while (alumnosEsperando < cantAlumnosXClase) {
                hayAlumnos.await(); //Me despierto y veo si estan todos 
            }
            //Estan todos los alumnos necesarios
            alumnosEsperando -= cantAlumnosXClase;
            for (int i = 0; i < cantAlumnosXClase; i++) {
                claseComienza.signal();
                //Despierto la cantidad de alumnos q vinieron
            }
        } finally {
            lock.unlock();
        }
        Thread.sleep(msDuracionClase);
    }
}
/*mport java.util.concurrent.Semaphore;
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
}*/
