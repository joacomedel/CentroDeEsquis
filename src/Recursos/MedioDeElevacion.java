package Recursos;

import java.util.concurrent.Semaphore;

public class MedioDeElevacion {
    Semaphore molinetes;

    public MedioDeElevacion(int n) {
        molinetes = new Semaphore(n);// ?
    }

    public void subir() {

    }
}
