package vnx.sisterfix.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Respuesta JSON de la busqueda de clientes del formulario. */
@Getter
@AllArgsConstructor
public class ClienteResumenDTO {
    private String nombre;
    private String telefono;
    private String email;
}