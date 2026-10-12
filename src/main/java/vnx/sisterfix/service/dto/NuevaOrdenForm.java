package vnx.sisterfix.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Datos del formulario "Registrar equipo". */
@Getter
@Setter
@NoArgsConstructor
public class NuevaOrdenForm {

    @NotBlank(message = "Escribe el teléfono del cliente.")
    @Pattern(regexp = "^$|^[0-9+()\\s-]{6,25}$",
            message = "Usa solo números, espacios, guiones o +. Mínimo 6 caracteres.")
    private String telefono;

    @NotBlank(message = "Escribe el nombre del cliente.")
    @Size(max = 100, message = "Máximo 100 caracteres.")
    private String nombre;

    @Email(message = "Escribe un email válido.")
    @Size(max = 120, message = "Máximo 120 caracteres.")
    private String email;

    @NotBlank(message = "Indica qué equipo es.")
    @Size(max = 100, message = "Máximo 100 caracteres.")
    private String dispositivo;

    @NotBlank(message = "Describe el problema que reporta el cliente.")
    @Size(max = 255, message = "Máximo 255 caracteres.")
    private String problemaReportado;
}