package vnx.sisterfix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.exception.OrdenNoEncontradaException;
import vnx.sisterfix.exception.TransicionInvalidaException;
import vnx.sisterfix.service.OrdenTrabajoService;

@Controller
@RequestMapping("/admin/ordenes")
public class AdminOrderController {

    private final OrdenTrabajoService ordenService;

    public AdminOrderController(OrdenTrabajoService ordenService) {
        this.ordenService = ordenService;
    }

    @GetMapping
    public String listarOrdenes(
            @RequestParam(value = "q", required = false, defaultValue = "") String q,
            @RequestParam(value = "etapa", required = false, defaultValue = "") String etapa,
            Model model) {

        String texto = q.trim();
        model.addAttribute("ordenes", texto.isEmpty() ? ordenService.listarTodas() : ordenService.buscar(texto));
        model.addAttribute("q", texto);
        model.addAttribute("etapa", etapa);
        return "admin/lista-ordenes";
    }

    @PostMapping("/{id}/actualizar-estado")
    public String actualizarEstado(
            @PathVariable("id") Long id,
            @RequestParam("nuevoEstado") EstadoOrden nuevoEstado,
            @RequestParam(value = "observaciones", required = false, defaultValue = "") String observaciones,
            RedirectAttributes redirectAttributes) {

        try {
            ordenService.actualizarEstado(id, nuevoEstado, observaciones);
            redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado correctamente.");
        } catch (OrdenNoEncontradaException | TransicionInvalidaException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/ordenes";
    }
}