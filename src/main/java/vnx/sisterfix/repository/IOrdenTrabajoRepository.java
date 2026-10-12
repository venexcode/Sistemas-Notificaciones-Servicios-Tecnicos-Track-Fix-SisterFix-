package vnx.sisterfix.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Todas, de la mas reciente a la mas antigua. Trae el cliente en la misma consulta (evita N+1)
    @EntityGraph(attributePaths = "cliente")
    List<OrdenTrabajo> findAllByOrderByFechaUltimaActualizacionDesc();

    // Busqueda del panel: codigo, equipo, nombre o telefono del cliente
    @EntityGraph(attributePaths = "cliente")
    @Query("""
            select o from OrdenTrabajo o join o.cliente c
            where lower(o.codigoSeguimiento) like lower(concat('%', :texto, '%'))
               or lower(o.dispositivo) like lower(concat('%', :texto, '%'))
               or lower(c.nombre) like lower(concat('%', :texto, '%'))
               or c.telefono like concat('%', :telefono, '%')
            order by o.fechaUltimaActualizacion desc
            """)
    List<OrdenTrabajo> buscar(@Param("texto") String texto, @Param("telefono") String telefono);
}