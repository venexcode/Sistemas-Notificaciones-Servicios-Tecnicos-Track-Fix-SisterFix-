package vnx.sisterfix.service;

import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.domain.model.OrdenTrabajo;
import vnx.sisterfix.service.dto.ClienteResumenDTO;
import vnx.sisterfix.service.dto.EtiquetaDTO;
import vnx.sisterfix.service.dto.NuevaOrdenForm;
import vnx.sisterfix.service.dto.OrdenConsultaDTO;

import java.util.List;
import java.util.Optional;

public interface OrdenTrabajoService {

    OrdenTrabajo crearOrden(String dispositivo, String problema, Long clienteId);

    /** Registra un equipo reutilizando al cliente si ya existe (por telefono o email). */
    OrdenTrabajo registrarEquipo(NuevaOrdenForm form);

    Optional<EtiquetaDTO> obtenerEtiqueta(Long ordenId);

    Optional<ClienteResumenDTO> buscarCliente(String telefono, String email);

    Optional<OrdenConsultaDTO> buscarPorCodigoSeguimiento(String codigo);

    List<OrdenTrabajo> listarTodas();

    List<OrdenTrabajo> buscar(String texto);

    OrdenTrabajo actualizarEstado(Long ordenId, EstadoOrden nuevoEstado, String observaciones);
}