package vnx.sisterfix.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.domain.model.Cliente;
import vnx.sisterfix.domain.model.OrdenTrabajo;
import vnx.sisterfix.exception.OrdenNoEncontradaException;
import vnx.sisterfix.repository.IClienteRepository;
import vnx.sisterfix.repository.IOrdenTrabajoRepository;
import vnx.sisterfix.service.dto.NuevaOrdenForm;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdenTrabajoServiceImplTest {

    @Mock
    private IOrdenTrabajoRepository ordenRepository;

    @Mock
    private IClienteRepository clienteRepository;

    @InjectMocks
    private OrdenTrabajoServiceImpl servicio;

    private NuevaOrdenForm formulario(String telefono, String nombre, String email) {
        NuevaOrdenForm form = new NuevaOrdenForm();
        form.setTelefono(telefono);
        form.setNombre(nombre);
        form.setEmail(email);
        form.setDispositivo("Notebook ASUS");
        form.setProblemaReportado("No enciende");
        return form;
    }

    @Test
    void registrarEquipoReutilizaAlClienteExistente() {
        Cliente existente = new Cliente("Juan Pérez", "1123456789", null);
        when(clienteRepository.findFirstByTelefono("1123456789")).thenReturn(Optional.of(existente));
        when(ordenRepository.save(any(OrdenTrabajo.class))).thenAnswer(i -> i.getArgument(0));

        OrdenTrabajo orden = servicio.registrarEquipo(formulario("11 2345-6789", "Juan Pérez", null));

        assertThat(orden.getCliente()).isSameAs(existente);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    void registrarEquipoCreaAlClienteSiNoExiste() {
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));
        when(ordenRepository.save(any(OrdenTrabajo.class))).thenAnswer(i -> i.getArgument(0));

        OrdenTrabajo orden = servicio.registrarEquipo(formulario("1198765432", "  María Gómez ", "  MARIA@Correo.com "));

        assertThat(orden.getCliente().getNombre()).isEqualTo("María Gómez");
        assertThat(orden.getCliente().getTelefono()).isEqualTo("1198765432");
        assertThat(orden.getCliente().getEmail()).isEqualTo("maria@correo.com");
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void laOrdenNuevaNaceRecibidaConHistorialInicialYCodigo() {
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));
        when(ordenRepository.save(any(OrdenTrabajo.class))).thenAnswer(i -> i.getArgument(0));

        OrdenTrabajo orden = servicio.registrarEquipo(formulario("1198765432", "María", null));

        assertThat(orden.getEstadoOrden()).isEqualTo(EstadoOrden.RECIBIDO);
        assertThat(orden.getHistorial()).hasSize(1);
        assertThat(orden.getHistorial().get(0).getEstadoAnterior()).isNull();
        assertThat(orden.getCodigoSeguimiento()).matches("[A-Z0-9]{4}-[A-Z0-9]{4}");
    }

    @Test
    void siElCodigoYaExisteGeneraOtroHastaEncontrarUnoLibre() {
        when(ordenRepository.existsByCodigoSeguimiento(any(String.class))).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));
        when(ordenRepository.save(any(OrdenTrabajo.class))).thenAnswer(i -> i.getArgument(0));

        OrdenTrabajo orden = servicio.registrarEquipo(formulario("1198765432", "María", null));

        assertThat(orden.getCodigoSeguimiento()).isNotBlank();
    }

    @Test
    void actualizarEstadoDeUnaOrdenInexistenteLanzaOrdenNoEncontrada() {
        when(ordenRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.actualizarEstado(99L, EstadoOrden.EN_REPARACION, "x"))
                .isInstanceOf(OrdenNoEncontradaException.class);
    }
}