package RecursosCompartidos;

import Hilos.Reloj;
import Recursos.Confiteria;
import Recursos.Entrada;

public class ComplejoInvernal {

    Entrada entrada;
    Confiteria confiteria;

    public ComplejoInvernal() {
        entrada = new Entrada(this);
        confiteria = new Confiteria();
    }

    public void entrarConfiteria(int tipoUso) throws InterruptedException {
        confiteria.usaConfiteria(tipoUso);
    }

    public void cambiarHora(int horaActualIn) {
        entrada.cambiarHora(horaActualIn);
    }

    public void entrarAComplejo() throws InterruptedException {
        entrada.personaIntentaEntrar();
    }
}
