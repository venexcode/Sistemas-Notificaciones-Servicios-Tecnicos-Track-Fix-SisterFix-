package vnx.sisterfix.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import vnx.sisterfix.domain.model.OrdenTrabajo;
import vnx.sisterfix.domain.util.QRSVG;
import vnx.sisterfix.service.OrdenTrabajoService;
import vnx.sisterfix.service.dto.ClienteResumenDTO;
import vnx.sisterfix.service.dto.EtiquetaDTO;
import vnx.sisterfix.service.dto.NuevaOrdenForm;

import java.util.Optional;

/** Registro de equipos nuevos, etiqueta imprimible y busqueda de clientes. Todo bajo /admin (protegido). */
@Controller
@RequestMapping("/admin")
public class AdminRegistroController {

    private final OrdenTrabajoService ordenService;
    private final String urlPublica;

    public AdminRegistroController(OrdenTrabajoService ordenService,
                                   @Value("${sisterfix.url-publica:}") String urlPublica) {
        this.ordenService = ordenService;
        this.urlPublica = urlPublica;
    }

    @GetMapping("/ordenes/nueva")
    public String nueva(Model model) {
        model.addAttribute("form", new NuevaOrdenForm());
        return "admin/nueva-orden";
    }

    @PostMapping("/ordenes/nueva")
    public String registrar(@Valid @ModelAttribute("form") NuevaOrdenForm form, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return "admin/nueva-orden";
        }
        OrdenTrabajo orden = ordenService.registrarEquipo(form);
        // Redirect (y no render directo): recargar la etiqueta no vuelve a registrar el equipo
        return "redirect:/admin/ordenes/" + orden.getId() + "/etiqueta";
    }

    @GetMapping("/ordenes/{id}/etiqueta")
    public String etiqueta(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<EtiquetaDTO> etiqueta = ordenService.obtenerEtiqueta(id);
        if (etiqueta.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No encontramos esa orden.");
            return "redirect:/admin/ordenes";
        }

        String base = urlBase() + "/sisterfix";
        String urlSeguimiento = UriComponentsBuilder.fromUriString(base + "/consultar")
                .queryParam("codigo", etiqueta.get().getCodigoSeguimiento())
                .build().toUriString();

        model.addAttribute("etiqueta", etiqueta.get());
        model.addAttribute("urlBase", base.replaceFirst("^https?://", ""));
        model.addAttribute("qrSvg", QRSVG.generar(urlSeguimiento));
        return "admin/etiqueta";
    }

    /** Lo usa el formulario para detectar clientes ya registrados mientras se escribe. */
    @GetMapping("/clientes/buscar")
    @ResponseBody
    public ResponseEntity<ClienteResumenDTO> buscarCliente(
            @RequestParam(value = "telefono", required = false, defaultValue = "") String telefono,
            @RequestParam(value = "email", required = false, defaultValue = "") String email) {

        return ordenService.buscarCliente(telefono, email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /**
     * Direccion publica del sitio. En desarrollo se toma de la peticion (localhost);
     * para que el QR funcione desde un celular, define sisterfix.url-publica.
     */
    private String urlBase() {
        if (urlPublica != null && !urlPublica.isBlank()) {
            return urlPublica.replaceAll("/+$", "");
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }
}