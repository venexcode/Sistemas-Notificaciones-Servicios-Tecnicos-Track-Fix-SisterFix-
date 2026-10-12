package vnx.sisterfix.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EtiquetaDTO {
    private Long id;
    private String codigoSeguimiento;
    private String dispositivo;
    private String problemaReportado;
    private String nombreCliente;
    private String telefonoCliente;
    private LocalDateTime fechaCreacion;
}