package vnx.sisterfix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vnx.sisterfix.domain.model.Cliente;

import java.util.Optional;

public interface IClienteRepository extends JpaRepository<Cliente, Long> {

    // "First": el email no es unico, asi que puede haber mas de un resultado
    Optional<Cliente> findFirstByEmailIgnoreCase(String email);

    Optional<Cliente> findFirstByTelefono(String telefono);
}