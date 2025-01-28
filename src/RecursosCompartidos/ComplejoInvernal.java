package RecursosCompartidos;

import Recursos.CentroDeClasesGrupales;
import Recursos.Confiteria;
import Recursos.Entrada;

public class ComplejoInvernal {

    private Entrada entrada;
    private Confiteria confiteria;
    private CentroDeClasesGrupales centroDeClasesGrupales;

    public ComplejoInvernal() {
        entrada = new Entrada(this);
        confiteria = new Confiteria();
        centroDeClasesGrupales = new CentroDeClasesGrupales(this);
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

    public void participarClase() throws InterruptedException {
        centroDeClasesGrupales.participarClase();
    }

    public void instruirClase() throws InterruptedException {
        centroDeClasesGrupales.instruirClase();
    }
}
