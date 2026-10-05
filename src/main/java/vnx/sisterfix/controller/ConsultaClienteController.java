package vnx.sisterfix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vnx.sisterfix.service.OrdenTrabajoService;
import vnx.sisterfix.service.dto.OrdenConsultaDTO;

import java.util.Optional;

@Controller
@RequestMapping("/sisterfix")
public class ConsultaClienteController {

    private final OrdenTrabajoService ordenService;

    public ConsultaClienteController(OrdenTrabajoService ordenService) {
        this.ordenService = ordenService;
    }

    @GetMapping
    public String verPaginaConsulta() {
        // Plantilla en: src/main/resources/templates/sisterfix/buscar.html
        return "sisterfix/buscar";
    }

    @GetMapping("/consultar")
    public String consultarEstado(@RequestParam("codigo") String codigo, Model model) {
        Optional<OrdenConsultaDTO> ordenOpt = ordenService.buscarPorCodigoSeguimiento(codigo);

        if (ordenOpt.isPresent()) {
            model.addAttribute("orden", ordenOpt.get());
        } else {
            model.addAttribute("error", "No se encontró ninguna orden de trabajo con el código indicado.");
        }

        // Nota: no se devuelve el codigo ingresado sin escapar; Thymeleaf (th:text) ya lo escapa
        model.addAttribute("codigoIngresado", codigo);

        // Plantilla en: src/main/resources/templates/sisterfix/resultado.html
        return "sisterfix/resultado";
    }
}