package vnx.sisterfix.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimiterTest {

    private static final Duration VENTANA = Duration.ofMinutes(1);

    private AtomicLong ahora;
    private RateLimiter limitador;

    @BeforeEach
    void preparar() {
        ahora = new AtomicLong(1_000_000);
        limitador = new RateLimiter(ahora::get);
    }

    @Test
    void permiteHastaElMaximoYLuegoBloquea() {
        assertThat(limitador.permitir("ip", 3, VENTANA)).isTrue();
        assertThat(limitador.permitir("ip", 3, VENTANA)).isTrue();
        assertThat(limitador.permitir("ip", 3, VENTANA)).isTrue();

        assertThat(limitador.permitir("ip", 3, VENTANA)).isFalse();
    }

    @Test
    void sePermiteDeNuevoCuandoPasaLaVentana() {
        for (int i = 0; i < 3; i++) {
            limitador.permitir("ip", 3, VENTANA);
        }
        assertThat(limitador.permitir("ip", 3, VENTANA)).isFalse();

        ahora.addAndGet(VENTANA.toMillis() + 1);

        assertThat(limitador.permitir("ip", 3, VENTANA)).isTrue();
    }

    @Test
    void cadaClaveTieneSuPropioContador() {
        limitador.permitir("a", 1, VENTANA);

        assertThat(limitador.permitir("a", 1, VENTANA)).isFalse();
        assertThat(limitador.permitir("b", 1, VENTANA)).isTrue();
    }

    @Test
    void reiniciarLimpiaTodosLosContadores() {
        limitador.permitir("ip", 1, VENTANA);

        limitador.reiniciar();

        assertThat(limitador.permitir("ip", 1, VENTANA)).isTrue();
    }
}