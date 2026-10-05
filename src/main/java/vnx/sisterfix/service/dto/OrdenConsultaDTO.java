package vnx.sisterfix.service.dto;

import lombok.Builder;
import lombok.Getter;
import vnx.sisterfix.domain.enums.EstadoOrden;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrdenConsultaDTO {

    private String codigoSeguimiento;
    private EstadoOrden estadoActual; // Corregido
    private String dispositivo;
    private String problemaReportado;
    private String nombreCliente;
    private LocalDateTime fechaCreacion;
    private List<HistorialItemDTO> historial;

    @Getter
    @Builder
    public static class HistorialItemDTO {
        private EstadoOrden estadoNuevo;
        private LocalDateTime fechaCambio;
        private String observaciones;
    }
}