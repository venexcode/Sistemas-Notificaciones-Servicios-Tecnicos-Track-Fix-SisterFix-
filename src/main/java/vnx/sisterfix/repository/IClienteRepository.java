package vnx.sisterfix.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vnx.sisterfix.domain.model.Cliente;

import java.util.Optional;

public interface IClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);
}
