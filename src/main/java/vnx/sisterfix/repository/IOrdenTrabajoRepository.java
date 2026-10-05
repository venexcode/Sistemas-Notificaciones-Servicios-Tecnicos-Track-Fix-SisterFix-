package vnx.sisterfix.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.domain.model.OrdenTrabajo;

import java.util.List;
import java.util.Optional;

public interface IOrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {

    // Consulta principal del cliente por su codigo unico
    Optional<OrdenTrabajo> findByCodigoSeguimiento(String codigoSeguimiento);

    // Panel de Administracion
    @EntityGraph(attributePaths = "cliente")
    List<OrdenTrabajo> findByEstadoOrden(EstadoOrden estadoOrden);

    // Evita el problema N+1 al listar y mostrar el nombre del cliente
    @Override
    @EntityGraph(attributePaths = "cliente")
    List<OrdenTrabajo> findAll();
}