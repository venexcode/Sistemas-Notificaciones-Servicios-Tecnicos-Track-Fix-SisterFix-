package vnx.sisterfix.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.domain.model.Cliente;
import vnx.sisterfix.domain.model.OrdenTrabajo;
import vnx.sisterfix.exception.OrdenNoEncontradaException;
import vnx.sisterfix.repository.IClienteRepository;
import vnx.sisterfix.repository.IOrdenTrabajoRepository;
import vnx.sisterfix.service.OrdenTrabajoService;
import vnx.sisterfix.service.dto.ClienteResumenDTO;
import vnx.sisterfix.service.dto.EtiquetaDTO;
import vnx.sisterfix.service.dto.NuevaOrdenForm;
import vnx.sisterfix.service.dto.OrdenConsultaDTO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdenTrabajoServiceImpl implements OrdenTrabajoService {

    private static final int TELEFONO_MINIMO = 6;

    private final IOrdenTrabajoRepository ordenTrabajoRepository;
    private final IClienteRepository clienteRepository;

    // ---------- Alta ----------

    @Override
    @Transactional
    public OrdenTrabajo crearOrden(String dispositivo, String problema, Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + clienteId));
        return nuevaOrden(cliente, dispositivo, problema);
    }

    @Override
    @Transactional
    public OrdenTrabajo registrarEquipo(NuevaOrdenForm form) {
        String telefono = normalizarTelefono(form.getTelefono());
        String email = normalizarEmail(form.getEmail());

        Cliente cliente = localizarCliente(telefono, email).orElseGet(() ->
                clienteRepository.save(new Cliente(form.getNombre().trim(), telefono, email)));

        // Si el cliente ya existia sin email y ahora lo tenemos, lo completamos
        if (cliente.getEmail() == null && email != null) {
            cliente.setEmail(email);
        }

        return nuevaOrden(cliente, form.getDispositivo().trim(), form.getProblemaReportado().trim());
    }

    private OrdenTrabajo nuevaOrden(Cliente cliente, String dispositivo, String problema) {
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setDispositivo(dispositivo);
        orden.setProblemaReportado(problema);
        orden.setCliente(cliente);
        // El codigo de seguimiento lo genera @PrePersist. El estado inicial queda en el historial.
        orden.cambiarEstado(EstadoOrden.RECIBIDO, "Ingreso del equipo al taller.");
        return ordenTrabajoRepository.save(orden);
    }

    // ---------- Clientes ----------

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteResumenDTO> buscarCliente(String telefono, String email) {
        return localizarCliente(normalizarTelefono(telefono), normalizarEmail(email))
                .map(c -> new ClienteResumenDTO(c.getNombre(), c.getTelefono(), c.getEmail()));
    }

    /** Primero por telefono; si no aparece, por email. */
    private Optional<Cliente> localizarCliente(String telefono, String email) {
        Optional<Cliente> encontrado = telefono.length() >= TELEFONO_MINIMO
                ? clienteRepository.findFirstByTelefono(telefono)
                : Optional.empty();

        if (encontrado.isEmpty() && email != null) {
            encontrado = clienteRepository.findFirstByEmailIgnoreCase(email);
        }
        return encontrado;
    }

    /** Deja solo digitos y el + inicial: "11 2345-6789" y "1123456789" son el mismo cliente. */
    private static String normalizarTelefono(String telefono) {
        return telefono == null ? "" : telefono.replaceAll("[^0-9+]", "");
    }

    private static String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    // ---------- Consultas ----------

    @Override
    @Transactional(readOnly = true)
    public Optional<EtiquetaDTO> obtenerEtiqueta(Long ordenId) {
        return ordenTrabajoRepository.findById(ordenId)
                .map(o -> EtiquetaDTO.builder()
                        .id(o.getId())
                        .codigoSeguimiento(o.getCodigoSeguimiento())
                        .dispositivo(o.getDispositivo())
                        .problemaReportado(o.getProblemaReportado())
                        .nombreCliente(o.getCliente().getNombre())
                        .telefonoCliente(o.getCliente().getTelefono())
                        .fechaCreacion(o.getFechaCreacion())
                        .build());
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
        return ordenTrabajoRepository.findAllByOrderByFechaUltimaActualizacionDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrdenTrabajo> buscar(String texto) {
        String limpio = texto == null ? "" : texto.trim();
        if (limpio.isEmpty()) {
            return listarTodas();
        }
        // Si el texto no tiene digitos, el filtro por telefono no debe coincidir con todo
        String telefono = normalizarTelefono(limpio);
        return ordenTrabajoRepository.buscar(limpio, telefono.isEmpty() ? "@@" : telefono);
    }

    // ---------- Cambios de estado ----------

    @Override
    @Transactional
    public OrdenTrabajo actualizarEstado(Long ordenId, EstadoOrden nuevoEstado, String observaciones) {
        OrdenTrabajo orden = ordenTrabajoRepository.findById(ordenId)
                .orElseThrow(() -> new OrdenNoEncontradaException(ordenId));

        // cambiarEstado valida la transicion y lanza IllegalStateException si no es valida
        orden.cambiarEstado(nuevoEstado, observaciones);
        return ordenTrabajoRepository.save(orden);
    }
}