package Recursos;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Hilos.Reloj;
import RecursosCompartidos.ComplejoInvernal;
import interfaz.Interfaz;


//Recurso compartido utilizando LOCKS 

public class Entrada {
    private final int horaApertura = 1000;
    private final int horaCierre = 1700;
    private final int horaInicioReloj = 1630;
    private int horaActual;
    private final Lock lockEntrada = new ReentrantLock();
    private Condition lockCondicion = lockEntrada.newCondition();
    boolean boolAbierto;

    
    
    
    // Look utilizado para que no existan muchas personas adquiriendo el permiso que
    // necesita el reloj para cambiar la hora, se utiliza la condicion
    public Entrada() {
        horaActual = horaInicioReloj;
        boolAbierto = horaActual <= horaCierre && horaActual >= horaApertura;
    }

    public void iniciar(ComplejoInvernal compInv) {
        Reloj reloj = new Reloj(compInv, horaInicioReloj);
        reloj.start();
    }

    public void personaIntentaEntrar() throws InterruptedException {
        // Ejecutado por persona
    	
        lockEntrada.lock();
        if (!boolAbierto) {
        	Interfaz.llegadaComplejoCerrado();
            lockCondicion.await();
        }
        Interfaz.entradaExitosa();
        lockEntrada.unlock();
    }

    public int cambiarHora(int horaActualIn) {
        // Ejecutado por reloj
    	int rtn = 0;
    	//Devuelvo 0 si no hay un cambio en la entrada
        if (horaActual <= horaApertura) {
            // antes estaba cerrado
            if (horaActualIn >= horaApertura) {
                // ahora esta abierto
                lockEntrada.lock();
                
                boolAbierto = true;
                lockCondicion.signalAll();
                lockEntrada.unlock();
                Interfaz.complejoAbre(horaActualIn);
                rtn = 1; //Devuelvo 1 si abrio
            }
        } else {
            if (horaActual <= horaCierre /* && horaActual > horaApertura */) {
                // esta abierto
                if (horaActualIn > horaCierre) {
                    // ahora se paso del horario de cierre , esta cerrado
                	
                    lockEntrada.lock();
                    Interfaz.complejoCierra(horaActualIn);
                    boolAbierto = false;
                    lockEntrada.unlock();
                    rtn = -1; //Devuelvo -1 si esta cerrado
                }
            }
        }
        horaActual = horaActualIn;
        return rtn;
    }
}
