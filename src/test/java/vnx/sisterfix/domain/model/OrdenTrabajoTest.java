package vnx.sisterfix.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.exception.TransicionInvalidaException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrdenTrabajoTest {

    @Test
    void primerCambioDeEstadoRegistraEstadoAnteriorNulo() {
        OrdenTrabajo orden = new OrdenTrabajo();

        orden.cambiarEstado(EstadoOrden.RECIBIDO, "Ingreso del equipo.");

        assertThat(orden.getEstadoOrden()).isEqualTo(EstadoOrden.RECIBIDO);
        assertThat(orden.getHistorial()).hasSize(1);
        assertThat(orden.getHistorial().get(0).getEstadoAnterior()).isNull();
        assertThat(orden.getHistorial().get(0).getEstadoNuevo()).isEqualTo(EstadoOrden.RECIBIDO);
    }

    @Test
    void cambioValidoGuardaAnteriorNuevoYObservaciones() {
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.cambiarEstado(EstadoOrden.RECIBIDO, "Ingreso");

        orden.cambiarEstado(EstadoOrden.EN_DIAGNOSTICO, "Revisando placa");

        HistorialEstado ultimo = orden.getHistorial().get(1);
        assertThat(orden.getEstadoOrden()).isEqualTo(EstadoOrden.EN_DIAGNOSTICO);
        assertThat(ultimo.getEstadoAnterior()).isEqualTo(EstadoOrden.RECIBIDO);
        assertThat(ultimo.getEstadoNuevo()).isEqualTo(EstadoOrden.EN_DIAGNOSTICO);
        assertThat(ultimo.getObservaciones()).isEqualTo("Revisando placa");
    }

    @Test
    void cambiarAlMismoEstadoSeRechaza() {
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.cambiarEstado(EstadoOrden.EN_REPARACION, "x");

        assertThatThrownBy(() -> orden.cambiarEstado(EstadoOrden.EN_REPARACION, "otra vez"))
                .isInstanceOf(TransicionInvalidaException.class);
        assertThat(orden.getHistorial()).hasSize(1);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoOrden.class, names = {"ENTREGADO", "CANCELADO"})
    void unaOrdenCerradaNoAdmiteMasCambios(EstadoOrden estadoFinal) {
        OrdenTrabajo orden = new OrdenTrabajo();
        orden.setEstadoOrden(estadoFinal);

        assertThatThrownBy(() -> orden.cambiarEstado(EstadoOrden.RECIBIDO, "reabrir"))
                .isInstanceOf(TransicionInvalidaException.class);
        assertThat(orden.getEstadoOrden()).isEqualTo(estadoFinal);
        assertThat(orden.getHistorial()).isEmpty();
    }

    @Test
    void elNuevoEstadoEsObligatorio() {
        OrdenTrabajo orden = new OrdenTrabajo();

        assertThatThrownBy(() -> orden.cambiarEstado(null, "sin estado"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}