package vnx.sisterfix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vnx.sisterfix.domain.enums.Rol;
import vnx.sisterfix.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    List<Usuario> findAllByOrderByUsernameAsc();

    long countByRolAndActivoTrue(Rol rol);
}