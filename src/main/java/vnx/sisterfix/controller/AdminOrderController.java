package vnx.sisterfix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vnx.sisterfix.domain.enums.EstadoOrden;
import vnx.sisterfix.service.OrdenTrabajoService;

@Controller
@RequestMapping
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
            @RequestParam("observaciones") String observaciones) {

        ordenService.actualizarEstado(id, nuevoEstado, observaciones);
        return "redirect:/admin/ordenes";
    }
}
