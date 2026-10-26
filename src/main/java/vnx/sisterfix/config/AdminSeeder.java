package vnx.sisterfix.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import vnx.sisterfix.domain.enums.Rol;
import vnx.sisterfix.service.UsuarioService;

/**
 * Si no hay ningun usuario, crea el administrador inicial con las credenciales de las propiedades
 * (sisterfix.admin.usuario / sisterfix.admin.password). Despues se gestiona todo desde /admin/usuarios.
 */
@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UsuarioService usuarioService;
    private final String usuario;
    private final String password;

    public AdminSeeder(UsuarioService usuarioService,
                       @Value("${sisterfix.admin.usuario}") String usuario,
                       @Value("${sisterfix.admin.password}") String password) {
        this.usuarioService = usuarioService;
        this.usuario = usuario;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioService.contar() == 0) {
            usuarioService.crear(usuario, password, Rol.ADMIN);
            log.info("Administrador inicial '{}' creado.", usuario);
        }
    }
}