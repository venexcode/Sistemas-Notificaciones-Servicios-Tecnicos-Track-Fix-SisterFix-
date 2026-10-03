package vnx.sisterfix.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.domain.model.OrdenTrabajo;

import java.util.List;
import java.util.Optional;

public interface IOrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {
    // Consulta principal del cliente por su codigo unico
    Optional<OrdenTrabajo> findByCodigoSeguimiento(@NotBlank String codigoSeguimiento);

    // Panel de Administracion
    List<OrdenTrabajo> findByEstadoOrden(EstadoOrden estadoOrden);
}
