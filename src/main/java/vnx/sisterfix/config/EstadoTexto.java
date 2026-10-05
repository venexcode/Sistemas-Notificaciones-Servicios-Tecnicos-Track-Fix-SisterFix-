package vnx.sisterfix.config;

import org.springframework.stereotype.Component;
import vnx.sisterfix.domain.enums.EstadoOrden;

/** Permite pedir el texto de un estado desde las plantillas, con el enum o con su nombre. */
@Component("estados")
public class EstadoTexto {

    public String etiqueta(Object estado) {
        return resolver(estado).getEtiqueta();
    }

    public String descripcion(Object estado) {
        return resolver(estado).getDescripcion();
    }

    private EstadoOrden resolver(Object estado) {
        return estado instanceof EstadoOrden e ? e : EstadoOrden.valueOf(String.valueOf(estado));
    }
}