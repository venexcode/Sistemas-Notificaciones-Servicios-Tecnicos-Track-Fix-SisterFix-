package vnx.sisterfix.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.domain.util.GeneradorCodigoSeguimiento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"cliente", "historial"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "orden_trabajo")
public class OrdenTrabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "codigo_seguimiento", nullable = false, unique = true, length = 10)
    private String codigoSeguimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 30)
    private EstadoOrden estadoOrden;

    @NotBlank(message = "El equipo o dispositivo es obligatorio")
    private String dispositivo;

    @NotBlank(message = "El detalle del problema reportado es obligatorio")
    private String problemaReportado;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaUltimaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "ordenTrabajo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaCambio DESC")
    private List<HistorialEstado> historial = new ArrayList<>();

    // Callback JPA
    @PrePersist
    protected void asignarValoresIniciales() {
        LocalDateTime ahora = LocalDateTime.now();
        this.fechaCreacion = ahora;
        this.fechaUltimaActualizacion = ahora;

        if (this.estadoOrden == null) {
            this.estadoOrden = EstadoOrden.RECIBIDO;
        }

        if (this.codigoSeguimiento == null) {
            this.codigoSeguimiento = GeneradorCodigoSeguimiento.generar();
        }
    }

    @PreUpdate
    protected void actualizarFechaModificacion() {
        this.fechaUltimaActualizacion = LocalDateTime.now();
    }

    // Método de conveniencia para registrar cambio de estado
    public void cambiarEstado(EstadoOrden nuevoEstado, String observaciones) {
        HistorialEstado registro = new HistorialEstado(this.estadoOrden, nuevoEstado, observaciones, this);
        this.historial.add(registro);
        this.estadoOrden = nuevoEstado;
    }
}