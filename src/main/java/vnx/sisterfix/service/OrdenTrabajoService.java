package vnx.sisterfix.service;

import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.domain.model.OrdenTrabajo;
import vnx.sisterfix.service.dto.OrdenConsultaDTO;

import java.util.List;
import java.util.Optional;

public interface OrdenTrabajoService {

    OrdenTrabajo crearOrden(String dispositivo, String problema, Long clienteId);

    Optional<OrdenConsultaDTO> buscarPorCodigoSeguimiento(String codigo);

    List<OrdenTrabajo> listarTodas();

    OrdenTrabajo actualizarEstado(Long ordenId, EstadoOrden nuevoEstado, String observaciones);
}