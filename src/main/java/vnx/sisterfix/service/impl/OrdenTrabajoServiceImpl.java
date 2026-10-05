package vnx.sisterfix.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.domain.model.Cliente;
import vnx.sisterfix.domain.model.OrdenTrabajo;
import vnx.sisterfix.repository.IClienteRepository;
import vnx.sisterfix.repository.IOrdenTrabajoRepository;
import vnx.sisterfix.service.OrdenTrabajoService;
import vnx.sisterfix.service.dto.OrdenConsultaDTO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdenTrabajoServiceImpl implements OrdenTrabajoService {

    private final IOrdenTrabajoRepository ordenTrabajoRepository;
    private final IClienteRepository clienteRepository;

    @Override
    @Transactional
    public OrdenTrabajo crearOrden(String dispositivo, String problema, Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));

        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setDispositivo(dispositivo);
        orden.setProblemaReportado(problema);
        orden.setCliente(cliente);
        // El codigo de seguimiento lo genera @PrePersist.
        // No se setea el estado antes: asi el primer historial tiene estadoAnterior = null.
        orden.cambiarEstado(EstadoOrden.RECIBIDO, "Ingreso del equipo al taller.");

        return ordenTrabajoRepository.save(orden);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrdenConsultaDTO> buscarPorCodigoSeguimiento(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return Optional.empty();
        }

        return ordenTrabajoRepository.findByCodigoSeguimiento(codigo.trim().toUpperCase())
                .map(orden -> OrdenConsultaDTO.builder()
                        .codigoSeguimiento(orden.getCodigoSeguimiento())
                        .estadoActual(orden.getEstadoOrden())
                        .dispositivo(orden.getDispositivo())
                        .problemaReportado(orden.getProblemaReportado())
                        .nombreCliente(orden.getCliente().getNombre())
                        .fechaCreacion(orden.getFechaCreacion())
                        .historial(orden.getHistorial().stream()
                                .map(h -> OrdenConsultaDTO.HistorialItemDTO.builder()
                                        .estadoNuevo(h.getEstadoNuevo())
                                        .fechaCambio(h.getFechaCambio())
                                        .observaciones(h.getObservaciones())
                                        .build())
                                .toList())
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenTrabajo> listarTodas() {
        return ordenTrabajoRepository.findAll();
    }

    @Override
    @Transactional
    public OrdenTrabajo actualizarEstado(Long ordenId, EstadoOrden nuevoEstado, String observaciones) {
        OrdenTrabajo orden = ordenTrabajoRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada con ID: " + ordenId));

        // cambiarEstado valida la transicion y lanza IllegalStateException si no es valida
        orden.cambiarEstado(nuevoEstado, observaciones);
        return ordenTrabajoRepository.save(orden);
    }
}