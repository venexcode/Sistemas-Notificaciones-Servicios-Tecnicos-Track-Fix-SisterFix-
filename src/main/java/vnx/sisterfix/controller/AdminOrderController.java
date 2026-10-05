package vnx.sisterfix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.service.OrdenTrabajoService;

@Controller
@RequestMapping("/admin/ordenes")
public class AdminOrderController {

    private final OrdenTrabajoService ordenService;

    public AdminOrderController(OrdenTrabajoService ordenService) {
        this.ordenService = ordenService;
    }

    @GetMapping
    public String listarOrdenes(Model model) {
        model.addAttribute("ordenes", ordenService.listarTodas());
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
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/ordenes";
    }
}