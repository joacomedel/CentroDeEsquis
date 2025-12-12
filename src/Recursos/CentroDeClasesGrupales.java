package Recursos;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Hilos.Instructor;
import RecursosCompartidos.ComplejoInvernal;
import interfaz.Interfaz;

public class CentroDeClasesGrupales {
    
    private Lock lock = new ReentrantLock(true);
    
    // Condición general para despertar al instructor (se usa para ambos tipos)
    private Condition hayAlumnos = lock.newCondition();   
    
    // --- VARIABLES PARA SKI ---
    private Condition claseComienzaSki = lock.newCondition(); 
    private Condition claseTerminoSki = lock.newCondition();
    private int alumnosEsperandoSki = 0; 
    
    // --- VARIABLES PARA SNOWBOARD ---
    private Condition claseComienzaSnow = lock.newCondition(); 
    private Condition claseTerminoSnow = lock.newCondition();
    private int alumnosEsperandoSnow = 0; 

    private final static int cantAlumnosXClase = 4;
    private final static int segundosEspera = 14; 
    private final static int cantidadInstructores = 5;
    private final static int msDuracionClase = 8000; 
    private Instructor[] instructores;

    public CentroDeClasesGrupales() {
    }

    public void iniciar(ComplejoInvernal complInv) {
        instructores = new Instructor[cantidadInstructores];
        for (int i = 0; i < cantidadInstructores; i++) {
            instructores[i] = new Instructor(complInv, i);
            instructores[i].start();
        }
    }

    public void participarClase(String tipoClase) throws InterruptedException {
        // Despachador según el tipo de clase
        if ("Sky".equalsIgnoreCase(tipoClase) || "Ski".equalsIgnoreCase(tipoClase)) {
            participarClaseSki();
        } else if ("Snow".equalsIgnoreCase(tipoClase) || "Snowboard".equalsIgnoreCase(tipoClase)) {
            participarClaseSnow();
        }
    }

    private void participarClaseSnow() throws InterruptedException {
        lock.lock(); 
        try {
            Interfaz.alumnoEsperando(); // Mensaje genérico o específico según tu interfaz
            alumnosEsperandoSnow++;
            
            // Si completo el cupo de Snow, despierto al instructor
            if (alumnosEsperandoSnow >= cantAlumnosXClase) {
                hayAlumnos.signal();
            }

            // Espero a que empiece la clase de Snow
            boolean entreAClase = claseComienzaSnow.await(segundosEspera, TimeUnit.SECONDS);
            
            if (entreAClase) {
                Interfaz.claseIniciada();
            } else {
                // Timeout: No vino instructor
                Interfaz.alumnoSeVa();
                alumnosEsperandoSnow--; 
                return; // Se va
            }
        } finally {
            lock.unlock(); 
        }

        // Esperar fin de clase Snow
        lock.lock();
        try {
            claseTerminoSnow.await(); 
        } finally {
            lock.unlock();
        }
    }

    private void participarClaseSki() throws InterruptedException {
        lock.lock(); 
        try {
            Interfaz.alumnoEsperando();
            alumnosEsperandoSki++;
            
            // Si completo el cupo de Ski, despierto al instructor
            if (alumnosEsperandoSki >= cantAlumnosXClase) {
                hayAlumnos.signal();
            }

            // Espero a que empiece la clase de Ski
            boolean entreAClase = claseComienzaSki.await(segundosEspera, TimeUnit.SECONDS);
            
            if (entreAClase) {
                Interfaz.claseIniciada();
            } else {
                // Timeout
                Interfaz.alumnoSeVa();
                alumnosEsperandoSki--; 
                return; // Se va
            }
        } finally {
            lock.unlock(); 
        }

        // Esperar fin de clase Ski
        lock.lock();
        try {
            claseTerminoSki.await(); 
        } finally {
            lock.unlock();
        }
    }

    public void instruirClase() throws InterruptedException {
        // EJECUTADO POR INSTRUCTOR
        lock.lock(); 
        boolean esClaseSki = false; // Bandera local para saber qué clase dio este instructor

        try {
            Interfaz.instructorEsperando();
            
            while (alumnosEsperandoSki < cantAlumnosXClase && alumnosEsperandoSnow < cantAlumnosXClase) {	
                hayAlumnos.await(); 
            }
            
            //si me despertaron y hay suficientes de ski les doy la clase a ellos
            if (alumnosEsperandoSki >= cantAlumnosXClase) {
                esClaseSki = true;
                alumnosEsperandoSki -= cantAlumnosXClase;
                for (int i = 0; i < cantAlumnosXClase; i++) {
                    claseComienzaSki.signal();
                }
            } else {
                esClaseSki = false;
                alumnosEsperandoSnow -= cantAlumnosXClase;
                // despierto a mis 4 alumnos de Snow
                for (int i = 0; i < cantAlumnosXClase; i++) {
                    claseComienzaSnow.signal();
                }
            }
        } finally {
            lock.unlock();
        }

        // Simulación del tiempo de la clase (fuera del lock)
        Thread.sleep(msDuracionClase);

        // Aviso de Fin de Clase
        lock.lock();
        try {
            if (esClaseSki) {
                for (int i = 0; i < cantAlumnosXClase; i++) claseTerminoSki.signal();
            } else {
                for (int i = 0; i < cantAlumnosXClase; i++) claseTerminoSnow.signal();
            }
        } finally {
            lock.unlock();
        }
    }
}