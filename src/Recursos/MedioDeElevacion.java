package Recursos;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import interfaz.Interfaz;

public class MedioDeElevacion {

    private AtomicInteger usos; 
    private final int cantMolinetes; 
    
    private final CyclicBarrier barreraSubida;
    private final CyclicBarrier barreraBajada;
    
    private final int segundoMaximosEspera = 40; 
    
    // Tiempos físicos
    private final int tiempoEntreSillas = 2000; // Tiempo que la barrera queda "ocupada" saliendo
    private final int tiempoViaje = 3000;       // Tiempo de viaje en el aire

    public MedioDeElevacion(int n) {
        this.cantMolinetes = n;
        usos = new AtomicInteger(0);

        // --- BARRERA DE SUBIDA ---
        this.barreraSubida = new CyclicBarrier(n, () -> {
            try {
                // Esta acción bloquea la liberación de la barrera.
                // Simula el espacio físico/tiempo entre una silla y la otra.
                // Nadie puede empezar a subir al siguiente grupo hasta que esto termine.
                Interfaz.grupoCompletoViaja(cantMolinetes); 
                Thread.sleep(tiempoEntreSillas); 
                this.incrementarUso(); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // --- BARRERA DE BAJADA ---
        this.barreraBajada = new CyclicBarrier(n, () -> {
        });
    }

    public boolean subir() throws InterruptedException {
        Interfaz.esperandoGrupo();
        Interfaz.llegadaMolinete(barreraSubida.getNumberWaiting(), cantMolinetes);
        
        try {
            // 1. SUBIDA: Esperamos a llenar la silla
            // Si la barrera está ejecutando la acción (el sleep de 2s), nos quedamos bloqueados aquí
            barreraSubida.await(segundoMaximosEspera, TimeUnit.SECONDS);
            
            // Una vez que pasamos el await, significa que la silla ya "salió" de la plataforma.
            // Ahora estamos en el aire.
            Interfaz.viajandoEnMedio(); 
            

        } catch (TimeoutException | BrokenBarrierException e) {
            Interfaz.esperandoMuchoTiempo();
            return false;
        }
        
        try {
        	Thread.sleep(tiempoViaje); 
			barreraBajada.await();
			Interfaz.personaSeBajo(); 
		} catch (InterruptedException | BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return true;
    }


    private int incrementarUso() {
        return usos.addAndGet(cantMolinetes);
    }
    public void reiniciarUso() {
        usos.set(0);
    }
    public int getUsos() {
        return usos.get();
    }
    public boolean estaEnUso() {
        return true;
    }
}