package Recursos;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import interfaz.Interfaz;

//Recurso compartido utilizando LOCKS  


public class MedioDeElevacion {
    private int usos;
    private int cantMolinetes;
    private int usoActual;
    private final Lock lock;
    private final Condition conditionEnUso;
    private final Condition conditionEsperaResto;
    private boolean boolEnUso;
    private final int segundoMaximosEspera = 40;

    
    
    public MedioDeElevacion(int n) {
        cantMolinetes = n;
        usoActual = 0;
        lock = new ReentrantLock();
        boolEnUso = false;
        usos = 0;
        conditionEsperaResto = lock.newCondition();

        conditionEnUso = lock.newCondition();
    }

    public boolean subir() throws InterruptedException {
        boolean subio = false;
        try {
            lock.lock();
            while (boolEnUso) {
            	Interfaz.esperandoGrupo();
                conditionEnUso.await(segundoMaximosEspera, TimeUnit.SECONDS);
            }
            usoActual++;
            Interfaz.llegadaMolinete(usoActual, cantMolinetes);
            subio = true;
            if (usoActual == cantMolinetes) {
                // es el ultimo en entrar0
                boolEnUso = true;
                conditionEsperaResto.signalAll();
                Interfaz.grupoCompletoViaja(cantMolinetes);
            } else {
                // no es el ultimo
            	Interfaz.esperandoGrupo();
                conditionEsperaResto.await();
            }
            lock.unlock();
            Interfaz.viajandoEnMedio(); 
            Thread.sleep(500);// Simula duracion del viaje
            lock.lock();
            usoActual--;
            if (usoActual == 0) {
                boolEnUso = false;
                conditionEnUso.signalAll();
                Interfaz.ultimaPersonaBaja();
                usos++; //De cada medio cuenta la cantidad de veces q se uso
            } else {
            	Interfaz.personaSeBajo();
            }
            //usos++; De cada medio cuento la cantidad de personas q lo usaron 
            lock.unlock();
        } catch (InterruptedException e) {
            // TODO: handle exception
            subio = false;
            Interfaz.esperandoMuchoTiempo();
        }
        return subio;
    }

    public void reiniciarUso() throws InterruptedException {
        lock.lock();
        usos = 0;
        lock.unlock();
    }

    public boolean estaEnUso() {
        boolean bool;
        lock.lock();
        bool = boolEnUso;
        lock.unlock();
        return bool;
    }

    public int getUsos() throws InterruptedException {
        int temp = 0;
        lock.lock();
        temp = usos;
        lock.unlock();
        return temp;
    }
}
