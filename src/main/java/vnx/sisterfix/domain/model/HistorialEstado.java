package vnx.sisterfix.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vnx.sisterfix.domain.enums.EstadoOrden;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_estados")
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoOrden estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOrden estadoNuevo;

    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fechaCambio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_trabajo_id",  nullable = false)
    private OrdenTrabajo ordenTrabajo;

    public HistorialEstado() {}

    // Constructor
    public HistorialEstado(EstadoOrden estadoAnterior, EstadoOrden estadoNuevo, String observaciones, OrdenTrabajo ordenTrabajo) {
            this.estadoAnterior = estadoAnterior;
            this.estadoNuevo = estadoNuevo;
            this.observaciones = observaciones;
            this.fechaCambio = LocalDateTime.now();
            this.ordenTrabajo = ordenTrabajo;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public EstadoOrden getEstadoAnterior() {
        return estadoAnterior;
    }

    public EstadoOrden getEstadoNuevo() {
        return estadoNuevo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public OrdenTrabajo getOrdenTrabajo() {
        return ordenTrabajo;
    }
}
