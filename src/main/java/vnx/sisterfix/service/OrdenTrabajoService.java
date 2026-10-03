package vnx.sisterfix.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.domain.model.*;
import vnx.sisterfix.repository.*;
import vnx.sisterfix.domain.util.GeneradorCodigoSeguimiento;

import java.util.List;
import java.util.Optional;

@Service
public class OrdenTrabajoService {

    private final IOrdenTrabajoRepository ordenRepository;
    private final IClienteRepository clienteRepository;

    public OrdenTrabajoService(IOrdenTrabajoRepository ordenRepository, IClienteRepository clienteRepository) {
        this.ordenRepository = ordenRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public OrdenTrabajo crearOrden(String dispositivo, String problema, Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));

        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setDispositivo(dispositivo);
        orden.setProblemaReportado(problema);
        orden.setCliente(cliente);
        orden.setCodigoSeguimiento(GeneradorCodigoSeguimiento.generar());
        orden.setEstadoOrden(EstadoOrden.RECIBIDO);

        // Registro del historial inicial
        orden.cambiarEstado(EstadoOrden.RECIBIDO, "Ingreso del equipo al taller.");

        return ordenRepository.save(orden);
    }

    @Transactional(readOnly = true)
    public Optional<OrdenTrabajo> buscarPorCodigoSeguimiento(String codigo) {
        return ordenRepository.findByCodigoSeguimiento(codigo.toUpperCase().trim());
    }

    @Transactional(readOnly = true)
    public List<OrdenTrabajo> listarTodas() {
        return ordenRepository.findAll();
    }

    @Transactional
    public OrdenTrabajo actualizarEstado(Long ordenId, EstadoOrden nuevoEstado, String observaciones) {
        OrdenTrabajo orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada con ID: " + ordenId));

        orden.cambiarEstado(nuevoEstado, observaciones);
        return ordenRepository.save(orden);
    }
}
