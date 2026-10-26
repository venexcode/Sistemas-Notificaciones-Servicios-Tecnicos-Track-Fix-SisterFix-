package vnx.sisterfix.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vnx.sisterfix.domain.enums.Rol;

@Getter
@Setter
@NoArgsConstructor
public class NuevoUsuarioForm {

    @NotBlank(message = "Escribe un nombre de usuario.")
    @Size(min = 3, max = 40, message = "Entre 3 y 40 caracteres.")
    @Pattern(regexp = "^[A-Za-z0-9._-]*$", message = "Usa solo letras, números, punto, guion o guion bajo.")
    private String username;

    @NotBlank(message = "Escribe una contraseña.")
    @Size(min = 8, max = 72, message = "Entre 8 y 72 caracteres.")
    private String password;

    @NotNull(message = "Elige un rol.")
    private Rol rol = Rol.TECNICO;
}