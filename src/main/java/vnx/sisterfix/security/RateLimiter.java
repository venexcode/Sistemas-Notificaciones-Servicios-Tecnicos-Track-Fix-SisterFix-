package vnx.sisterfix.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * Limitador de ventana deslizante en memoria: como maximo N intentos por clave en una ventana de tiempo.
 * Es suficiente para una sola instancia de la aplicacion; con varias instancias haria falta un almacen compartido.
 */
@Component
public class RateLimiter {

    private static final int LIMITE_CLAVES = 10_000;

    private final Map<String, Deque<Long>> registros = new ConcurrentHashMap<>();
    private final LongSupplier reloj;

    @Autowired
    public RateLimiter() {
        this(System::currentTimeMillis);
    }

    RateLimiter(LongSupplier reloj) {
        this.reloj = reloj;
    }

    /** Registra un intento y devuelve true si esta permitido. Un intento rechazado no se registra. */
    public boolean permitir(String clave, int maximo, Duration ventana) {
        long ahora = reloj.getAsLong();
        long limite = ahora - ventana.toMillis();

        if (registros.size() > LIMITE_CLAVES) {
            purgar(limite);
        }

        Deque<Long> intentos = registros.computeIfAbsent(clave, k -> new ArrayDeque<>());
        synchronized (intentos) {
            while (!intentos.isEmpty() && intentos.peekFirst() <= limite) {
                intentos.pollFirst();
            }
            if (intentos.size() >= maximo) {
                return false;
            }
            intentos.addLast(ahora);
            return true;
        }
    }

    /** Segundos hasta que se libere el intento mas antiguo de la ventana (minimo 1). */
    public long segundosParaReintentar(String clave, Duration ventana) {
        Deque<Long> intentos = registros.get(clave);
        if (intentos == null) {
            return 1;
        }
        synchronized (intentos) {
            Long masAntiguo = intentos.peekFirst();
            if (masAntiguo == null) {
                return 1;
            }
            long faltanMs = masAntiguo + ventana.toMillis() - reloj.getAsLong();
            return Math.max(1, (faltanMs + 999) / 1000);
        }
    }

    public void reiniciar() {
        registros.clear();
    }

    private void purgar(long limite) {
        registros.values().removeIf(cola -> {
            synchronized (cola) {
                return cola.isEmpty() || cola.peekLast() <= limite;
            }
        });
    }
}