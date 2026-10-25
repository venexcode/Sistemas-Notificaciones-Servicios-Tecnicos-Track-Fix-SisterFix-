package vnx.sisterfix.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrdenNoEncontradaException extends RuntimeException {
    public OrdenNoEncontradaException(Long id) {
        super("Orden no encontrada: " + id);
    }
}
