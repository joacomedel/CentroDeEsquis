package Recursos;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
                System.out.println("Entro al medio de elevacion pero estaba en uso");
                conditionEnUso.await(segundoMaximosEspera, TimeUnit.SECONDS);
            }
            usoActual++;
            System.out.println("Persona:" + Thread.currentThread().getName() + " subio, uso actual " + usoActual);
            subio = true;
            if (usoActual == cantMolinetes) {
                // es el ultimo en entrar0
                boolEnUso = true;
                conditionEsperaResto.signalAll();
                System.out.println("Entro la ultima persona necesaria para que salga el medio de elevacion");
            } else {
                // no es el ultimo
                System.out.println("Espera que vengan el resto de personas");
                conditionEsperaResto.await();
            }
            lock.unlock();
            Thread.sleep(500);// Simula duracion del viaje
            lock.lock();
            usoActual--;
            if (usoActual == 0) {
                boolEnUso = false;
                conditionEnUso.signalAll();
                System.out.println("Bajo la ultima persona");
            } else {
                System.out.println("Ya se bajo");
            }
            usos++;
            lock.unlock();
        } catch (InterruptedException e) {
            // TODO: handle exception
            subio = false;
            System.out.println("Espero mucho pero no viene nadie mas");
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
