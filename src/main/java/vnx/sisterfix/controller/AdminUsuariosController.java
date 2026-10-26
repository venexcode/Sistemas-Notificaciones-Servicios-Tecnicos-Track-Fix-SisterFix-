package vnx.sisterfix.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vnx.sisterfix.domain.enums.Rol;
import vnx.sisterfix.service.UsuarioService;
import vnx.sisterfix.service.dto.NuevoUsuarioForm;

/** Gestion de usuarios del panel. Solo para ADMIN (lo exige SecurityConfig). */
@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuariosController {

    private final UsuarioService usuarioService;

    public AdminUsuariosController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ModelAttribute("roles")
    public Rol[] roles() {
        return Rol.values();
    }

    @GetMapping
    public String listar(Model model, Authentication autenticacion) {
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new NuevoUsuarioForm());
        }
        cargarListado(model, autenticacion);
        return "admin/usuarios";
    }

    @PostMapping
    public String crear(@Valid @ModelAttribute("form") NuevoUsuarioForm form, BindingResult resultado,
                        Model model, Authentication autenticacion, RedirectAttributes redirectAttributes) {
        if (!resultado.hasErrors()) {
            try {
                usuarioService.crear(form.getUsername(), form.getPassword(), form.getRol());
                redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente.");
                return "redirect:/admin/usuarios";
            } catch (IllegalArgumentException e) {
                resultado.rejectValue("username", "usuario.duplicado", e.getMessage());
            }
        }
        cargarListado(model, autenticacion);
        return "admin/usuarios";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable("id") Long id, Authentication autenticacion,
                                RedirectAttributes redirectAttributes) {
        try {
            usuarioService.cambiarActivo(id, autenticacion.getName());
            redirectAttributes.addFlashAttribute("mensaje", "Estado del usuario actualizado.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/password")
    public String cambiarPassword(@PathVariable("id") Long id,
                                  @RequestParam("nuevaPassword") String nuevaPassword,
                                  RedirectAttributes redirectAttributes) {
        try {
            usuarioService.cambiarPassword(id, nuevaPassword);
            redirectAttributes.addFlashAttribute("mensaje", "Contraseña actualizada.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    private void cargarListado(Model model, Authentication autenticacion) {
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("actual", autenticacion.getName());
    }
}