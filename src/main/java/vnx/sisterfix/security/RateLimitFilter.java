package vnx.sisterfix.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

/**
 * Limita los intentos de login (POST /login) y las consultas publicas de codigo (GET /sisterfix/consultar).
 * Se agrega dentro de la cadena de Spring Security; por eso NO es un @Component.
 * La IP se toma de getRemoteAddr(): detras de un proxy, configura server.forward-headers-strategy=native.
 */
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiter limitador;
    private final int loginMaximo;
    private final Duration loginVentana;
    private final int consultaMaximo;
    private final Duration consultaVentana;

    public RateLimitFilter(RateLimiter limitador, int loginMaximo, Duration loginVentana,
                           int consultaMaximo, Duration consultaVentana) {
        this.limitador = limitador;
        this.loginMaximo = loginMaximo;
        this.loginVentana = loginVentana;
        this.consultaMaximo = consultaMaximo;
        this.consultaVentana = consultaVentana;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                   @NonNull FilterChain chain)
            throws ServletException, IOException {

        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        String ip = request.getRemoteAddr();

        if ("POST".equals(request.getMethod()) && "/login".equals(ruta)) {
            String clave = "login:" + ip;
            if (!limitador.permitir(clave, loginMaximo, loginVentana)) {
                response.setHeader("Retry-After", String.valueOf(limitador.segundosParaReintentar(clave, loginVentana)));
                response.sendRedirect(request.getContextPath() + "/login?bloqueado");
                return;
            }
        } else if ("GET".equals(request.getMethod()) && "/sisterfix/consultar".equals(ruta)) {
            String clave = "consulta:" + ip;
            if (!limitador.permitir(clave, consultaMaximo, consultaVentana)) {
                response.setHeader("Retry-After", String.valueOf(limitador.segundosParaReintentar(clave, consultaVentana)));
                response.sendError(HttpStatus.TOO_MANY_REQUESTS.value());
                return;
            }
        }

        chain.doFilter(request, response);
    }
}