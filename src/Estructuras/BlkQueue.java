package Estructuras;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class BlkQueue<E> {
    private Queue<E> cola;
    private Semaphore mutex;
    private Semaphore cantElem;

    public BlkQueue() {
        mutex = new Semaphore(1);
        cantElem = new Semaphore(0);
        cola = new LinkedList<E>();
    }

    public boolean add(E elem) throws InterruptedException {
        boolean rtr = false;
        mutex.acquire();
        cantElem.release(1);
        rtr = cola.add(elem);
        mutex.release();
        return rtr;
    }

    public E peek() throws InterruptedException {
        E elem;
        mutex.acquire();
        elem = cola.peek();
        mutex.release();
        return elem;
    }

    public E pool() throws InterruptedException {
        E elem;
        cantElem.acquire();
        mutex.acquire();
        elem = cola.poll();
        mutex.release();
        return elem;
    }

    public LinkedList<E> pool(int i) throws InterruptedException {
        LinkedList<E> list = new LinkedList<>();
        cantElem.acquire(i);
        mutex.acquire();
        for (int j = 0; j < i; j++) {
            list.add(cola.poll());
        }
        mutex.release();
        return list;
    }
}
