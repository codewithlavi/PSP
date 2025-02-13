import java.util.Random;

class NumeroOculto {
    private int numeroSecreto;
    private boolean juegoTerminado;

    public NumeroOculto(int numeroSecreto) {
        this.numeroSecreto = numeroSecreto;
        this.juegoTerminado = false;
    }

    synchronized public int propuestaNumero(int num) {
        if(juegoTerminado) {
            return -1;
        }
        if(num == numeroSecreto) {
            juegoTerminado = true;
            return 1;
        }
        return 0;
    }
}

class HiloAdivinador implements Runnable {
    private NumeroOculto juego;
    private int id;
    private Random random;

    public HiloAdivinador(NumeroOculto juego, int id) {
        this.juego = juego;
        this.id = id;
        this.random = new Random();
    }

    @Override
    public void run() {
        while(true) {
            int propuesta = random.nextInt(101); // Número entre 0 y 100
            int resultado = juego.propuestaNumero(propuesta);
            
            if(resultado == 1) {
                System.out.println("¡Hilo " + id + " ha adivinado el número! Era: " + propuesta);
                break;
            } else if(resultado == -1) {
                System.out.println("Hilo " + id + " termina: otro hilo ya adivinó el número");
                break;
            } else {
                System.out.println("Hilo " + id + " propone: " + propuesta + " (incorrecto)");
            }

            try {
                Thread.sleep(100); // Pequeña pausa entre intentos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class JuegoAdivinanza {
    public static void main(String[] args) {
        Random random = new Random();
        int numeroSecreto = random.nextInt(101); // Número entre 0 y 100
        System.out.println("Número secreto generado: " + numeroSecreto);

        NumeroOculto juego = new NumeroOculto(numeroSecreto);
        Thread[] hilos = new Thread[10];

        // Crear y lanzar los 10 hilos adivinadores
        for(int i = 0; i < 10; i++) {
            hilos[i] = new Thread(new HiloAdivinador(juego, i+1));
            hilos[i].start();
        }

        // Esperar a que todos los hilos terminen
        for(Thread hilo : hilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Juego terminado");
    }
}