package vnx.sisterfix.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vnx.sisterfix.domain.model.OrdenTrabajo;
import vnx.sisterfix.service.OrdenTrabajoService;

import java.util.Optional;

@Controller
@RequestMapping("/templates/sisterfix/sisterfix")
public class ConsultaClienteController {

    private final OrdenTrabajoService ordenService;

    public ConsultaClienteController(OrdenTrabajoService ordenService) {
        this.ordenService = ordenService;
    }

    @GetMapping
    public String verPaginaConsulta() {
        return "templates/sisterfix/sisterfix/buscar";
    }

    @GetMapping("/consultar")
    public String consultarEstado(@RequestParam("codigo") String codigo, Model model) {
        Optional<OrdenTrabajo> ordenOpt = ordenService.buscarPorCodigoSeguimiento(codigo);

        if (ordenOpt.isPresent()) {
            model.addAttribute("orden", ordenOpt.get());
        } else {
            model.addAttribute("error", "No se encontró ninguna orden de trabajo con el código: " + codigo);
        }
        model.addAttribute("codigoIngresado", codigo);
        return "templates/sisterfix/sisterfix/resultado";
    }
}
