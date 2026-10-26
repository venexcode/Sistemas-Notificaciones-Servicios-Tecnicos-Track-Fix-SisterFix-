package vnx.sisterfix.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vnx.sisterfix.domain.enums.Rol;
import vnx.sisterfix.domain.model.Usuario;
import vnx.sisterfix.repository.IUsuarioRepository;

import java.util.List;

/** Gestiona los usuarios del panel y le dice a Spring Security como autenticarlos. */
@Service
public class UsuarioService implements UserDetailsService {

    private static final int PASSWORD_MINIMA = 8;
    private static final int PASSWORD_MAXIMA = 72; // limite de BCrypt

    private final IUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(IUsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(normalizar(username))
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .roles(usuario.getRol().name())
                .disabled(!usuario.isActivo())
                .build();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAllByOrderByUsernameAsc();
    }

    @Transactional(readOnly = true)
    public long contar() {
        return usuarioRepository.count();
    }

    @Transactional
    public Usuario crear(String username, String password, Rol rol) {
        String nombre = normalizar(username);
        if (nombre.length() < 3) {
            throw new IllegalArgumentException("El nombre de usuario debe tener al menos 3 caracteres.");
        }
        validarPassword(password);
        if (usuarioRepository.existsByUsername(nombre)) {
            throw new IllegalArgumentException("Ese nombre de usuario ya existe.");
        }
        return usuarioRepository.save(new Usuario(nombre, passwordEncoder.encode(password), rol));
    }

    @Transactional
    public void cambiarActivo(Long id, String usuarioActual) {
        Usuario usuario = buscar(id);

        if (usuario.getUsername().equalsIgnoreCase(usuarioActual)) {
            throw new IllegalArgumentException("No puedes desactivar tu propio usuario.");
        }
        boolean desactivando = usuario.isActivo();
        if (desactivando && usuario.getRol() == Rol.ADMIN
                && usuarioRepository.countByRolAndActivoTrue(Rol.ADMIN) <= 1) {
            throw new IllegalArgumentException("Debe quedar al menos un administrador activo.");
        }
        usuario.setActivo(!usuario.isActivo());
    }

    @Transactional
    public void cambiarPassword(Long id, String nuevaPassword) {
        validarPassword(nuevaPassword);
        buscar(id).setPasswordHash(passwordEncoder.encode(nuevaPassword));
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
    }

    private static void validarPassword(String password) {
        if (password == null || password.length() < PASSWORD_MINIMA || password.length() > PASSWORD_MAXIMA) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener entre " + PASSWORD_MINIMA + " y " + PASSWORD_MAXIMA + " caracteres.");
        }
    }

    private static String normalizar(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }
}